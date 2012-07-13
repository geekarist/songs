package com.github.geekarist.songs.listcreator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.http.PrintableHttpPost;

public class Authenticator {

	private HttpClient httpClient;
	private PrintStream out;
	private String authorizationToken;

	public Authenticator(HttpClient httpClient, PrintStream out) {
		this.httpClient = httpClient;
		this.out = out;
	}

	public void authenticate() throws SongsLibException {
		String deviceCode = fetchDeviceCode();
		waitForAuthorization(deviceCode);
	}

	private void waitForAuthorization(String deviceCode) throws SongsLibException {
		HttpPost request = new PrintableHttpPost("/o/oauth2/token");
		addCommonHeaders(request);
		StringEntity entity = createEntity(createTokenRequestContents(deviceCode));
		request.setEntity(entity);
		setAuthorizationToken(obtainToken(request));
	}

	private void setAuthorizationToken(String obtainToken) {
		this.authorizationToken = obtainToken;
	}

	protected String obtainToken(HttpPost request) throws SongsLibException {
		int retryCount = 0;
		String tokenError;
		String tokenResponseBody;
		do {
			HttpResponse tokenResponse = execute(request, "https://accounts.google.com/o/oauth2/token");
			tokenResponseBody = responseBodyToString(tokenResponse.getEntity());
			tokenError = readJsonAttrValue(tokenResponseBody, "error");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new SongsLibException("Error while waiting between token requests", e);
			}
		} while ("authorization_pending".equals(tokenError) && retryCount < 10);
		
		return readJsonAttrValue(tokenResponseBody, "access_token");
	}

	private String createTokenRequestContents(String deviceCode) {
		return String.format( //
				"client_id=CLIENT_ID.apps.googleusercontent.com" + //
						"&client_secret=CLIENT_SECRET" + //
						"&code=%s" + //
						"&grant_type=http://oauth.net/grant_type/device/1.0",//
				deviceCode);
	}

	protected String fetchDeviceCode() throws SongsLibException {
		HttpPost request = new PrintableHttpPost("/o/oauth2/device/code");
		addCommonHeaders(request);
		StringEntity entity = createEntity(createDeviceCodeRequestContents());
		request.setEntity(entity);
		HttpResponse deviceCodeResponse = execute(request, "https://accounts.google.com/o/oauth2/device/code");
		String deviceCodeResponseBody = responseBodyToString(deviceCodeResponse.getEntity());
		String verificationUrl = readJsonAttrValue(deviceCodeResponseBody, "verification_url");
		String userCode = readJsonAttrValue(deviceCodeResponseBody, "user_code");
		out.printf("Please go to %s in your web browser and enter this code: %s", verificationUrl, userCode);
		return readJsonAttrValue(deviceCodeResponseBody, "device_code");
	}

	protected StringEntity createEntity(String createDeviceCodeRequestContents) throws SongsLibException {
		StringEntity entity;
		try {
			entity = new StringEntity(createDeviceCodeRequestContents);
		} catch (UnsupportedEncodingException e1) {
			throw new SongsLibException("Error while creating device code request", e1);
		}
		return entity;
	}

	protected String readJsonAttrValue(String deviceCodeResponseBody, String jsonAttrName) throws SongsLibException {
		String deviceCode;
		try {
			JSONObject jsonDeviceCodeResponse = new JSONObject(deviceCodeResponseBody);
			if (jsonDeviceCodeResponse.has(jsonAttrName)) {
				deviceCode = jsonDeviceCodeResponse.getString(jsonAttrName);
			} else {
				deviceCode = null;
			}
		} catch (JSONException e) {
			throw new SongsLibException("Error while parsing device code response", e);
		}
		return deviceCode;
	}

	protected String responseBodyToString(HttpEntity entity) throws SongsLibException {
		String deviceCodeResponseBody;
		try {
			deviceCodeResponseBody = EntityUtils.toString(entity);
		} catch (ParseException e) {
			throw new SongsLibException("Error while reading device code response body", e);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading device code response body", e);
		}
		return deviceCodeResponseBody;
	}

	protected HttpResponse execute(HttpPost request, String uri) throws SongsLibException {
		HttpResponse deviceCodeResponse;
		try {
			deviceCodeResponse = httpClient.execute(new HttpHost(uri), request);
		} catch (ClientProtocolException e) {
			throw new SongsLibException("Error while executing device code request", e);
		} catch (IOException e) {
			throw new SongsLibException("Error while executing device code request", e);
		}
		return deviceCodeResponse;
	}

	protected String createDeviceCodeRequestContents() {
		return "client_id=CLIENT_ID.apps.googleusercontent.com&scope=https://gdata.youtube.com";
	}

	protected void addCommonHeaders(HttpPost request) {
		request.addHeader("Host", "accounts.google.com");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}

	public String getAccessToken() {
		return authorizationToken;
	}

}
