package com.schriek.snuffelneus;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity die gekoppelde apparaten weer geeft. Niet echt van groot belang maar handig voor ons
 * @author Schriek
 *
 */
public class PairedKoppelActivity extends Activity {

	private BluetoothAdapter btAdapter = null;
	private OutputStream outStream = null;
	private BluetoothSocket socket = null;
	private List<String> devices = new ArrayList<String>();
	private ArrayAdapter<String> devices_adapter = null;
	private IntentFilter iFilter;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.paired_koppel_activity);

		setResult(RESULT_CANCELED);

		ListView devices_list = (ListView) findViewById(R.id.paired);

		devices_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, devices);

		devices_list.setAdapter(devices_adapter);
		devices_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String val = (String) ((TextView) arg1).getText();
				String adres = val.substring(val.length() - 17);

				Intent returnIntent = new Intent();
				returnIntent.putExtra("adres", adres);
				returnIntent.putExtra("name",
						val.substring(0, val.indexOf("\n")));

				PairedKoppelActivity.this.setResult(RESULT_OK, returnIntent);

				finish();

			}
		});

		Set<BluetoothDevice> neuzen = getBondedSnuffelneuzen();
		List<String> namesList = new ArrayList<String>();

		for (BluetoothDevice dev : neuzen) {
			namesList.add(dev.getName() + "\n" + dev.getAddress());
		}

		devices.addAll(namesList);
		devices_adapter.notifyDataSetChanged();

	}

	private Set<BluetoothDevice> getBondedSnuffelneuzen() {
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter
				.getDefaultAdapter().getBondedDevices();
		Set<BluetoothDevice> snuffelneuzen = new LinkedHashSet<BluetoothDevice>();
		for (BluetoothDevice device : pairedDevices) {
			if (device.getName().startsWith("snuffelneus"))
				snuffelneuzen.add(device);
		}

		return snuffelneuzen;
	}
}
