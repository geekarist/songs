package com.github.geekarist.songs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SongFinder {

	private static final String PROXY_PASS = "xxx";
	private static final String PROXY_USER = "xxx";
	private static final String URL = "http://developer.echonest.com/api/v4/song/search";
	private static final String API_KEY = "N6E4NIOVYMTHNDM8J";
	
	private static final int PROXY_PORT = 8080;
	private static final String PROXY_URL = "ptx.proxy.corp.sopra";

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
		try {
			DefaultHttpClient httpClient = createHttpClient();
			HttpGet httpGet = new HttpGet(request);
			BasicResponseHandler responseHander = new BasicResponseHandler();
			HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT);
			return httpClient.execute(proxy, httpGet, responseHander);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading echo nest response", e);
		}
	}

	protected DefaultHttpClient createHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(PROXY_URL, PROXY_PORT),
                new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
		return httpClient;
	}

	private String createRequest() {
		return String.format(
				"%s?api_key=%s&format=json&results=%d&style=%s&sort=song_hotttnesss-desc&min_tempo=%d&max_tempo=%d", //
				URL, API_KEY, nbResults, style, bpm - 1, bpm + 1);
	}
}
