package com.esamir;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

	private static final String MAP_KEY = "MAP_KEY";
	private static final String MAP_KEY_TYPE = "MAP_KEY_TYPE";
	private static final String CONTAINER_TYPE = "CONTAINER_TYPE";
	private static final String ENTRY_VALUE = "SERIALIZED_VALUE";
	private static final String COMPLEX_OBJECT_ENTRY = "COMPLEX_OBJECT_ENTRY";

	public static Object put2(Object key, Object value) {

		if (key instanceof Long) {
			Map<String, Object> xcacheObjectData = new HashMap<String, Object>();

			//Single case.
			if (value instanceof XCacheObject) {
				XCacheObject xcacheObject = (XCacheObject) value;
				xcacheObject.populateCacheMap(xcacheObjectData);
			} else if (value instanceof Map || value instanceof Collection) {
				xcacheObjectData.put(COMPLEX_OBJECT_ENTRY, ((JSONObject)serializeComplex(value)).toJSONString());
			} else {
				logger.debug(value.getClass().getName());
				logger.debug("I did not find an XCache Object nor a collection");
			}

			final String compositeKey = getCompositeKey(key);
			logger.info("Inserted " + objectName + " into Cassandra: " + compositeKey);

			return xcacheObjectData;
		} else {
			return null;
		}
	}

	public static Object serializeComplex(Object value) {

		if (value instanceof Map)  //Main container is a map
		{

			Map map = (Map) value;
			JSONArray jsonarray = new JSONArray();
			//Iterator it = map.values().iterator();
			Iterator keys_it = map.keySet().iterator();
			//
			while (keys_it.hasNext()) {
				Object map_key = keys_it.next();
				JSONObject jsonMapEntry = new JSONObject();
				jsonMapEntry.put(MAP_KEY, map_key);
				jsonMapEntry.put(MAP_KEY_TYPE, map_key.getClass().getName());
				Object map_value = map.get(map_key);
				jsonMapEntry.put(ENTRY_VALUE, serializeComplex(map_value));
				jsonMapEntry.put(CONTAINER_TYPE, "MAP");
				jsonarray.add(jsonMapEntry);
			}
			return jsonarray;

		} else if (value instanceof XCacheComplexObject) {
			XCacheComplexObject complex = (XCacheComplexObject) value;
			JSONObject jsonvalue = new JSONObject();
			String payload = complex.serialize(jsonvalue);
			return jsonvalue;


		} else if (value instanceof Collection) {
			JSONArray jsonarray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonarray.clear();

			Collection item_collection = (Collection) value;
			Iterator item_iterator = item_collection.iterator();
			Object obj = item_iterator.next();
			while (item_iterator.hasNext()) {
				jsonarray.add(serializeComplex(obj));
				obj = item_iterator.next();
			}
			return jsonarray;

		} else {
			logger.error("Unsupported key detected");
		}

		return new JSONArray();
	}

	public static List deserializeComplex(String payload) {

		JSONParser parser = new JSONParser();
		try {

			Object oPayload =  parser.parse(payload);
			if(oPayload instanceof JSONArray)
			{
				JSONArray data = (JSONArray) oPayload;
				logger.info("Detected a json Array");
				for(int i=0; i < data.size(); i++)
				{
					JSONObject values = (JSONObject) data.get(i);
					if(values.containsKey(MAP_KEY)) {
						//treat as a map.
					}
					int z= 0;

				}

			}
			else if(oPayload instanceof JSONObject) {
				logger.info("Detected a json Object");

			}

		} catch (ParseException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		return new ArrayList();
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
		//Map serializeMe = buildMapInstance();
		//JSONObject foobar = serializeComplex(serializeMe);


		Map simpleMap = new HashMap();
		Map simpleMap2 = new HashMap();
		Map simpleMap3 = new HashMap();

		for (int i = 0; i < 5; i++) {
			simpleMap.put(i, Seller.getRandomInstance());
			simpleMap2.put(i * 3, Seller.getRandomInstance());
			simpleMap3.put(i * 4, Seller.getRandomInstance());
		}

		Map secondLevel = new HashMap();
		List list = new ArrayList();
		List list2 = new ArrayList();
		for (int i = 0; i < 10; i++) {
			list.add(Seller.getRandomInstance());
			list2.add(Seller.getRandomInstance());
		}
		secondLevel.put(5, list);
		secondLevel.put(7, list2);
		secondLevel.put("69", simpleMap);
		secondLevel.put("96", simpleMap3);
		secondLevel.put(23, Seller.getRandomInstance());
		//secondLevel.put("23", simpleMap2);
		//secondLevel.put("34", simpleMap3);


		JSONArray foobar = (JSONArray) serializeComplex(list);
		//JSONObject foobar = serializeComplex(simpleMap2);
		String buffer = foobar.toJSONString();
		logger.info("woot:  " + buffer);
		deserializeComplex(buffer);


		//build
		for (int i = 0; i < 10; i++) {
			Double a = Math.pow(i, 2.0);

		}


	}

}
