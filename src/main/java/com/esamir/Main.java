package com.esamir;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.org.mozilla.javascript.internal.Interpreter;

import java.io.*;
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
            JSONObject jsonObject = new JSONObject();

			Map map = (Map) value;
			//Iterator it = map.values().iterator();
			Iterator keys_it = map.keySet().iterator();
            JSONArray jsonarray = new JSONArray();
			while (keys_it.hasNext()) {
				Object map_key = keys_it.next();
				JSONObject jsonMapEntry = new JSONObject();
				Object map_value = map.get(map_key);
                JSONObject jsonValue = (JSONObject) serializeComplex(map_value);

                jsonMapEntry.put(ENTRY_VALUE, jsonValue);
                jsonMapEntry.put(MAP_KEY, map_key);
                jsonMapEntry.put(MAP_KEY_TYPE, map_key.getClass().getName());
                //jsonMapEntry.put(MAP_KEY, map_key);
                //jsonMapEntry.put(MAP_KEY_TYPE, map_key.getClass().getName());
				//jsonMapEntry.put(CONTAINER_TYPE, Map.class.getName());
				jsonarray.add(jsonMapEntry);
			}
            jsonObject.put(ENTRY_VALUE, jsonarray);
            jsonObject.put(CONTAINER_TYPE, Map.class.getName());

            return jsonObject;

		} else if (value instanceof XCacheComplexObject) {
			XCacheComplexObject complex = (XCacheComplexObject) value;
			JSONObject jsonvalue = new JSONObject();
			String payload = complex.serialize(jsonvalue);
			return jsonvalue;


		} else if (value instanceof Collection) {
			JSONArray jsonarray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonarray.clear();
            jsonObject.put(CONTAINER_TYPE, Collection.class.getName());

			Collection item_collection = (Collection) value;
			Iterator item_iterator = item_collection.iterator();
			Object obj = item_iterator.next();
			while (item_iterator.hasNext()) {
				jsonarray.add(serializeComplex(obj));
				obj = item_iterator.next();
			}
            jsonObject.put(ENTRY_VALUE, jsonarray );
			return jsonObject;

		} else {
			logger.error("Unsupported key detected");
		}

		return new JSONArray();
	}

	public static Object deserializeComplex(JSONObject value) {

         if(value instanceof JSONObject) {

            logger.debug("found an instance of a JSONObject");
            //Ensure that it contains a type identifier and it is a java collection.
            if(value.containsKey(CONTAINER_TYPE) && ((String)value.get(CONTAINER_TYPE)).equals(Collection.class.getName())) {
                if(value.containsKey(ENTRY_VALUE) == false) {
                    return new ArrayList(); // no payload , returning empty list.
                }
                Object payload = value.get(ENTRY_VALUE);
                if(payload instanceof JSONArray) {
                    JSONArray payloadArray = (JSONArray) payload;
                    Collection result = new ArrayList();
                    for(Object arrayItem : payloadArray) {
                        if(arrayItem instanceof JSONObject) {
                            JSONObject jsonArrayItem = (JSONObject) arrayItem;
                            Seller item_value = new Seller();   //replace this with an XCacheComplexObject instantiation class
                            item_value.deserialize(jsonArrayItem.toJSONString());
                            result.add(item_value);
                        }
                    }
                    return result;
                }
            } else if(value.containsKey(CONTAINER_TYPE) && ((String)value.get(CONTAINER_TYPE)).equals(Map.class.getName()))  {
                logger.info("Found a map instance");
                Map map  = new HashMap();
                if(value.containsKey(ENTRY_VALUE)) {
                    Object payload = value.get(ENTRY_VALUE);
                    if(payload instanceof JSONArray) {

                        for(Object item :((JSONArray) payload)) {
                            if(item instanceof JSONObject) {
                                JSONObject jsonArrayItem = (JSONObject) item;
                                String keyType = (String) jsonArrayItem.get(MAP_KEY_TYPE);
                                Object keyValue = jsonArrayItem.get(MAP_KEY);
                                JSONObject mapValue = ((JSONObject) jsonArrayItem.get(ENTRY_VALUE));

                                if(mapValue == null || keyType == null || keyValue == null) {
                                    logger.error("Invalid data detected for map entry with value of: " + mapValue + " key Value of: " + keyValue + " with type of: " + keyType);
                                    continue;
                                }
                                //Gotchas
                                //Our JSON library favors Longs over Ints, downcasting to match initial object.
                                if(keyValue.getClass().getName().equals(Long.class.getName()) && keyValue.getClass().getName().equals(keyType) == false) {

                                    Long temp = (Long) keyValue;
                                    keyValue = (Integer) temp.intValue();
                                }
                                logger.info(keyValue.getClass().getName());

                                Seller item_value = new Seller();   //replace this with an XCacheComplexObject instantiation class
                                map.put(keyValue, item_value);
                            }
                        } //end for loop.
                        return map;
                    }
                }



            }


            else {
                    // payload was not an array.. unsure how to handle.. return empty list.
                    return null;
            }
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

    public static void writeToFile(String fileName, String data)
    {
        try {
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);
            out.flush();
            out.close();
            fstream.close();
            logger.info("Successfully wrote data to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromfile(String fileName)
    {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            while ((str = in.readLine()) != null) {
                buffer.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return buffer.toString();
    }

    public static String writeListObject()
    {
		List list = new ArrayList();
		for (int i = 0; i < 10; i++) {
			list.add(Seller.getRandomInstance());
		}

        Object ret =  serializeComplex(list);
        String buffer = "";

        if( ret instanceof JSONArray)
            buffer = ((JSONArray)ret).toJSONString();
        else if(ret instanceof JSONObject)
            buffer = ((JSONObject)ret).toJSONString();

        return buffer;

    }
    public static String writeMapObject()
    {
        Map serializeMe = buildMapInstance();
        Map simpleMap = new HashMap();

		for (int i = 0; i < 5; i++) {
			simpleMap.put(i, Seller.getRandomInstance());
		}

        String buffer = "";
		Object ret =  serializeComplex(simpleMap);

        if( ret instanceof JSONArray)
            buffer = ((JSONArray)ret).toJSONString();
        else if(ret instanceof JSONObject)
            buffer = ((JSONObject)ret).toJSONString();

        return buffer;
    }
    public static void writeObject(String type)
    {
        //JSONObject foobar = serializeComplex(serializeMe);
        Map map = buildMapInstance();

        /*
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


        */
        String buffer = "";
		Object ret =  serializeComplex(map);

        if( ret instanceof JSONArray)
            buffer = ((JSONArray)ret).toJSONString();
        else if(ret instanceof JSONObject)
            buffer = ((JSONObject)ret).toJSONString();

        logger.info(buffer);
		//JSONObject foobar = serializeComplex(simpleMap2);

        writeToFile("/tmp/samir.json", buffer);

    }

    public static void generateSampleData()
    {
        String buffer = "";
        buffer = writeMapObject();
        writeToFile("/tmp/map.json", buffer);
        buffer = writeListObject();
        //writeToFile("/tmp/list.json", buffer);

    }

    public static void readSampleData()
    {
         String fileName = "/tmp/map.json";

        String raw_data = readFromfile(fileName);
        JSONParser parser = new JSONParser();
        Object v = null;
        try {
            JSONObject value = (JSONObject) parser.parse(raw_data);
            v =  deserializeComplex(value);
            int i = 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info("exiting readSampleData");



    }

	public static void main(String[] args) {
        //generateSampleData();
        //writeObject("");
        readSampleData();




        logger.info("THE END");

	}

}
