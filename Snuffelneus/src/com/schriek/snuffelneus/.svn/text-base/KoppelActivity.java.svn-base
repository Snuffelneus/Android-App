package com.schriek.snuffelneus;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
 * Activity om nieuwe snuffelneuzen te koppelen
 * @author Schriek
 *
 */
public class KoppelActivity extends Activity {

	private BluetoothAdapter btAdapter = null;
	private OutputStream outStream = null;
	private BluetoothSocket socket = null;
	private SingBroadcastReceiver mReceiver = null;
	private List<String> devices = new ArrayList<String>();
	private ArrayAdapter<String> devices_adapter = null;
	private IntentFilter iFilter;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.koppel_activity);

		setResult(RESULT_CANCELED);

		mReceiver = new SingBroadcastReceiver();

		/* Filteren op voor ons belangrijke dingen, eg device gevonden */
		iFilter = new IntentFilter();
		iFilter.addAction(BluetoothDevice.ACTION_FOUND);
		iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		iFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

		registerReceiver(mReceiver, iFilter);

		ListView devices_list = (ListView) findViewById(R.id.device_list);
		
		devices_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, devices);

		devices_list.setAdapter(devices_adapter);
		devices_list.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Als er op een items geklikt wordt, verbinden om een koppel aanvraag te genereren.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String val = (String) ((TextView) arg1).getText();
				String adres = val.substring(val.length() - 17);

				if (adres != null) {
					btAdapter.cancelDiscovery();

					try {
						BluetoothDevice device = btAdapter
								.getRemoteDevice(adres);
						Method m = device.getClass()
								.getMethod("createRfcommSocket",
										new Class[] { int.class });
						BluetoothSocket btSocket = (BluetoothSocket) m.invoke(
								device, 1);

						btSocket.connect();
					} catch (Exception e) {
						ListLogger.Error(e.getMessage());
					}
				}
			}
		});

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		CheckBTState();

	}

	/**
	 * Deze methode checkt de status van BT
	 */
	private void CheckBTState() {
		if (btAdapter == null) {
			ListLogger.Error("Bluetooth NOT supported. Aborting.");
			return;
		} else {
			if (btAdapter.isEnabled()) {

				if (!btAdapter.startDiscovery())
					ListLogger.Error("foutje");
			} else {
				btAdapter.enable();
				CheckBTState();
			}
		}
	}

	/**
	 * Aangeroepen als back of iets dergelijks wordt ingedrukt, ruimt de boel op
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {

		}
	}

	private class SingBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction(); // may need to chain this to a
												// recognizing function
			ListLogger.Verbose(action);

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					if (device.getName() != null) {
						if (device.getName().startsWith("snuffelneus")) {
							String dev_name = device.getName() + "\n"
									+ device.getAddress();

							devices.add(dev_name);
							devices_adapter.notifyDataSetChanged();
						}
					}
				}
			}

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				ListLogger.Verbose("Scan done");
			}

			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				switch (device.getBondState()) {
				case BluetoothDevice.BOND_NONE:
					break;

					/**
					 * Als het koppelen succesvol was
					 */
				case BluetoothDevice.BOND_BONDED:
					unregisterReceiver(mReceiver);

					if (btAdapter.isEnabled())
						btAdapter.disable();

					/* gekoppelde apparaat terug geven */
					Intent returnIntent = new Intent();
					returnIntent.putExtra("adres", device.getAddress());
					returnIntent.putExtra("name", device.getName());
					
					KoppelActivity.this.setResult(RESULT_OK, returnIntent);

					finish();
					break;

				case BluetoothDevice.BOND_BONDING:
					break;
				default:

					break;

				}
			}
		}
	}

}
