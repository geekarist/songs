package com.github.geekarist.songs.listcreator;

import junit.framework.Assert;

import org.apache.http.client.HttpClient;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

public class AuthenticatorTest {

	@Test
	@Ignore
	public void testAuthenticate() {
		HttpClient httpClientMock = EasyMock.createMock(HttpClient.class);

		String deviceCodeRequest = "";
		String deviceCodeResponse = "";
		String accesTokenRequest = "";
		String accesTokenPendingResponse = "";
		String accesTokenOkResponse = "";

		expectExecuteRequestDeviceCode(httpClientMock, deviceCodeRequest, deviceCodeResponse);
		expectRequestAccessToken(httpClientMock, accesTokenRequest, accesTokenPendingResponse);
		expectRequestAccessToken(httpClientMock, accesTokenRequest, accesTokenPendingResponse);
		expectRequestAccessToken(httpClientMock, accesTokenRequest, accesTokenOkResponse);
		
		EasyMock.replay(httpClientMock);

		Authenticator authenticator = new Authenticator(httpClientMock);
		authenticator.authenticate();
		String token = authenticator.getAccessToken();

		EasyMock.verify(httpClientMock);
		Assert.assertEquals("AUTHORIZATION_TOKEN", token);
	}

	private void expectRequestAccessToken(HttpClient httpClientMock, Object accesTokenRequest,
			Object accesTokenPendingResponse) {
		// TODO Auto-generated method stub

	}

	private void expectExecuteRequestDeviceCode(HttpClient httpClientMock, Object deviceCodeRequest,
			Object deviceCodeResponse) {
		// TODO Auto-generated method stub

	}

}
