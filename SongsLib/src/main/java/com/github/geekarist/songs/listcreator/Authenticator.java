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

import com.github.geekarist.songs.Sleeper;
import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.http.PrintableHttpPost;

public class Authenticator {

	private static final String DEVICECODE_URL = "https://accounts.google.com/o/oauth2/device/code";
	private static final String DEVICECODE_POST_URI = "/o/oauth2/device/code";
	private static final String ATTR_USER_CODE = "user_code";
	private static final String ATTR_VERIFICATION_URL = "verification_url";
	private static final String ATTR_DEVICE_CODE = "device_code";
	private static final String ATTR_INTERVAL = "interval";

	private static final String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String TOKEN_POST_URI = "/o/oauth2/token";
	private static final String ATTR_ERROR = "error";
	private static final String ATTR_ACCESS_TOKEN = "access_token";
	private static final String VALUE_AUTHORIZATION_PENDING = "authorization_pending";
	private static final int MAX_TOKEN_RETRY = 10;

	private HttpClient httpClient;
	private PrintStream out;
	private String authorizationToken;
	private Sleeper sleeper;

	public Authenticator(HttpClient httpClient, PrintStream out, Sleeper sleeper) {
		this.httpClient = httpClient;
		this.out = out;
		this.sleeper = sleeper;
	}

	private class DeviceCodeResponse {
		public DeviceCodeResponse(String deviceCode, int delaySeconds) {
			this.deviceCode = deviceCode;
			this.delaySeconds = delaySeconds;
		}

		public String deviceCode;
		public int delaySeconds;
	}

	public void authenticate() throws SongsLibException {
		DeviceCodeResponse deviceCodeResponse = fetchDeviceCodeResponse();

		HttpPost request = new PrintableHttpPost(TOKEN_POST_URI);
		addCommonHeaders(request);
		String tokenRequestContents = createTokenRequestContents(deviceCodeResponse.deviceCode);
		StringEntity tokenRequestEntity = createEntity(tokenRequestContents);
		request.setEntity(tokenRequestEntity);

		String token = fetchToken(request, deviceCodeResponse.delaySeconds);
		setAuthorizationToken(token);
	}

	private void setAuthorizationToken(String token) {
		this.authorizationToken = token;
	}

	private String fetchToken(HttpPost request, int delaySeconds) throws SongsLibException {
		int nbCalls = 0;
		String tokenError;
		String tokenResponseBody;
		do {
			if (nbCalls != 0) {
				try {
					sleeper.sleep(delaySeconds * 1000);
				} catch (InterruptedException e) {
					throw new SongsLibException("Error while waiting between token requests", e);
				}
			}
			nbCalls++;
			HttpResponse tokenResponse = execute(request, TOKEN_URL);
			tokenResponseBody = responseBodyToString(tokenResponse.getEntity());
			tokenError = readJsonAttrValue(tokenResponseBody, ATTR_ERROR);
		} while (VALUE_AUTHORIZATION_PENDING.equals(tokenError) && nbCalls < MAX_TOKEN_RETRY);

		return readJsonAttrValue(tokenResponseBody, ATTR_ACCESS_TOKEN);
	}

	private String createTokenRequestContents(String deviceCode) {
		return String.format( //
				"client_id=CLIENT_ID.apps.googleusercontent.com" + //
						"&client_secret=CLIENT_SECRET" + //
						"&code=%s" + //
						"&grant_type=http://oauth.net/grant_type/device/1.0",//
				deviceCode);
	}

	private DeviceCodeResponse fetchDeviceCodeResponse() throws SongsLibException {
		HttpPost request = new PrintableHttpPost(DEVICECODE_POST_URI);
		addCommonHeaders(request);
		StringEntity entity = createEntity(createDeviceCodeRequestContents());
		request.setEntity(entity);
		HttpResponse deviceCodeResponse = execute(request, DEVICECODE_URL);
		String deviceCodeResponseBody = responseBodyToString(deviceCodeResponse.getEntity());
		String verificationUrl = readJsonAttrValue(deviceCodeResponseBody, ATTR_VERIFICATION_URL);
		String userCode = readJsonAttrValue(deviceCodeResponseBody, ATTR_USER_CODE);
		out.printf("Please go to %s in your web browser and enter this code: %s", verificationUrl, userCode);
		return new DeviceCodeResponse( //
				readJsonAttrValue(deviceCodeResponseBody, ATTR_DEVICE_CODE), //
				Integer.parseInt(readJsonAttrValue(deviceCodeResponseBody, ATTR_INTERVAL)));
	}

	private StringEntity createEntity(String createDeviceCodeRequestContents) throws SongsLibException {
		StringEntity entity;
		try {
			entity = new StringEntity(createDeviceCodeRequestContents);
		} catch (UnsupportedEncodingException e1) {
			throw new SongsLibException("Error while creating device code request", e1);
		}
		return entity;
	}

	private String readJsonAttrValue(String deviceCodeResponseBody, String jsonAttrName) throws SongsLibException {
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

	private String responseBodyToString(HttpEntity entity) throws SongsLibException {
		String deviceCodeResponseBody;
		try {
			deviceCodeResponseBody = EntityUtils.toString(entity);
		} catch (ParseException e) {
			throw new SongsLibException("Error while reading http response body", e);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading http response body", e);
		}
		return deviceCodeResponseBody;
	}

	private HttpResponse execute(HttpPost request, String uri) throws SongsLibException {
		HttpResponse deviceCodeResponse;
		try {
			deviceCodeResponse = httpClient.execute(new HttpHost(uri), request);
		} catch (ClientProtocolException e) {
			throw new SongsLibException("Error while executing http request", e);
		} catch (IOException e) {
			throw new SongsLibException("Error while executing http request", e);
		}
		return deviceCodeResponse;
	}

	private String createDeviceCodeRequestContents() {
		return "client_id=CLIENT_ID.apps.googleusercontent.com&scope=https://gdata.youtube.com";
	}

	private void addCommonHeaders(HttpPost request) {
		request.addHeader("Host", "accounts.google.com");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}

	public String getAccessToken() {
		return authorizationToken;
	}

}
