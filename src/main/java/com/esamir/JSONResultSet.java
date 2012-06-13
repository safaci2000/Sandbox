package com.esamir;


import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;


public class JSONResultSet {

	final JSONObject decoder;

	private JSONResultSet() {

		decoder = null;
	}

	public JSONResultSet(JSONObject value) {

		this.decoder = value;
	}

	public String getString(String fieldName) {

		return getString(fieldName, "");
	}

	public String getString(String fieldName, String defaultValue) {
		String value = (String) decoder.get(fieldName);

		if ((value == null) || value.equals("")) {
			return defaultValue;
		}

		return value;
	}

	public Boolean getBoolean(String fieldName) {

		return getBoolean(fieldName, Boolean.FALSE);
	}

	public Boolean getBoolean(String fieldName, Boolean defaultValue) {

		Boolean value = (Boolean) decoder.get(fieldName);

		return (value == null) ? defaultValue : value;
	}

	public Integer getInteger(String fieldName) {

		return getInteger(fieldName, 0);
	}

	public Integer getInteger(String fieldName, int defaultInt) {

		Long value = (Long) decoder.get(fieldName);

		return (value == null) ? defaultInt : value.intValue();
	}

	public Integer getInteger(String fieldName, Integer defaultInt) {

		return getInteger(fieldName, defaultInt.intValue());
	}

	public Long getLong(String fieldName) {

		return getLong(fieldName, 0L);
	}

	public Long getLong(String fieldName, long defaultLong) {

		Long value = (Long) decoder.get(fieldName);

		return (value == null) ? new Long(defaultLong) : value;

	}

	public Long getLong(String fieldName, Long defaultLong) {

		return getLong(fieldName, defaultLong.longValue());
	}

	public Double getDouble(String fieldName) {

		return getDouble(fieldName, 0.0);
	}

	public Double getDouble(String fieldName, double defaultDouble) {

		Double value = (Double) decoder.get(fieldName);

		return (value == null) ? new Double(defaultDouble) : value;

	}

	public Double getDouble(String fieldName, Double defaultDouble) {

		return getDouble(fieldName, new Double(defaultDouble));
	}


	public float getFloat(String fieldName) {

		Float value = (Float) decoder.get(fieldName);

		return (value == null) ? 0.0f : value;
	}

	// numeric values seem to come in as Longs, need to downcast
	public Byte getByte(String fieldName) {
		final Long value = (Long) decoder.get(fieldName);

		return (value == null) ? null : value.byteValue();
	}


	public float getFloat(String fieldName, Float defaultValue) {

		Float value = (Float) decoder.get(fieldName);

		return (value == null) ? defaultValue : value;
	}


	public float getFloat(String fieldName, float defaultValue) {
		return getFloat(fieldName, new Float(defaultValue));
	}

	public Date getDate(String fieldName) {

		return getDate(fieldName, null);
	}


	// NOTE:  Using simple-json (our current implementation), date and time
	// are considered complex objects. It is recommended to store the date
	// in json as the epoch time ie.  .getTime(); which should be available
	// for  java.util.Date, java.sql.Date, and java.sql.Timestamp;

	public Date getDate(String fieldName, Date defaultDate) {

		Long epoch = (Long) decoder.get(fieldName);

		return (epoch == null) ? defaultDate : new Date(epoch);
	}

	public java.sql.Date getSQLDate(String fieldName) {

		return getSQLDate(fieldName, null);
	}

	public java.sql.Date getSQLDate(String fieldName,
	                                java.sql.Date defaultDate) {

		Long epoch = (Long) decoder.get(fieldName);

		return (epoch == null) ? defaultDate : new java.sql.Date(epoch);
	}

	public Timestamp getSQLTimeStamp(String fieldName) {

		return getSQLTimeStamp(fieldName, null);
	}

	public Timestamp getSQLTimeStamp(String fieldName, Timestamp defaultDate) {

		Long epoch = (Long) decoder.get(fieldName);

		return (epoch == null) ? defaultDate : new java.sql.Timestamp(epoch);

	}

	public byte[] getByteArray(String fieldName) {

		byte[] value = (byte[]) decoder.get(fieldName);

		return value;
	}


}
