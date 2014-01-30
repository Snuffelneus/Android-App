package com.schriek.snuffelneus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MetingPage extends Fragment implements OnClickListener {

	private TextView scoreView;
	private TextView scoreNaamView;
	private TextView tijdLocView;
	private TextView volgendeMetingView;
	private TextView eenheidView;
	private TextView rawView;
	private TextView stofNaam;
	private TextView snuffelneusNaam;
	private Button meetButton;

	private LocalBroadcastManager broadcaster;

	private final String _stofNaam = "Stikstof dioxide (NO2)";
	private final String _eenheid = "ug/m³";
	private final String template = "dd-MM-yyyy HH:mm:ss";

	public static final String MANUAL_REQUEST = "com.schriek.snuffelneus.MetingPage.MANUAL";

	// public final int METING_MANUAL = 3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		broadcaster = LocalBroadcastManager.getInstance(getActivity());

		View startfragment = inflater.inflate(R.layout.startpagina, container,
				false);

		scoreView = (TextView) startfragment.findViewById(R.id.score);
		scoreNaamView = (TextView) startfragment.findViewById(R.id.score_naam);
		tijdLocView = (TextView) startfragment.findViewById(R.id.tijdloc);
		volgendeMetingView = (TextView) startfragment
				.findViewById(R.id.volgendemeting);
		eenheidView = (TextView) startfragment.findViewById(R.id.eenheid);
		rawView = (TextView) startfragment.findViewById(R.id.rawvalue);
		stofNaam = (TextView) startfragment.findViewById(R.id.stofnaam);
		snuffelneusNaam = (TextView) startfragment
				.findViewById(R.id.snuffelneus_name);

		meetButton = (Button) startfragment.findViewById(R.id.meting);
		meetButton.setOnClickListener(this);

		meetButton.setText("Enkele meting");

		eenheidView.setText(_eenheid);
		stofNaam.setText(_stofNaam);

		scoreNaamView.setText("-");
		scoreView.setText("0");

		String adres = getActivity().getSharedPreferences(
				"com.schriek.snuffelneus", 0).getString("bl_adres_name", "null");
		
		if (adres != null)
			snuffelneusNaam.setText(adres);

		setLastMeasurement();

		return startfragment;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.meting) {
			if (getActivity() != null) {
				getActivity().startService(
						new Intent(getActivity(), BackgroundTask.class));
				sendMessage();
			}
		}
	}

	public void updateContent(DataContainer data) {
		/*tijdLocView.setText(data.getTijdStip());
		rawView.setText(data.getRawValue());

		// TODO
		scoreNaamView.setText("Goed");
		scoreView.setText("10");

		DateFormat dataFormate = new SimpleDateFormat(template);

		Date d = new Date(System.currentTimeMillis()
				+ AlarmManagerBroadcastReceiver.SERVICE_INTERVAL);

		volgendeMetingView.setText("Volgende meting om "
				+ dataFormate.format(d));*/

		setLastMeasurement();

	}

	public void updateContent(Record data) {
		rawView.setText(Double.toString(data.getSensor1()));

		// TODO
		if (data.getSensor1() > 300) {
			scoreNaamView.setText("Goed");
		} else {
			scoreNaamView.setText("Slecht");

		}
		int score = (int) (data.getSensor1() / 600.0 * 10);
		scoreView.setText(Integer.toString(score));

		DateFormat dataFormate = new SimpleDateFormat(template);

		Geocoder geoCoder = new Geocoder(getActivity());
		List<Address> list;
		try {
			list = geoCoder.getFromLocation(data.getLatitude(),
					data.getLongitude(), 1);

			String adres = list.get(0).getAddressLine(0) + " te "
					+ list.get(0).getLocality();

			final String template2 = "HH:mm";

			// Versturen

			Date d = new Date(System.currentTimeMillis()
					+ AlarmManagerBroadcastReceiver.SERVICE_INTERVAL);
			tijdLocView.setText("Om "
					+ new SimpleDateFormat(template2).format(data
							.getTimestamp() * 1000.0) + " in de buurt van\n"
					+ adres);

			volgendeMetingView.setText("Volgende meting om "
					+ dataFormate.format(d));
		} catch (IOException e) {
			ListLogger.Error(e.getMessage());
		}

	}

	private void sendMessage() {
		Intent intent = new Intent(MANUAL_REQUEST);
		intent.putExtra(MANUAL_REQUEST, "Manual");

		broadcaster.sendBroadcast(intent);
	}

	private void setLastMeasurement() {
		DataSource datasource = new DataSource();
		datasource.open();
		Record laatste = null;
		if (datasource.getAllRecords().size() != 0)
			laatste = datasource.getAllRecords().get(
					datasource.getAllRecords().size() - 1);

		if (datasource.getAllRecords().size() != 0)
			updateContent(laatste);

		if (datasource.getAllRecords().size() == 0)
			noMeasures();

	}

	void noMeasures() {

		scoreNaamView.setText("Nog geen metingen gedaan.");
		tijdLocView.setText("-");
		rawView.setText("-");
		scoreView.setText("");
		DateFormat dataFormate = new SimpleDateFormat(template);
		Date d = new Date(System.currentTimeMillis()
				+ AlarmManagerBroadcastReceiver.SERVICE_INTERVAL);

		volgendeMetingView.setText("Volgende meting om "
				+ dataFormate.format(d));
	}
}
