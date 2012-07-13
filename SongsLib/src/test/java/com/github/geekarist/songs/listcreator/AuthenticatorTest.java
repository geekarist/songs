package com.github.geekarist.songs.listcreator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.easymock.EasyMock;
import org.junit.Test;

import com.github.geekarist.songs.http.PrintableHttpPost;
import com.github.geekarist.testutils.HttpTestUtils;

public class AuthenticatorTest {

	@Test
	public void testAuthenticate() throws IOException {
		HttpClient httpClientMock = EasyMock.createMock(HttpClient.class);

		HttpHost deviceCodeTarget = new HttpHost("https://accounts.google.com/o/oauth2/device/code");
		HttpHost accessTokenTarget = new HttpHost("https://accounts.google.com/o/oauth2/token");

		HttpPost deviceCodeRequest = createExpectedDeviceCodeRequest();
		HttpPost accessTokenRequest = createExpectedAccessTokenRequest();

		HttpResponse deviceCodeResponse = HttpTestUtils.createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/authenticator/deviceCodeResponse.txt")));
		HttpResponse accessTokenPendingResponse = HttpTestUtils.createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/authenticator/accessTokenPendingResponse.txt")));
		HttpResponse accessTokenOkResponse = HttpTestUtils.createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/authenticator/accessTokenOkResponse.txt")));

		expectExecute(httpClientMock, deviceCodeTarget, deviceCodeRequest, deviceCodeResponse);
		expectExecute(httpClientMock, accessTokenTarget, accessTokenRequest, accessTokenPendingResponse);
		expectExecute(httpClientMock, accessTokenTarget, accessTokenRequest, accessTokenPendingResponse);
		expectExecute(httpClientMock, accessTokenTarget, accessTokenRequest, accessTokenOkResponse);

		EasyMock.replay(httpClientMock);

		Authenticator authenticator = new Authenticator(httpClientMock);
		authenticator.authenticate();
		String token = authenticator.getAccessToken();

		EasyMock.verify(httpClientMock);
		Assert.assertEquals("AUTHORIZATION_TOKEN", token);
	}

	protected HttpPost createExpectedAccessTokenRequest() throws UnsupportedEncodingException {
		HttpPost accessTokenRequest = new PrintableHttpPost("/o/oauth2/token");
		accessTokenRequest.addHeader("Host", "accounts.google.com");
		accessTokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		StringEntity entity = new StringEntity( //
				"client_id=CLIENT_ID.apps.googleusercontent.com" + //
				"&client_secret=CLIENT_SECRET" + //
				"&code=CODE" + //
				"&grant_type=http://oauth.net/grant_type/device/1.0");
		accessTokenRequest.setEntity(entity);
		return accessTokenRequest;
	}

	protected HttpPost createExpectedDeviceCodeRequest() throws UnsupportedEncodingException {
		HttpPost deviceCodeRequest = new PrintableHttpPost("/o/oauth2/device/code");
		deviceCodeRequest.addHeader("Host", "accounts.google.com");
		deviceCodeRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity entity = new StringEntity(
				"client_id=CLIENT_ID.apps.googleusercontent.com&scope=https://gdata.youtube.com");
		deviceCodeRequest.setEntity(entity);
		return deviceCodeRequest;
	}

	private void expectExecute(HttpClient httpClientMock, HttpHost accessTokenTarget,
			HttpPost accessTokenPendingRequest, HttpResponse accessTokenPendingResponse)
			throws ClientProtocolException, IOException {
		httpClientMock.execute(EasyMock.eq(accessTokenTarget), HttpTestUtils.requestMatches(accessTokenPendingRequest));
		EasyMock.expectLastCall().andReturn(accessTokenPendingResponse);
	}

}
