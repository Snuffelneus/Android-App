package com.schriek.snuffelneus;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.schriek.snuffelneus.TimeoutableLocationListener.TimeoutLisener;

public class GPSController extends Thread {

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static LocationManager locationManager;

	private OnResultCallback callback;

	/**
	 * Initializes the GPS controller
	 * 
	 * @param con
	 *            Context
	 */
	public GPSController(Context con, OnResultCallback cb) {
		locationManager = (LocationManager) con
				.getSystemService(Context.LOCATION_SERVICE);

		callback = cb;

		if (locationManager == null)
			ListLogger.Error("Could not open Location Service");
	}

	private Location newLocation;
	private Location bestCachedLocation;
	private TimeoutableLocationListener listener;

	/**
	 * Deze methode wordt automatisch gestart als dit thread wordt gestart.
	 * Roept de timeoutableLocationListener aan, die de gps zoekt. Pakt ook de
	 * laatst bekende locaties gebaseerd op gps en netwerk en vergelijkt deze
	 * voor het beste resultaat.
	 */
	@Override
	public void run() {
		if (callback == null) {
			System.err.println("Cb is null!");
			return;
		}

		Looper.prepare();

		Location lastNetworkLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		Location lastGPSLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (isBetterLocation(lastNetworkLocation, lastGPSLocation))
			bestCachedLocation = lastNetworkLocation;
		else
			bestCachedLocation = lastGPSLocation;

		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setSpeedRequired(false);
		criteria.setBearingRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		listener = new TimeoutableLocationListener(locationManager,
				(30 * 1000), new TimeoutLisener() {

					@Override
					public void onTimeouted(LocationListener sender) {
						if (isBetterLocation(newLocation, bestCachedLocation))
							callback.onResult(newLocation);
						else
							callback.onResult(bestCachedLocation);

					}

					@Override
					public void onLocationFound(Location location) {
						System.out.println("Found gps loc!");
						callback.onResult(location);
					}
				});

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				100, 0, listener);

	}

	/*
	 * Alle nog lopende acties stopppen
	 */
	public void cleanUp() {
		if (listener != null)
			listener.stopLocationUpdateAndTimer();

		this.interrupt();
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (location == null) {
			Log.e("isBetterLocation", "New location is null");
			return false;
		}

		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public interface OnResultCallback {
		void onResult(Location location);
	}
}
