package com.ais.sand.radiofm91;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
			randomStr = result;
			String passKey = "";
			try {
				passKey = convertToMD5(AppID + randomStr)
						+ convertToMD5(hiddenkey + randomStr);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			String traffy_request_url = "http://api.traffy.in.th/apis/apitraffy.php?api=getIncident&key="
					+ passKey + "&format=XML";
			new RequestTask().execute(traffy_request_url);
		} else {
			// this mean we get real data from traffy already
			this.xmlParser(result);
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

	public void xmlParser(String result) {
		String xmlRecords = "<data><employee><name>A</name>"
				+ "<title>Manager</title></employee></data>";

		DocumentBuilder db;
		NodeList nodes = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);
			nodes = doc.getElementsByTagName("employee");

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);

			NodeList name = element.getElementsByTagName("news");
			Element line = (Element) name.item(0);
			System.out.println("news: " + getCharacterDataFromElement(line));

		}
	}

	public static String getCharacterDataFromElement(Element e) {
		if(e == null )return "";
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
}
