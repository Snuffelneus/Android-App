package com.schriek.snuffelneus;

import java.io.DataInput;

/**
 * Container class om belangrijke data makkelijk te transporteren
 * 
 * @author Schriek
 * 
 */
public class DataContainer {

	private String rawValue; /* Ruwe sensor value */
	private String tijdStip; /* Tijd stip */
	private double lat; /* Latitude */
	private double lot; /* Longitude */
	private double batt1;
	private double batt2;

	private final static String DIVIDER = "-!-";

	public DataContainer(String raw, String tijd, double lat, double lot,
			double batt1, double batt2) {
		this.rawValue = raw;
		this.tijdStip = tijd;
		this.lat = lat;
		this.lot = lot;
		this.batt1 = batt1;
		this.batt2 = batt2;
	}

	public String getRawValue() {
		return rawValue;
	}

	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	public void setBatterij1(double val) {
		this.batt1 = val;
	}

	public double getBatterij1() {
		return batt1;
	}

	public void setBatterij2(double val) {
		this.batt2 = val;
	}

	public double getBatterij2() {
		return batt2;
	}

	public String getTijdStip() {
		return tijdStip;
	}

	public void setTijdStip(String tijdStip) {
		this.tijdStip = tijdStip;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLot() {
		return lot;
	}

	public void setLot(double lot) {
		this.lot = lot;
	}

	public String toString() {
		return rawValue + DIVIDER + tijdStip + DIVIDER + lat + DIVIDER + lot
				+ DIVIDER + batt1 + DIVIDER + batt2;
	}

	public static DataContainer fromString(String raw) {
		try {
			String[] split = raw.split(DIVIDER);

			DataContainer tmp = new DataContainer(split[0], split[1],
					Double.valueOf(split[2]), Double.valueOf(split[3]),
					Double.valueOf(split[4]), Double.valueOf(split[5]));

			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
