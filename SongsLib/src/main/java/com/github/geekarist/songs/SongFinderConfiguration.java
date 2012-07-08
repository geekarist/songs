package com.github.geekarist.songs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SongFinderConfiguration {

	private boolean proxyEnabled = false;
	private String proxyPass = "";
	private String proxyUser = "";
	private int proxyPort = 0;
	private String proxyUrl = "";

	private String echoNestUrl = "";
	private String echoNestApiKey = "";

	public SongFinderConfiguration(String path) throws SongsLibException {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(path));
		} catch (FileNotFoundException e) {
			throw new SongsLibException(String.format("Error while loading configuration file [%s]: file not found",
					path), e);
		} catch (IOException e) {
			throw new SongsLibException(
					String.format("Error while loading configuration file [%s]: io exception", path), e);
		}
		proxyEnabled = Boolean.parseBoolean(properties.getProperty("proxy.enabled"));
		proxyPass = properties.getProperty("proxy.pass");
		proxyUser = properties.getProperty("proxy.user");
		proxyPort = Integer.parseInt(properties.getProperty("proxy.port"));
		proxyUrl = properties.getProperty("proxy.url");
		echoNestUrl = properties.getProperty("echonest.url");
		echoNestApiKey = properties.getProperty("echonest.api.key");
	}

	public SongFinderConfiguration() {
	}

	public String getProxyPass() {
		return proxyPass;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public String getEchoNestUrl() {
		return echoNestUrl;
	}

	public String getEchoNestApiKey() {
		return echoNestApiKey;
	}

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

}
