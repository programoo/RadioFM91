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
			this.traffyNewsXmlParser(result);
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

	public void traffyNewsXmlParser(String xmlString) {

		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			doc = builder.parse(is);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		//doc.getDocumentElement().normalize();

		System.out.println("Root element :"
				+ doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("news");

		System.out.println("----------------------------" + nList.getLength());

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String id = eElement.getAttribute("id");
				String type = eElement.getAttribute("type");
				String primarySource = eElement.getAttribute("primarysource");
				String secondarySource = eElement
						.getAttribute("secondarysource");
				String startTime = eElement.getAttribute("starttime");
				String endTime = eElement.getAttribute("endtime");

				String mediaType = eElement.getElementsByTagName("media")
						.item(0).getAttributes().getNamedItem("type")
						.getNodeValue();
				String mediaPath = eElement.getElementsByTagName("media")
						.item(0).getAttributes().getNamedItem("path")
						.getNodeValue();

				String title = eElement.getElementsByTagName("title").item(0)
						.getTextContent();
				String description = eElement
						.getElementsByTagName("description").item(0)
						.getTextContent();

				String locationType = eElement.getElementsByTagName("location")
						.item(0).getAttributes().getNamedItem("type")
						.getNodeValue();

				String roadName = getStringValueFromExistElement(eElement,
						"road", "name");
				String startPointName = getStringValueFromExistElement(
						eElement, "startpoint", "name");
				String startPointLat = getStringValueFromExistElement(eElement,
						"startpoint", "latitude");
				String startPointLong = getStringValueFromExistElement(
						eElement, "startpoint", "longitude");

				String endPointName = getStringValueFromExistElement(eElement,
						"endpoint", "name");
				String endPointLat = getStringValueFromExistElement(eElement,
						"endpoint", "latitude");
				String endPointLong = getStringValueFromExistElement(eElement,
						"endpoint", "longitude");
				News n =new News(id, type, primarySource, secondarySource, startTime,
						endTime, mediaType, mediaPath, title, description,
						locationType,roadName,startPointName,startPointLat,startPointLong,endPointName,endPointLat,endPointLong);
				System.out.println(n.toString());
			}
		}

	}// end xml parser

	public String getStringValueFromExistElement(Element eElement,
			String elementName, String attributeName) {
		try {
			String valueString = eElement.getElementsByTagName(elementName)
					.item(0).getAttributes().getNamedItem(attributeName)
					.getNodeValue();
			return valueString;
		} catch (NullPointerException e) {
			Log.d(tag, "element not found: " + elementName + ","
					+ attributeName);
			return "undefined";
		}

	}

}
