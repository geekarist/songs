package com.github.geekarist.songs.listcreator;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.http.PrintableHttpPost;

public class ListCreator {

	private static final String API_HOSTNAME = "gdata.youtube.com";
	private static final String API_KEY = "AI39si4uPDnNHnZyEjzPz8rrHCJQ1s9Vy-cLhcaqgVYU6dr3SzUfi-TxOyHM0RZ6OeyNsuGI55TknpisiKRBHWlcczy3LNTvaA";
	private static final String API_URL = "/feeds/api/users/default/playlists?alt=jsonc";
	
	private HttpClient httpClient;

	protected ListCreator(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void create(String title, String description, List<String> tags) throws SongsLibException {
		try {
			HttpPost httpPost = new PrintableHttpPost(API_URL);
			httpPost.addHeader("Host", API_HOSTNAME);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Authorization", "AuthSub token=\"AUTHORIZATION_TOKEN\"");
			httpPost.addHeader("GData-Version", "2");
			httpPost.addHeader("X-GData-Key", API_KEY);
			httpPost.setEntity(new StringEntity(createRequest(title, description, tags)));
			httpClient.execute(new HttpHost(API_HOSTNAME), httpPost);
		} catch (IOException e) {
			throw new SongsLibException("Error while reading response of playlist creation service", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

	protected String createRequest(String title, String description, List<String> tags) {
		return String.format(
				"{ \"data\":{ \"title\":\"%s\", \"description\":\"%s\", \"tags\":[%s], \"privacy\":\"private\" } }",
				title, description, tagsToStr(tags));
	}

	private String tagsToStr(List<String> tags) {
		StringBuffer result = new StringBuffer();
		if (tags != null && tags.size() != 0) {
			result.append("\"");
			result.append(StringUtils.join(tags, "\",\""));
			result.append("\"");
		}
		return result.toString();
	}

}
