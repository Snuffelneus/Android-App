package com.schriek.snuffelneus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.schriek.snuffelneus.GPSController.OnResultCallback;

/**
 * Deze class regelt de gehele communicatie met de snuffelneus in de
 * achtergrond. Voor nadere uitleg bekijk de bijbehorende documenten.
 * 
 * @author Schriek
 * 
 */
public class BackgroundTask extends IntentService {

	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket;
	private OutputStream btOut;
	private InputStream btIn;
	private SharedPreferences preferences;
	private NotificationManager notifyManager;

	private DataSource databaseManager = DataSource.getInstance();
	private HttpController httpController = HttpController.getInstance();
	private JSONController json = new JSONController();
	private GPSController gpsControlller;
	private LocalBroadcastManager broadcaster;

	private final int NOTIFICATION_ID = 1;
	private final int NOTIFICATION_SHUTDOWN_ID = 2;
	private final String WAKEUP_STRING = "wakeup\n"; /*
													 * Moet newline termnitated
													 * zijn
													 */
	private final static String SERVICE_NAME = "BackgroundTask";

	public final static String SNUFFELNEUS = "com.schriek.snuffelneus.BackgroundTask.REQUEST_PROCESSED";

	/** wordt gebruikt door twee threads **/
	private volatile Location loc = null;

	private final Object lock = new Object();

	/**
	 * Deze constructor doet niks maar is verplicht voor een IntentService
	 */
	public BackgroundTask() {
		super(SERVICE_NAME);

	}

