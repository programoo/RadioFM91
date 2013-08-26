package com.ais.sand.radiofm91;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class RequestTask extends AsyncTask<String, String, String> {
	private String tag = getClass().getSimpleName();
	public static String AppID = "abcb6710";
	public static String hiddenkey = "TxLYP6j1Ee";
	public static String randomStr = "undefined";

	static ArrayList<String> newsFeed = new ArrayList<String>();

	@Override
	protected String doInBackground(String... uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			response = httpclient.execute(new HttpGet(uri[0]));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			// TODO Handle problems..
		} catch (IOException e) {
			// TODO Handle problems..
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		newsFeed.add(result);
		Log.d(this.getClass().getSimpleName(), "onPostExecute");
		Log.i(tag, "result: " + result);

		// Do anything with response..
		if (randomStr == "undefined") {
			// keep randomStr
			randomStr = result;
			// step2 create session key
			String passKey = "";
			try {
				passKey = convertToMD5(AppID + randomStr)
						+ convertToMD5(hiddenkey + randomStr);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// step3 url
			String traffy_request_url = "http://api.traffy.in.th/apis/apitraffy.php?api=getIncident&key="
					+ passKey + "&format=XML";
			//request again for get real data from traffy
			new RequestTask()
			.execute(traffy_request_url);

		}

	}

	public String convertToMD5(String msg) throws NoSuchAlgorithmException {
		String plaintext = msg;
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update(plaintext.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		return hashtext;
	}
}
