package com.schriek.snuffelneus;

import java.util.Timer;
import java.util.TimerTask;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * TimeoutableLocationListner is implementation of LocationListener. If
 * onLocationChanged isn't called within XX mili seconds, automatically remove
 * listener.
 * 
 * @author amay077 - http://twitter.com/amay077
 */
public class TimeoutableLocationListener implements LocationListener {
	protected Timer timerTimeout = new Timer();
	protected LocationManager locaMan = null;

	private TimeoutLisener listener;
	private boolean done;
	/**
	 * Initialize instance.
	 * 
	 * @param locaMan
	 *            the base of LocationManager, can't set null.
	 * @param timeOutMS
	 *            timeout elapsed (mili seconds)
	 * @param timeoutListener
	 *            if timeout, call onTimeouted method of this.
	 */
	public TimeoutableLocationListener(LocationManager locaMan, long timeOutMS,
			final TimeoutLisener timeoutListener) {
		this.locaMan = locaMan;
		this.listener = timeoutListener;
		this.done = false;

		timerTimeout.schedule(new TimerTask() {

			@Override
			public void run() {
				if (timeoutListener != null) {
					timeoutListener
							.onTimeouted(TimeoutableLocationListener.this);
				}
				stopLocationUpdateAndTimer();
			}
		}, timeOutMS);
	}

	/***
	 * Location callback.
	 * 
	 * If override on your concrete class, must call base.onLocation().
	 */
	@Override
	public void onLocationChanged(Location location) {
		listener.onLocationFound(location);

		stopLocationUpdateAndTimer();
	}

	@Override
	public void onProviderDisabled(String s) {
	}

	@Override
	public void onProviderEnabled(String s) {
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
	}

	public void stopLocationUpdateAndTimer() {
		if (!done) {
			locaMan.removeUpdates(this);

			timerTimeout.cancel();
			timerTimeout.purge();
			timerTimeout = null;

			done = true;
		}
	}

	public interface TimeoutLisener {
		void onLocationFound(Location location);

		void onTimeouted(LocationListener sender);
	}
}