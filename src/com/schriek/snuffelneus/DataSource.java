package com.schriek.snuffelneus;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * De database controller
 * 
 * Hiermee kan het programma toegang krijgen tot de database.
 * 
 * Een nieuwe datasource aanmaken kan mbv: private DataSource datasource = new
 * DataSource(this); datasource.open();
 * 
 * Voorbeeld om de gehele database uit te lezen:
 * 
 * List<Record> values = datasource.getAllRecords();
 * 
 * int j = 0; int aantal = values.size(); for(int i = 0; i < aantal; i++){ 
 * j = values.get(i).getId(); Log.i("Lijst",Integer.toString(j)); 
 * }
 * 
 * @author PimvanDijk
 */
public class DataSource {

	// Waardes van de database.
	private SQLiteDatabase database;
	
	private static MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_SENSORWAARDE1,
			MySQLiteHelper.COLUMN_SENSORWAARDE2,
			MySQLiteHelper.COLUMN_SENSORWAARDE3,
			// hier nieuwe sensorwaarde kolom toevoegen
			MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_LONGITUDE,
			MySQLiteHelper.COLUMN_LATITUDE };

	private static DataSource instance = new DataSource();

	public static synchronized DataSource getInstance() {
		if(instance == null)
			return null;
		
		return instance;
	}
	/**
	 * Nieuwe datasource aanmaken
	 * 
	 * @param context
	 *            gebruik this
	 */
	public static void initialize(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	/**
	 * open de database
	 * 
	 * @throws SQLException
	 *             Bij fout in de sql
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * sluiten van de database
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Nieuwe record toevoegen aan de database
	 * 
	 * @param sensor1
	 *            Double van de waarde van de eerste sensor
	 * @param sensor2
	 *            Double van de waarde van de tweede sensor
	 * @param sensor3
	 *            Double van de waarde van de derde sensor
	 * @param timestamp
	 *            Integer van de waarde van timestamp
	 * @param longitude
	 *            Double van de waarde van de Longitude
	 * @param latitude
	 *            Double van de waarde van de Latitude
	 * @return Het nieuwe aangemaakte record worden
	 */
	public Record createRecord(double sensor1, double sensor2, double sensor3,
			int timestamp, double longitude, double latitude) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SENSORWAARDE1, sensor1);
		values.put(MySQLiteHelper.COLUMN_SENSORWAARDE2, sensor2);
		values.put(MySQLiteHelper.COLUMN_SENSORWAARDE3, sensor3);
		// nieuwe sensorwaardes toevoegen
		values.put(MySQLiteHelper.COLUMN_TIMESTAMP, timestamp);
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
		values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
		long insertId = database.insert(MySQLiteHelper.TABLE_SENSORDATA, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SENSORDATA,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Record newRecord = cursorToRecord(cursor);
		cursor.close();
		return newRecord;
	}

	/**
	 * Verwijderen van het record wat wordt meegegeven
	 * 
	 * @param record
	 *            Dit record moet worden verwijderd
	 */
	public void deleteRecord(Record record) {
		long id = record.getId();
		ListLogger.Verbose("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_SENSORDATA,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	/**
	 * Het verwijderen van de record met id
	 * 
	 * @param id
	 *            Record moet worden verwijderd met deze id
	 */
	public void deleteRecord(long id) {
		ListLogger.Verbose("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_SENSORDATA,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	/**
	 * Haal de lijst op met alle records in de database
	 * 
	 * @return de lijst met alle records in de database
	 */
	public List<Record> getAllRecords() {
		List<Record> Records = new ArrayList<Record>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_SENSORDATA,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Record record = cursorToRecord(cursor);
			Records.add(record);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Records;
	}

	/**
	 * Krijg de record terug met de cursor
	 * 
	 * @param cursor
	 * @return record waar de cursor naar verwees
	 */
	private Record cursorToRecord(Cursor cursor) {
		Record record = new Record();
		record.setId(cursor.getInt(0));
		record.setSensor1(cursor.getDouble(1));
		record.setSensor2(cursor.getDouble(2));
		record.setSensor3(cursor.getDouble(3));
		record.setTimestamp(cursor.getInt(4));
		record.setLongitude(cursor.getDouble(5));
		record.setLatitude(cursor.getDouble(6));
		return record;
	}

	/**
	 * Krijg de eerste record (bovenste) in de database terug
	 * 
	 * @return Het bovenste record.
	 */
	public Record getFirstRecord() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SENSORDATA,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();

		Record record = new Record();
		record.setId(cursor.getInt(0));
		record.setSensor1(cursor.getDouble(1));
		record.setSensor2(cursor.getDouble(2));
		record.setSensor3(cursor.getDouble(3));
		record.setTimestamp(cursor.getInt(4));
		record.setLongitude(cursor.getDouble(5));
		record.setLatitude(cursor.getDouble(6));
		return record;
	}
}