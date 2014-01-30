package com.schriek.snuffelneus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpController {

	private static HttpController instance = new HttpController(); /* Singleton */

	private final String API_ADRES = "http://snuffelneus.azurewebsites.net/api/Values";
	
	private HttpClient httpClient;

	/**
	 * Constructor
	 */
	public HttpController() {
		httpClient = new DefaultHttpClient();
	}
	
	/**
	 * 
	 * @return instantie van HTTPController
	 */
	public static synchronized HttpController getInstance() {
		if (instance != null)
			return instance;

		return null;
	}

	/**
	 * Data pushen naar de server in JSON formaat.
	 * @param data
	 */
	public synchronized void push(final String data) {

		Threadpool.executeRunable(new Runnable() {
			
			@Override
			public void run() {
				try {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

					HttpPost httpPost = new HttpPost(API_ADRES);

					StringEntity se = new StringEntity(data);
					httpPost.setEntity(se);

					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("Content-type", "application/json");
					HttpResponse httpResponse = httpClient.execute(httpPost);

					httpResponse.getEntity().consumeContent();
//					System.out.println(EntityUtils.toString(httpResponse
//							.getEntity()));

				} catch (Exception e) {
					ListLogger.warn(e.getMessage());
				}
			}

		});
	
	}

}
