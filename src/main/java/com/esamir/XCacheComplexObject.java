package com.esamir;

import org.json.simple.JSONObject;

/**
 * User: Samir Faci
 * Date: 6/13/12
 * Time: 4:24 PM
 */
public interface XCacheComplexObject {
	/**
	 * this will taken the current object, and generate
	 * a string data representation preferably using YAML/JSON
	 */
	public String serialize(JSONObject decoder);

	/**
	 * this will populate object T given a String representation
	 * of the object. (Most likely JSON/ Yaml
	 *
	 * @param buffer
	 */
	public void deserialize(String buffer);

	// getId is sometimes implemented by business objects.	Falling back to
	// getCacheId
	public String getCacheId();
}
