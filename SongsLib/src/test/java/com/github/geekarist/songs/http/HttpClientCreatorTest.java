package com.github.geekarist.songs.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.http.HttpClientCreator;

public class HttpClientCreatorTest {

	@Test
	public void testCreateHttpClientWithoutProxy() throws SongsLibException {
		HttpClientCreator httpClientCreator = new HttpClientCreator(false, "", "", "", "", 0);
		DefaultHttpClient httpClient = httpClientCreator.createHttpClient();
		Assert.assertNull(httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));
	}

	@Test
	public void testCreateHttpClientWithProxy() throws SongsLibException, UnknownHostException {
		HttpClientCreator httpClientCreator = new HttpClientCreator(true, "user", "pass", "domain", "host", 9999);
		DefaultHttpClient httpClient = httpClientCreator.createHttpClient();

		HttpHost expectedProxyHost = new HttpHost("host", 9999);
		Credentials expectedCredentials = new NTCredentials("user", "pass", InetAddress.getLocalHost().getHostName(),
				"domain");

		Assert.assertEquals(expectedProxyHost, httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));
		Assert.assertEquals(expectedCredentials,
				httpClient.getCredentialsProvider().getCredentials(new AuthScope("host", 9999)));
	}
}
