package com.schriek.snuffelneus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	private final int KOPPEL_REQUEST_CODE = 1;
	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = getSharedPreferences("com.schriek.snuffelneus", MODE_PRIVATE);

		addPreferencesFromResource(R.xml.settings);

		Preference koppelSettings = (Preference) findPreference("koppelen");
		Preference pairdKoppelSettings = (Preference) findPreference("paired_koppelen");
		Preference intervalSetting = (Preference) findPreference("interval"); /* toDo: interval setting*/
		
		koppelSettings
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent viewIntent = new Intent(SettingsActivity.this,
								KoppelActivity.class);
						startActivityForResult(viewIntent, KOPPEL_REQUEST_CODE);

						return true;
					}
				});

		pairdKoppelSettings
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent viewIntent = new Intent(SettingsActivity.this,
								PairedKoppelActivity.class);
						startActivityForResult(viewIntent, KOPPEL_REQUEST_CODE);

						return true;
					}
				});
		
//		intervalSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//
//			public boolean onPreferenceClick(Preference preference) {
//				new TimePickerDialog(getApplicationContext(), new TimePickerDialog.OnTimeSetListener() {
//					
//					@Override
//					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//						// TODO Auto-generated method stub
//						
//					}
//				}, 0, 0, true).show();
//				
//				return true;
//			}
//		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == KOPPEL_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String newAdres = data.getStringExtra("adres");
				String newName = data.getStringExtra("name");
				
				if (newAdres != null) {
					settings.edit().putString("bl_adres", newAdres).commit();
					settings.edit().putString("bl_adres_name", newName)
							.commit();
					
					ListLogger.info("Paired device : "
							+ settings.getString("bl_adres_name", "null") + " "
							+ settings.getString("bl_adres", "null"));
				}

			} else if (resultCode == RESULT_CANCELED) {
				ListLogger.info("Koppelen geannuleerd");
			}
		}
	}

}
