package com.esamir;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * User: sfaci
 * Date: 6/4/12
 * Time: 12:26 PM
 */
public class Main {
	static Logger logger = Logger.getLogger(Main.class);
	static JSONArray jsonArray = new JSONArray();
	static private final String objectName = Seller.class.getName();


	static {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	static private String getCompositeKey(Object key) {
		return key + ":" + objectName;
	}


	public static Object put2(Object key, Object value) {
		if (key instanceof Long) {
			Map<String, Object> xcacheObjectData = new HashMap<String, Object>();
			if (value instanceof Collection) {
				logger.debug("found a collection and I'll fix it up like a collection");
				Iterator<XCacheComplexObject> xcacheObjectsIter = ((Collection<XCacheComplexObject>) value).iterator();
				JSONObject mapper = new JSONObject();
				while (xcacheObjectsIter.hasNext()) {
					XCacheComplexObject xcacheComplexDataObject = xcacheObjectsIter.next();
					xcacheObjectData.put(xcacheComplexDataObject.getCacheId(), xcacheComplexDataObject.serialize(mapper));
					mapper.clear();
				}
			} else if (value instanceof Map) {
				logger.debug("found a HashMap of ISellerProgramRecords");
				Map values = (Map) value;
				if (values.keySet().size() == 0) {
					return null;
				}
				Set keys = values.keySet();
				Iterator it = keys.iterator();
				Object map_key = it.next();
				Object a_value = values.get(map_key);
				if (a_value instanceof Map || a_value instanceof Collection) {
					//Call yourself.
				}

				logger.debug("Hash Map is keyed by: " + map_key.getClass().getName());
				logger.debug("Value type is: " + a_value.getClass().getName());


			} else {
				logger.debug(value.getClass().getName());
				logger.debug("I did not find an XCache Object nor a collection");
			}
			final String compositeKey = getCompositeKey(key);
			//xCacheCassandraManager.updateRow(compositeKey, xcacheObjectData);
			logger.info("Inserted " + objectName + " into Cassandra: " + compositeKey);

			return value;
		} else {
			return null;
		}
	}

	public static Map buildMapInstance() {
		Utility util = Utility.getInstance();
		Map map = new HashMap();
		Long key_l1 = util.getRandomLong();
		Long key_l2 = util.getRandomLong();
		String key_l3 = util.getRandomString();

		return map;
	}


	public static void main(String[] args) {
		Map serializeMe = buildMapInstance();

		//build
		for (int i = 0; i < 10; i++) {
			Double a = Math.pow(i, 2.0);

		}


	}

}
