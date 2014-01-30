package com.schriek.snuffelneus;

import java.text.SimpleDateFormat;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This shows how to create a simple activity with a map and a marker on the
 * map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is
 * not installed/enabled/updated on a user's device.
 */
public class MapActivity extends Fragment {
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */
	private GoogleMap mMap;
	double lowLimit = 100.0;
	double highLimit = 600.0;

	private final String template = "hh:mm:ss dd-MM-yyyy";
	  //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        	clearMap();
        	addAllMarkers();
        	//Log.i("MAP", "markers gezet");
            timerHandler.postDelayed(this, 60000);
        }
    };
	
	class MyInfoWindowAdapter implements InfoWindowAdapter{

        private final View myContentsView;
		
		MyInfoWindowAdapter(LayoutInflater inflater){
			myContentsView = inflater.inflate(R.layout.custom_info_contents, null);
			
		}
		
		@Override
		public View getInfoContents(Marker marker) {
			
			String[] list = marker.getSnippet().split(",");
			
			// Hieronder kunnen de verschillende snippets worden ingevuld. Denk aan een beschrijving van de waardes.
			String snippet = "Hier is de luchtkwaliteit best redelijk"; 
			if( Double.parseDouble(list[0]) > highLimit)
				snippet = "De luchtkwaliteit is slecht!";
			else if(Double.parseDouble(list[0]) < lowLimit)
				snippet = "De luchtkwaliteit is hier uitstekend!";

			TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText("Meting van Snuffelneus:");
			TextView datetime = ((TextView)myContentsView.findViewById(R.id.datetime));
            datetime.setText("Gemeten op: "+new SimpleDateFormat(template).format(Double.parseDouble(list[3])* 1000.0));
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(snippet);
            TextView sensor1 = ((TextView)myContentsView.findViewById(R.id.sensor1));
            sensor1.setText("Fijnstof: " + list[0] + " ppb");
            TextView sensor2 = ((TextView)myContentsView.findViewById(R.id.sensor2));
            sensor2.setText("Temperatuur:  " + list[1] + "  \u00b0C");
            TextView sensor3 = ((TextView)myContentsView.findViewById(R.id.sensor3));
            sensor3.setText("Luchtvochtigheid:  " + list[2] + " %");
            
			
            return myContentsView;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View fragment = inflater.inflate(R.layout.map_activity, container,
				false);
		mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		setUpMap();

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(inflater));

		addAllMarkers();
		//deleteAllMarkers(); 
        timerHandler.postDelayed(timerRunnable, 0);
        focusOnRotterdam();
		return fragment;
	}


	/**
	 * Het toevoegen van een marker op de kaart. De marker is klikbaar. Manier
	 * om een marker toe te voegen:
	 * 
	 * addMarker(new LatLng(11.84031,14.640971),"Schrieks crib",
	 * "Stinkt hier! Niet te harden!"
	 * ,BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
	 * );
	 * 
	 * @param lPos
	 *            LatLng van de positie
	 * @param sTitle
	 *            De titel, wanneer op de marker wordt geklikt
	 * @param sSnippet
	 *            De beschrijving, wanneer op de marker wordt geklikt
	 * @param iIcon
	 *            De Icon, dit moet een BitmapDescriptor zijn:
	 *            BitmapDescriptorFactory
	 *            .defaultMarker(BitmapDescriptorFactory.KLEUR)
	 */
	public void addMarker(LatLng lPos, String sTitle, String sSnippet,
			BitmapDescriptor iIcon) {
		mMap.addMarker(new MarkerOptions().title(sTitle)
				.snippet(sSnippet).position(lPos).icon(iIcon));
	}

	public void addAllMarkers() {
		DataSource datasource = new DataSource();
		datasource.open();
		List<Record> values = datasource.getAllRecords();

		int aantal = values.size();
		
		//Log.i("Aantal markers:", aantal + " markers");
		for (int i = 0; i < aantal; i++) {
			
			float bmf = BitmapDescriptorFactory.HUE_ORANGE;
			if( (values.get(i).getSensor1()) > highLimit)
				bmf = BitmapDescriptorFactory.HUE_RED;
			else if((values.get(i).getSensor1()) < lowLimit)
				bmf = BitmapDescriptorFactory.HUE_GREEN;
			addMarker(new LatLng(values.get(i).getLatitude(), values.get(i)
					.getLongitude()), "",
					values.get(i).getSensor1()
							+ "," + values.get(i).getSensor2()
							+ "," + values.get(i).getSensor3() + "," + values.get(i).getTimestamp(),
					BitmapDescriptorFactory
							.defaultMarker(bmf));

		}
	}
	
	void deleteAllMarkers(){
		DataSource datasource = new DataSource();
		datasource.open();
		List<Record> values = datasource.getAllRecords();

		int aantal = values.size();
		//Log.i("DELETE MAP", "DELETE ALL MARKERS " + aantal);
		for (int i = 0; i < aantal; i++) {
			datasource.deleteRecord(values.get(i));
			}

	}
	
	public void clearMap(){
		mMap.clear();
	}
	/**
	 * Inzoomen op Rotterdam
	 */
	public void focusOnRotterdam() {
		LatLng ROTTERDAM = new LatLng(51.924216, 4.481776);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ROTTERDAM, 12));
	}

	/**
	 * Inzoomen op de latitude en longitude
	 * 
	 * @param lati
	 *            double van de latitude
	 * @param longi
	 *            double van de longitude
	 * @param zoom
	 *            integer van de zoomfactor
	 */
	public void focus(double lati, double longi, int zoom) {
		LatLng CENTER = new LatLng(lati, longi);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, zoom));
	}

	private void setUpMap() {
		if (mMap != null)
			mMap.setMyLocationEnabled(true);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		clearMap();
        timerHandler.removeCallbacks(timerRunnable);

		try {
			Fragment fragment = (getFragmentManager()
					.findFragmentById(R.id.map));
			FragmentTransaction ft = getActivity().getSupportFragmentManager()
					.beginTransaction();
			ft.remove(fragment);
			ft.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	   // this method performs the task
	   public void run() {
		   clearMap();
		   addAllMarkers();
	   }    

}