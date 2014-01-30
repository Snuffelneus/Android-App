package com.schriek.snuffelneus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON class
 * 
 * Maakt string van waardes om te verzenden
 * @author PimvanDijk
 *
 */
public class JSONController {
	
	/**
	 * constructor
	 */
	public JSONController(){
	}
	
	/**
	 * Het maken van een string in JSON
	 * @param secret De unieke sleutel voor dit device
	 * @param s1 sensordata1
	 * @param s2 sensordata2
	 * @param s3 sensordata3
	 * @param ts timestamp
	 * @param longi longitude
	 * @param lat latitude
	 * @return de string in JSON
	 */
	public String writeJSON(String secret, double s1, double s2, double s3, String ts, double longi, double lat) {
		JSONObject obj = new JSONObject();
		JSONArray req = new JSONArray();
		JSONObject reqObj = new JSONObject();
		
		try {
			reqObj.put("Sensor", "dust");
			reqObj.put("Value", s1);
			req.put(reqObj);
			reqObj = new JSONObject();
			reqObj.put("Sensor", "temp");
			reqObj.put("Value", s2);
			req.put(reqObj);
			reqObj = new JSONObject();
			reqObj.put("Sensor", "hum");
			reqObj.put("Value", s3);
			req.put(reqObj);
			obj.put("Values", req);
			obj.put("Secret", secret);
			obj.put("Measured", ts);
			obj.put("Longitude", longi);
			obj.put("Latitude", lat);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj.toString();

	}
}
