package com.esamir;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

			//Single case.
			if (value instanceof XCacheObject) {
				XCacheObject xcacheObject = (XCacheObject) value;
				xcacheObject.populateCacheMap(xcacheObjectData);
			} else if (value instanceof Collection) {
				logger.debug("found a collection and I'll fix it up like a collection");

				Iterator<XCacheComplexObject> xcacheObjectsIter = ((Collection<XCacheComplexObject>) value).iterator();
				JSONObject mapper = new JSONObject();

				while (xcacheObjectsIter.hasNext()) {
					XCacheComplexObject xcacheComplexDataObject = xcacheObjectsIter.next();
					xcacheObjectData.put(xcacheComplexDataObject.getCacheId(), xcacheComplexDataObject.serialize(mapper));
					mapper.clear();
				}
				return xcacheObjectData;
			} else if (value instanceof Map) {
				Map map_values = (Map) value;

				if (map_values.keySet().size() == 0)
					return null;

				JSONObject jsonMap = new JSONObject();
				JSONArray jsonArray = new JSONArray();


				Iterator it = map_values.values().iterator();
				Object item_value = it.next();

				if ((item_value instanceof Map) || (item_value instanceof Collection)) {
					// Call yourself.
				} else if (item_value instanceof XCacheComplexObject) {
					JSONObject mapper = new JSONObject();
					xcacheObjectData.put(((XCacheComplexObject) item_value).getCacheId(), ((XCacheComplexObject) item_value).serialize(mapper));
				}
				logger.debug("Value type is: " + item_value.getClass().getName());

			} else {
				logger.debug(value.getClass().getName());
				logger.debug("I did not find an XCache Object nor a collection");
			}

			final String compositeKey = getCompositeKey(key);

			// xCacheCassandraManager.updateRow(compositeKey, xcacheObjectData);
			logger.info("Inserted " + objectName + " into Cassandra: " + compositeKey);

			return xcacheObjectData;
		} else {
			return null;
		}
	}

	public static JSONObject serializeComplex(Object value) {
		if (value instanceof Map) {

			Map map_value = (Map) value;
			JSONArray jsonarray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			Iterator it = map_value.values().iterator();
			Object item_value = it.next();

			if (item_value instanceof XCacheComplexObject)  // Simple? Complex Object
			{
				Iterator keys_it = map_value.keySet().iterator();

				while (keys_it.hasNext()) {
					JSONObject mapper = new JSONObject();
					Object map_key = keys_it.next();
					XCacheComplexObject entry = (XCacheComplexObject) map_value.get(map_key);
					logger.info(entry.serialize(mapper));
					mapper.put("MAP_KEY_TYPE", map_key.getClass().getName());
					mapper.put("MAP_KEY", map_key);
					jsonarray.add(mapper);
				}
				jsonObject.put("MAP_ITEM_VALUE", jsonarray);
				return jsonObject;


			} else if (item_value instanceof Map) {  // HashMap
				it = map_value.values().iterator();
				logger.info("in Map: size of map is:" + ((Map) item_value).size());
				while (it.hasNext()) {
					item_value = it.next();
					serializeComplex(item_value);
				}

			} else if (item_value instanceof Collection) {  //ie. List.

			}


		} else if (value instanceof XCacheComplexObject) {
			XCacheComplexObject complex = (XCacheComplexObject) value;
			JSONObject jsonvalue = new JSONObject();
			String payload = complex.serialize(jsonvalue);
			return jsonvalue;

		} else {
			logger.error("Unsupported key detected");
		}

		return null;
	}

	public static Map buildMapInstance() {
		Utility util = Utility.getInstance();
		Map parent_map = new HashMap();

		for (int i = 0; i < 5; i++) {

			Long key_l1 = util.getRandomLong();
			Map a = new HashMap();
			for (int y = 0; y < 5; y++) {

				Long key_l2 = util.getRandomLong();

				Map b = new HashMap();
				a.put(key_l2, b);
				for (int z = 0; z < 3; z++) {

					String key_l3 = util.getRandomString();
					Seller obj = Seller.getRandomInstance();
					b.put(key_l3, obj);
				}
			}
			parent_map.put(key_l1, a);

		}

		return parent_map;
	}

	public static void main(String[] args) {
		Map serializeMe = buildMapInstance();
		Map simpleMap = new HashMap();
		simpleMap.put(1, Seller.getRandomInstance());
		simpleMap.put(2, Seller.getRandomInstance());
		simpleMap.put(3, Seller.getRandomInstance());
		simpleMap.put(4, Seller.getRandomInstance());
		simpleMap.put(5, Seller.getRandomInstance());
		Map secondLevel = new HashMap();
		secondLevel.put("69", simpleMap);

		JSONObject foobar = serializeComplex(simpleMap);


		//build
		for (int i = 0; i < 10; i++) {
			Double a = Math.pow(i, 2.0);

		}
		logger.info("woot");


	}

}
