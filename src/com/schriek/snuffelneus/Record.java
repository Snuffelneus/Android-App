package com.schriek.snuffelneus;

import android.util.Log;

/**
 * Het record wat in de database staat
 * 
 * @author PimvanDijk
 * 
 */
public class Record {
	private int id;
	private double sensor1;
	private double sensor2;
	private double sensor3;
	private int timestamp;
	private double longitude;
	private double latitude;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getSensor1() {
		return sensor1;
	}

	public void setSensor1(double sensor1) {
		this.sensor1 = sensor1;
	}

	public double getSensor2() {
		return sensor2;
	}

	public void setSensor2(double sensor2) {
		this.sensor2 = sensor2;
	}

	public double getSensor3() {
		return sensor3;
	}

	public void setSensor3(double sensor3) {
		this.sensor3 = sensor3;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}