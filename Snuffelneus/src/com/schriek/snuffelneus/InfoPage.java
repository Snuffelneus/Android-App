package com.schriek.snuffelneus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Deze class spreekt voor zich
 * @author Schriek
 *
 */
public class InfoPage extends Fragment {

	private final String title1 = "Wat wordt gemeten?";
	private final String title2 = "Hoe bepaal je de luchtkwaliteit?";
	private final String title3 = "Over deze app";

	private final String text1 = "Met snuffelneus worden drie dingen," +
			" dit zijn temperatuur, luchtvochtigheid en de het NO2 gehalte van de lucht. " +
			"Vanuit het NO2 gehalte kan de hoeveelheid fijnstof worden bepaald.";
	private final String text2 = "De luchtkwaliteit kan op verschillende manieren" +
			"worden bepaald en is van veel factoren afhankelijk. Het RIVM meet drie " +
			"waardes, te weten Ozon, PM10(fijnstof) en NO2. " +
			"Snuffelneus meet NO2 en hieruit is het fijnstofgehalte van de lucht te bepalen.";
	private final String text3 = "Deze app is gemaakt om te worden gebruikt in " +
			"samenwerking met de Snuffelneus. De Snuffelneus stuurt gemeten data " +
			"naar deze app. De app laat de gemeten waardes zien op een kaart." +
			"Hieruit kan direct worden bekeken hoe de luchtkwaliteit is op de locaties waar u bent geweest." +
			"Als deel van het onderzoek naar luchtkwaliteit worden de waardes ook verstuurd naar" +
			"een centrale database." +
			"\n" +
			"\n" +
			"Gemaakt door Pim & Edwin als onderdeel van het project Snuffelneus" +
			"van de minor Embdedded Systems van de opleiding Elektrotechniek aan Hogeschool Rotterdam." +
			"\n" +
			"\n" +
			"Meer informatie is te vinden op www.snuffelneus.nl";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View info = inflater.inflate(R.layout.info_pagina, null, false);

		TextView _title1 = (TextView) info.findViewById(R.id.title1);
		TextView _title2 = (TextView) info.findViewById(R.id.title2);
		TextView _title3 = (TextView) info.findViewById(R.id.title3);
		TextView _text1 = (TextView) info.findViewById(R.id.text1);
		TextView _text2 = (TextView) info.findViewById(R.id.text2);
		TextView _text3 = (TextView) info.findViewById(R.id.text3);

		_title1.setText(title1);
		_title2.setText(title2);
		_title3.setText(title3);
		_text1.setText(text1);
		_text2.setText(text2);
		_text3.setText(text3);
		return info;
	}

}
