package com.github.geekarist.songs.listcreator;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.github.geekarist.songs.HttpClientCreator;
import com.github.geekarist.songs.SongsLibException;

public class PlaylistCreator {

	private HttpClient httpClient;

	public PlaylistCreator() throws SongsLibException {
		HttpClientCreator httpClientCreator = new HttpClientCreator( //
				false, "cpele", "Moyotu56", "emeaad", "http://ptx.proxy.corp.sopra", 8080);
		httpClient = httpClientCreator.createHttpClient();
	}

	protected PlaylistCreator(HttpClient httpClientMock) {
		this.httpClient = httpClientMock;
	}

	public void create(String title, String description, List<String> tags) throws SongsLibException {
		String url = "/feeds/api/users/default/playlists?alt=jsonc";
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("xx", "xx");
			httpPost.setEntity(new StringEntity("xx"));
			httpClient.execute(new HttpHost("gdata.youtube.com"), httpPost);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading response of playlist creation service", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

}
