package com.schriek.snuffelneus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Spreekt voor zich
 * @author Schriek
 *
 */
public class ListLogger extends Fragment {

	private static Handler handler = new Handler(Looper.getMainLooper()); // ui
																			// thread
	private static List<String> _items = new ArrayList<String>();
	private static ArrayAdapter<String> _list;
	
	public ListLogger(Context con) {
		_list = new ArrayAdapter<String>(con,
				android.R.layout.simple_list_item_1, _items) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);

				String type = (String) text.getText();

				if (type.startsWith("Info")) {
					text.setTextColor(Color.BLACK);
				} else if (type.startsWith("Warning")) {
					text.setTextColor(Color.CYAN);
				} else if (type.startsWith("Error")) {
					text.setTextColor(Color.RED);
				} else if (type.startsWith("Verbose")) {
					text.setTextColor(Color.BLUE);
				}
				
				return view;
			}
		};
	}

	private static void add(final String msg) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				_items.add(0, msg);

				if (_list != null)
					_list.notifyDataSetChanged();
			}
		});
	}

	public static void info(String msg) {
		add("Info : " + msg);
	}

	public static void warn(String msg) {
		add("Warning : " + msg);
	}

	public static void Error(String msg) {
		add("Error : " + msg);
	}

	public static void Verbose(String msg) {
		add("Verbose : " + msg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View commandfragment = inflater.inflate(R.layout.score_pagina,
				container, false);

		ListView list = (ListView) commandfragment.findViewById(R.id.list);
		list.setAdapter(_list);

		return commandfragment;
	}
}
