package com.schriek.snuffelneus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * De SQL helper van de SQLIte database
 * @author PimvanDijk
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_SENSORDATA = "sensordata";
  public static final String COLUMN_ID = "sensordata_id";
  public static final String COLUMN_SENSORWAARDE1 = "sensor1";
  public static final String COLUMN_SENSORWAARDE2 = "sensor2";
  public static final String COLUMN_SENSORWAARDE3 = "sensor3";
  // nieuwe sensorwaardes toevoegen
  public static final String COLUMN_TIMESTAMP = "timestamp";
  public static final String COLUMN_LONGITUDE = "longitude";
  public static final String COLUMN_LATITUDE = "latitude";

  private static final String DATABASE_NAME = "data.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_SENSORDATA + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_SENSORWAARDE1+ " real, "
      + COLUMN_SENSORWAARDE2 + " real,"
      + COLUMN_SENSORWAARDE3 + " real,"
      // nieuwe sensorwaardes toevoegen
      + COLUMN_TIMESTAMP + " integer,"
      + COLUMN_LONGITUDE + " real,"
      + COLUMN_LATITUDE + " real);";

  /**
   * Maak een nieuwe database helper aan
   * @param context de activity welke met de database wil praten
   */
  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * Maak de database
   */
  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }
  
  /**
   * Verwijder de database en maak een nieuwe aan. Bv als een nieuwe databasestructuur is ingevoerd.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORDATA);
    onCreate(db);
  }

} 