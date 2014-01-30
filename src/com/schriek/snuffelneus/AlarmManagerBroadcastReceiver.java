package com.schriek.snuffelneus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	public static final long SERVICE_INTERVAL = 1000 * 40;

	/**
	 * Welke service er gestart wordt
	 */
	@Override
	public void onReceive(final Context con, Intent arg1) {
		con.startService(new Intent(con, BackgroundTask.class));
	}

	/**
	 * Cancelled de Alarm Manager, zodat de service niet meer in de toekomst
	 * wordt gerund.
	 * 
	 * @param context
	 *            Context van de activity
	 */
	public void cancelAlarm(Context context) {
		ListLogger.info("Alarm manager cancel");

		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		//sender.cancel();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		//context.stopService(intent);
	}
	
	/**
	 * Activeert de Alarm manager
	 * 
	 * @param con
	 *            Context van de activity
	 */
	public void setAlarmManager(Context con, long offset) {
		ListLogger.info("Alarm manager interval : " + SERVICE_INTERVAL
				+ " Offset : " + offset);

		Intent i = new Intent(con, AlarmManagerBroadcastReceiver.class);

		PendingIntent sender = PendingIntent.getBroadcast(con, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager _alaramManager = (AlarmManager) con
				.getSystemService(Context.ALARM_SERVICE);

		_alaramManager.setRepeating(AlarmManager.RTC,
				System.currentTimeMillis() + offset, SERVICE_INTERVAL, sender);
	}

}
