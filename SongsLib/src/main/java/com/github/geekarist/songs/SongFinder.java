package com.github.geekarist.songs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SongFinder {

	private static final Logger LOGGER = Logger.getLogger(SongFinder.class);

	private SongFinderConfiguration configuration;

	public SongFinder(SongFinderConfiguration conf) {
		this.configuration = conf;
	}

	private String style;
	private int bpm;
	private int nbResults;

	public void chooseStyle(String style) {
		this.style = style;
	}

	public void chooseBpm(int i) {
		this.bpm = i;
	}

	public void chooseNbResults(int i) {
		this.nbResults = i;
	}

	public List<Song> findSongs() throws SongsLibException {
		String request = createRequest();
		String response = callService(request);
		return mapResponse(response);
	}

	private List<Song> mapResponse(String response) throws SongsLibException {
		List<Song> result = new ArrayList<Song>();
		try {
			JSONObject jsonServiceResponse = new JSONObject(response);
			JSONObject jsonResponse = jsonServiceResponse.getJSONObject("response");
			JSONArray jsonSongArray = jsonResponse.getJSONArray("songs");
			for (int i = 0; !jsonSongArray.isNull(i); i++) {
				JSONObject jsonSong = jsonSongArray.getJSONObject(i);
				Song song = new Song(jsonSong.getString("artist_name"), jsonSong.getString("title"));
				result.add(song);
			}
		} catch (JSONException e) {
			throw new SongsLibException("Error while mapping echo nest response", e);
		}
		return result;
	}

	private String callService(String request) throws SongsLibException {
		DefaultHttpClient httpClient = createHttpClient();
		try {
			HttpGet httpGet = new HttpGet(request);
			BasicResponseHandler responseHander = new BasicResponseHandler();
			return httpClient.execute(httpGet, responseHander);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading echo nest response", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	protected DefaultHttpClient createHttpClient() throws SongsLibException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (configuration.isProxyEnabled()) {
			configureClientForProxy(httpClient);
		}
		return httpClient;
	}

	private void configureClientForProxy(DefaultHttpClient httpClient) throws SongsLibException {
		String proxyUser = configuration.getProxyUser();
		String proxyPass = configuration.getProxyPass();
		String proxyUrl = configuration.getProxyUrl();
		int proxyPort = configuration.getProxyPort();
		String localHostName = getLocalHostName();

		HttpHost proxyHost = new HttpHost(proxyUrl, proxyPort);
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);

		httpClient.getCredentialsProvider().setCredentials( //
				new AuthScope(proxyUrl, proxyPort), //
				new NTCredentials(proxyUser, proxyPass, localHostName, "emeaad"));
		
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

	private String createRequest() {
		String req = String.format(
				"%s?api_key=%s&format=json&results=%d&style=%s&sort=song_hotttnesss-desc&min_tempo=%d&max_tempo=%d", //
				configuration.getEchoNestUrl(), configuration.getEchoNestApiKey(), nbResults, style, bpm - 1, bpm + 1);
		LOGGER.debug(req);
		return req;
	}
}