	/**
	 * Deze methode start het meet process
	 */
	private void start() {
		/**
		 * Notification maken De TaskStackBuilder wordt gebruikt om een Activity
		 * te starten als er op de notification wordt gedrukt
		 */
		Notification.Builder notifcation = new Notification.Builder(
				getApplicationContext()).setContentTitle("Snuffelneus")
				.setContentText("Nu aan het meten...")
				.setSmallIcon(android.R.drawable.ic_menu_zoom);

		Intent resultIntent = new Intent(this, MainActivityPager.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivityPager.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notifcation.setContentIntent(resultPendingIntent);

		notifyManager.notify(NOTIFICATION_ID, notifcation.build());

		/** Adres uit preferences halen **/
		preferences = getSharedPreferences("com.schriek.snuffelneus",
				MODE_PRIVATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

		double s1 = 0;
		double s2 = 0;
		double s3 = 0;
		double batt1 = 0;
		double batt2 = 0;

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI_Number = telephonyManager.getDeviceId();

		/* Tijd formateren zodat de server het snapt */
		String template = "yyyy-MM-dd'T'hh:mm:ss'Z'";
		DateFormat dataFormate = new SimpleDateFormat(template);

		Date d = new Date(System.currentTimeMillis());

		/* Callback voor de GPS controller */
		gpsControlller = new GPSController(getApplicationContext(),
				new OnResultCallback() {

					@Override
					public void onResult(Location location) {
						loc = location;

						synchronized (lock) {
							lock.notify();
						}
					}
				});

		gpsControlller.start();

		/* Bluetooth sequence */
		if ((btSocket = connect()) == null) {
			stopSelf();
		} else {
			try {
				btOut = btSocket.getOutputStream();
				btIn = btSocket.getInputStream();

			} catch (IOException e) {
				ListLogger.Error("Socket BT : " + e.getMessage());
			}
		}

		try {
			// Als de wakeup niet goed verzonden kon worden
			if (!sendWakeUp())
				stopSelf();

			if (btIn == null) {
				stopSelf();
			}

			/* In het volgende deel ontvangen we de data van de snuffelneus */
			byte[] buffer = new byte[1024];
			int offset = 0;
			int total_length = 0;

			while (loc == null) {
				try {
					Thread.sleep(150); // Sleep anders komt data te snel binnen
					
					if (btIn.available() > 0) {// Als er data beschikbaar is
						try {
							byte[] tmp_buffer = new byte[256];
							int bytes = btIn.read(tmp_buffer);

							System.arraycopy(tmp_buffer, 0, buffer, offset,
									bytes);
							offset += bytes;
							total_length += bytes;

							String in = new String(buffer, 0, total_length);

							if (in.endsWith("EOM")) {
								String split[] = in.split("--");

								s1 = Double.parseDouble(split[1]);
								s2 = Double.parseDouble(split[2]);
								s3 = Double.parseDouble(split[3]);
								batt1 = Double.parseDouble(split[4]);
								batt2 = Double.parseDouble(split[5]);

								// Format :
								// Result--sensor--temp--luchtvochtigheid--batterij1--batterij2--EOM
								ListLogger.Verbose("Sensor " + s1 + " Temp "
										+ s2 + " Luchtvochtigheid " + s3
										+ " Batterij 1 " + batt1
										+ " Batterij 2 " + batt2);

								if (batt1 < 3.3) { // Wanneer de
													// snuffelneus
													// uit moet
									btOut.write("shutdown\n".getBytes());
									ListLogger
											.Error("Shutting down Snuffelneus");

									Notification.Builder notifcation_shutdown = new Notification.Builder(
											getApplicationContext())
											.setContentTitle("Snuffelneus")
											.setContentText(
													"Batterij van de Snuffelneus is bijna leeg. De snuffelneus is uitgeschakeld")
											.setSmallIcon(
													android.R.drawable.alert_dark_frame);

									notifyManager.notify(
											NOTIFICATION_SHUTDOWN_ID,
											notifcation_shutdown.build());
								}

								break;

							}

						} catch (Exception e) {
							ListLogger.Error(e.getMessage());
							stopSelf();
							break;
						}
					}
				} catch (Exception e) {
					ListLogger.Error(e.getMessage());
					stopSelf();
					break;
				}
			}

		} catch (IOException e) {
			ListLogger.Error("Send wakeup : " + e.getMessage());
		}

		/* Boel netjes sluiten */
		try {
			if (btIn != null) {
				btIn.close();
				btIn = null;
			}

			if (btOut != null) {
				btOut.close();
				btOut = null;
			}

			if (btSocket != null) {
				btSocket.close();
				btSocket = null;
			}
		} catch (IOException e) {
			int lineNumber = Thread.currentThread().getStackTrace()[2]
					.getLineNumber();
			ListLogger.Error(getClass().getName() + " " + lineNumber + " "
					+ e.getMessage());
		}

		/** wachten op het GPS thread met een wait lock **/
		synchronized (lock) {
			while (loc == null) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					ListLogger.Error(e.getMessage());
				}
			}
		}

		/**
		 * Indien we voorbij de lock zijn gekomen hebben we een GPS fix (nieuw
		 * of oud) en sensor data
		 **/

		/* Als we een locatie hebben gaan we het weg schrijven */
		if (loc != null) {
			try {
				// Straat zoeken mbv GeoCoder.
				Geocoder geoCoder = new Geocoder(getApplicationContext());
				List<Address> list = geoCoder.getFromLocation(
						loc.getLatitude(), loc.getLongitude(), 1);

				String adres = list.get(0).getAddressLine(0) + " te "
						+ list.get(0).getLocality();

				final String template2 = "hh:mm";

				databaseManager.createRecord(s1, s2, s3,
						(int) (System.currentTimeMillis() / 1000),
						loc.getLongitude(), loc.getLatitude());

				/* Versturen */
				DataContainer data = new DataContainer(Double.toString(s1),
						"Om " + new SimpleDateFormat(template2).format(d)
								+ " in de buurt van\n" + adres,
						loc.getLatitude(), loc.getLongitude(), batt1, batt2);
				sendMessage(data);

				httpController.push(json.writeJSON(IMEI_Number, s1, s2, s3,
						dataFormate.format(d), loc.getLongitude(),
						loc.getLatitude()));

			} catch (Exception e) {
				if (e.getMessage() == "Service not Available")
					ListLogger.Error(e.getMessage() + " - Try reboot");
				else
					ListLogger.Error(e.getMessage());
			}
		}

		// if (btAdapter.isEnabled())
		// btAdapter.disable();

		stopSelf();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		/**
		 * Moet in main thread context gecalled worden anders zal het niet
		 * werken
		 **/
		notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		broadcaster = LocalBroadcastManager.getInstance(this);
	}

