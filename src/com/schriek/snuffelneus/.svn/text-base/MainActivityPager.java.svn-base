package com.schriek.snuffelneus;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivityPager extends FragmentActivity {

	public static final int LOGGER = 0;
	public static final int STARTPAGINA = 1;
	public static final int MAP = 2;
	public static final int INFO = 3;

	private Button _meetButton;
	private SharedPreferences prefs = null;
	private AlarmManagerBroadcastReceiver alarm = null;

	private final int SETTINGS_REQUEST_CODE = 1;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	private Drawable infoIcon;
	private Drawable listIcon;
	private Drawable speedIcon;
	private int iconHeight;
	private int iconWidth;
	private TabsAdapter adapter;
	private BroadcastReceiver receiver;
	
	private final int REQUEST_ENABLE_BT = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_pager);

		/*
		 * Tabs en fragment populaten, verder nodige controllers starten
		 */
		infoIcon = getResources().getDrawable(R.drawable.info);
		listIcon = getResources().getDrawable(R.drawable.resultaten);
		speedIcon = getResources().getDrawable(R.drawable.speed);

		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayUseLogoEnabled(false);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		adapter = new TabsAdapter(this, mViewPager);

		Tab tabLog = bar.newTab();
		Tab tabMeting = bar.newTab();
		Tab tabMap = bar.newTab();
		Tab tabInfo = bar.newTab();

		tabMeting.setText("Meting").setIcon(R.drawable.ic_action_meter);
		tabLog.setText("Log").setIcon(R.drawable.ic_action_lijst);
		tabMap.setText("Kaart").setIcon(R.drawable.ic_action_kaart);
		tabInfo.setText("Info").setIcon(R.drawable.ic_action_info);

		adapter.addTab(tabLog, ListLogger.class, null, LOGGER, false);
		adapter.addTab(tabMeting, MetingPage.class, null, STARTPAGINA, true);
		adapter.addTab(tabMap, MapActivity.class, null, MAP, false);
		adapter.addTab(tabInfo, InfoPage.class, null, INFO, false);

		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(STARTPAGINA);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

		DataSource.initialize(this);
		DataSource.getInstance().open();

		prefs = getSharedPreferences("com.schriek.snuffelneus", MODE_PRIVATE);

		alarm = new AlarmManagerBroadcastReceiver();
		alarm.cancelAlarm(getApplicationContext());
		alarm.setAlarmManager(getApplicationContext(), 0L);

		/*
		 * Receiver ontvangt broadcasts van andere activity's
		 * 
		 */
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String data = intent.getExtras().getString(
						BackgroundTask.SNUFFELNEUS);

				String manual = intent.getExtras().getString(
						MetingPage.MANUAL_REQUEST);
				
				if (data != null) {
					DataContainer tmp = DataContainer.fromString(data);

					if (tmp != null) {
						MetingPage fr = (MetingPage) adapter
								.getItem(STARTPAGINA);

						fr.updateContent(tmp);
					}

				}

				if (manual != null) {
					alarm.cancelAlarm(getApplicationContext());
					alarm.setAlarmManager(getApplicationContext(),
							AlarmManagerBroadcastReceiver.SERVICE_INTERVAL);
				}

			}
		};
	}

	@Override
	public void onBackPressed() {
		if (mViewPager.getCurrentItem() == STARTPAGINA) {
			super.onBackPressed();
		} else {
			int where = mViewPager.getCurrentItem();

			if (where < STARTPAGINA) {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
			} else if (where > STARTPAGINA) {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			alarm.cancelAlarm(this); // Schedulen van meten pauzeren tijdens het
										// koppelen

			startActivityForResult(new Intent(this, SettingsActivity.class),
					SETTINGS_REQUEST_CODE);
			return true;
		case R.id.stop:
			alarm.cancelAlarm(getApplicationContext());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } 
		
		/*
		 * Kijken voor eerste run
		 */
		if (prefs.getBoolean("firstrun", true)) {
			prefs.edit().putBoolean("firstrun", false).commit();

			startActivity(new Intent(this, KoppelActivity.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SETTINGS_REQUEST_CODE) {
			alarm.setAlarmManager(this, 0); // Koppelen klaar, schedulen weer
											// starten
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
				new IntentFilter(BackgroundTask.SNUFFELNEUS));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

		super.onStop();
	}

	/**
	 * Leuke transition
	 * @author Android
	 *
	 */
	class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}