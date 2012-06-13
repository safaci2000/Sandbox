package com.esamir;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * User: sfaci
 * Date: 6/8/12
 * Time: 1:42 PM
 */
public class Seller implements XCacheComplexObject {
	private JSONParser parser = new JSONParser();
	private Utility util = Utility.getInstance();
	public long id = util.getRandomInt();
	public double cpc;
	public double cpcOverride;
	public boolean maxCpcFlag;
	public long lastModifiedDate;
	public long endDate;
	public int priceTier = -1;
	public String stringData;
	public int leadQuality = -1;
	public int bidAlgorithmId = -1;


	@Override
	public String serialize(JSONObject data) {
		data.put(SellerConstants.ID, this.id);
		data.put(SellerConstants.CPC, this.cpc);
		data.put(SellerConstants.CPC_OVERRIDE, this.cpcOverride);
		data.put(SellerConstants.MAX_CPC_FLAG, this.maxCpcFlag);
		data.put(SellerConstants.LAST_MODIFIED_DATE, this.lastModifiedDate);
		data.put(SellerConstants.END_DATE, this.endDate);
		data.put(SellerConstants.PRICE_TIER, this.priceTier);
		data.put(SellerConstants.STRING_DATA, this.stringData);
		data.put(SellerConstants.BID_ALGORITHM_ID, this.bidAlgorithmId);
		data.put(SellerConstants.LEAD_QUALITY, this.leadQuality);

		return data.toJSONString();
	}

	@Override
	public void deserialize(String buffer) {
		JSONObject decoder = null;

		try {
			decoder = (JSONObject) parser.parse(buffer);

			JSONResultSet values = new JSONResultSet(decoder);

			id = values.getLong(SellerConstants.ID);
			cpc = values.getDouble(SellerConstants.CPC);
			cpcOverride = values.getDouble(
					SellerConstants.CPC_OVERRIDE);

			maxCpcFlag = values.getBoolean(
					SellerConstants.MAX_CPC_FLAG);

			lastModifiedDate = values.getLong(
					SellerConstants.LAST_MODIFIED_DATE, 0L);
			endDate = values.getLong(SellerConstants.END_DATE, 0L);
			priceTier = values.getInteger(SellerConstants.PRICE_TIER);

			stringData = values.getString(
					SellerConstants.STRING_DATA);
			bidAlgorithmId = values.getInteger(
					SellerConstants.BID_ALGORITHM_ID, -1);
			leadQuality = values.getInteger(
					SellerConstants.LEAD_QUALITY, -1);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getCacheId() {
		return String.valueOf(id);
	}

	static Seller getRandomInstance() {
		Utility util = Utility.getInstance();
		Seller obj = new Seller();

		obj.maxCpcFlag = util.getRandomBoolean();
		obj.cpc = util.getRandomDouble();
		obj.cpcOverride = util.getRandomDouble();

		obj.bidAlgorithmId = util.getRandomInt();
		obj.leadQuality = util.getRandomInt();
		obj.priceTier = util.getRandomInt();

		obj.endDate = util.getRandomLong();
		obj.id = util.getRandomLong();
		obj.lastModifiedDate = util.getRandomLong();
		obj.stringData = util.getRandomString();
		return obj;
	}
}
