package com.github.geekarist.songs;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpClientCreator {

	private boolean proxyEnabled;
	private String proxyUser;
	private String proxyPass;
	private String proxyUrl;
	private int proxyPort;
	private String proxyDomain;

	public HttpClientCreator(boolean proxyEnabled, String proxyUser, String proxyPass, String proxyDomain, String proxyUrl, int proxyPort) {
		this.proxyEnabled = proxyEnabled;
		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;
		this.proxyDomain = proxyDomain;
		this.proxyUrl = proxyUrl;
		this.proxyPort = proxyPort;
	}

	public DefaultHttpClient createHttpClient() throws SongsLibException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (proxyEnabled) {
			configureClientForProxy(httpClient);
		}
		return httpClient;
	}

	private void configureClientForProxy(DefaultHttpClient httpClient) throws SongsLibException {
		String localHostName = getLocalHostName();

		HttpHost proxyHost = new HttpHost(proxyUrl, proxyPort);
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);

		httpClient.getCredentialsProvider().setCredentials( //
				new AuthScope(proxyUrl, proxyPort), //
				new NTCredentials(proxyUser, proxyPass, localHostName, proxyDomain));

	}

	private String getLocalHostName() throws SongsLibException {
		String proxyWorkstation;
		try {
			proxyWorkstation = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new SongsLibException("Error while retrieving local hostname for proxy authentification", e);
		}
		return proxyWorkstation;
	}

}
