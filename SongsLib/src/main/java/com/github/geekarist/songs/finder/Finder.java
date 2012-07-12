package com.github.geekarist.songs.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.geekarist.songs.HttpClientCreator;
import com.github.geekarist.songs.SongsLibException;

public class Finder {

	private static final Logger LOGGER = Logger.getLogger(Finder.class);

	private Configuration configuration;

	public Finder(Configuration conf) {
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
		HttpClientCreator clientCreator = buildHttpClientCreator();
		DefaultHttpClient httpClient = clientCreator.createHttpClient();
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

	protected HttpClientCreator buildHttpClientCreator() {
		return new HttpClientCreator(configuration.isProxyEnabled(), configuration.getProxyUser(),
				configuration.getProxyPass(), configuration.getProxyDomain(), configuration.getProxyUrl(),
				configuration.getProxyPort());
	}

	private String createRequest() {
		String req = String.format(
				"%s?api_key=%s&format=json&results=%d&style=%s&sort=song_hotttnesss-desc&min_tempo=%d&max_tempo=%d", //
				configuration.getEchoNestUrl(), configuration.getEchoNestApiKey(), nbResults, style, bpm - 1, bpm + 1);
		LOGGER.debug(req);
		return req;
	}
}