	/** Verbind met de gepairde snuffelneus **/
	private BluetoothSocket connect() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		if (btAdapter == null)
			// BT niet ondersteund, misschien tonen aan gebruiker

			if (!btAdapter.isEnabled()) {
				btAdapter.enable();
			}

		BluetoothSocket btSocket = null;

		try {
			String adres = preferences.getString("bl_adres", "null");

			if (adres == null) {
				ListLogger.Error("Geen snuffelneus gepaired");

				return null;
			}

			/* Socket maken */
			BluetoothDevice device = btAdapter.getRemoteDevice(adres);
			Method m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });

			/** Channel moet 1 zijn **/
			btSocket = (BluetoothSocket) m.invoke(device, 1);

		} catch (Exception e) {
			ListLogger.Error("Kon niet verbinden met Snuffelneus!");
			return null;
		}

		/* Fout afhandeling */
		try {
			btSocket.connect();
			ListLogger.info("Verbonden met Snuffelneus!");
			return btSocket;
		} catch (IOException e) {
			if (e.getMessage().equalsIgnoreCase("Bluetooth is off")
					|| e.getMessage().equalsIgnoreCase("No route to host")) {
				ListLogger.Error("Bluetooth staat waarschijnlijk uit");
			} else if (e.getMessage().equalsIgnoreCase("Host is down")) {
				ListLogger.Error("Snuffelneus staat uit of is buiten bereik");
			} else {
				int lineNumber = Thread.currentThread().getStackTrace()[2]
						.getLineNumber();
				ListLogger.Error(getClass().getName() + " " + lineNumber + " "
						+ e.getMessage());
			}
			try {
				btSocket.close();
			} catch (IOException e2) {
				int lineNumber = Thread.currentThread().getStackTrace()[2]
						.getLineNumber();
				ListLogger.Error(getClass().getName() + " " + lineNumber + " "
						+ e.getMessage());
			}
		}

		return null;
	}

	/*
	 * Deze methode stuurt het command om te gaan meten.
	 * 
	 * @return true als het gelukt is, false als het niet gelukt is.
	 */
	private boolean sendWakeUp() throws IOException {
		if (btSocket == null || btOut == null) {
			return false;
		}
		btOut.write(WAKEUP_STRING.getBytes());
		btOut.flush();

		ListLogger.info("Wake up");

		return true;
	}

	/**
	 * Ruimt threads zoals de GPS die een fix probeert te krijgen op
	 */
	private void stopPendingTasks() {
		gpsControlller.cleanUp();

		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		/* Netjes opruimen */
		ListLogger.Verbose("Service stopping");

		stopPendingTasks();

		notifyManager.cancel(NOTIFICATION_ID);

		if (btSocket != null)
			if (btSocket.isConnected())
				try {
					btSocket.close();
					btSocket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

		if (btIn != null) {
			try {
				btIn.close();
			} catch (Exception e) {
			}
			btIn = null;
		}

		if (btOut != null) {
			try {
				btOut.close();
			} catch (Exception e) {
			}
			btOut = null;
		}
		// TODO
		// if (btAdapter.isEnabled()) {
		// btAdapter.disable();
		// }

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* Message broadcasten zodat we die in de MainActivityPager kunnen opvangen */
	private void sendMessage(DataContainer container) {
		Intent intent = new Intent(SNUFFELNEUS);
		String message = container.toString();

		if (message != null)
			intent.putExtra(SNUFFELNEUS, message);

		broadcaster.sendBroadcast(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		/**
		 * Deze methode zorgt er voor dat alle code erin in een worker thread
		 * wordt gedraaid
		 **/
		start();
	}
}
