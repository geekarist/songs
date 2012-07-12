package com.github.geekarist.songs.listcreator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Ignore;
import org.junit.Test;

import com.github.geekarist.songs.SongsLibException;

public class PlayListCreatorTest {

	@Test
	@Ignore
	public void testCreate() throws ClientProtocolException, IOException, SongsLibException {
		HttpClient httpClientMock = EasyMock.createMock(HttpClient.class);
		ClientConnectionManager connectionManagerMock = EasyMock.createMock(ClientConnectionManager.class);

		HttpHost expectedTarget = new HttpHost("gdata.youtube.com");
		HttpPost expectedRequest = createHttpRequest(FileUtils.readFileToString(new File(
				"src/test/resources/youtubeplaylistcreator/createPlayListRequestEntity.txt")));
		HttpResponse response = createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/youtubeplaylistcreator/createPlayListResponse.txt")));

		expectExecute(httpClientMock, expectedTarget, expectedRequest, response);
		expectGetConnectionManager(httpClientMock, connectionManagerMock);
		expectShutdown(connectionManagerMock);

		EasyMock.replay(httpClientMock);

		PlaylistCreator creator = new PlaylistCreator(httpClientMock);
		creator.create("List Title", "List Description", Arrays.asList("tag1", "tag2"));

		EasyMock.verify(httpClientMock);
	}

	private void expectShutdown(ClientConnectionManager connectionManagerMock) {
		connectionManagerMock.shutdown();
		EasyMock.expectLastCall();
	}

	private void expectGetConnectionManager(HttpClient httpClientMock, ClientConnectionManager connectionManagerMock) {
		httpClientMock.getConnectionManager();
		EasyMock.expectLastCall().andReturn(connectionManagerMock);
	}

	protected HttpPost createHttpRequest(String requestContents) throws IOException {
		HttpPost httpPost = new HttpPost("/feeds/api/users/default/playlists?alt=jsonc");
		httpPost.addHeader("Host", "gdata.youtube.com");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Authorization", "AuthSub token=\"AUTHORIZATION_TOKEN\"");
		httpPost.addHeader("GData-Version", "2");
		httpPost.addHeader("X-GData-Key", "DEVELOPER_KEY");
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ReaderInputStream(new StringReader(requestContents)));
		httpPost.setEntity(entity);
		return httpPost;
	}

	private void expectExecute(HttpClient mock, HttpHost expectedTarget, HttpPost expectedRequest, HttpResponse response)
			throws ClientProtocolException, IOException {
		mock.execute(EasyMock.eq(expectedTarget), requestMatches(expectedRequest));
		EasyMock.expectLastCall().andReturn(response);
	}

	private HttpPost requestMatches(HttpPost expectedRequest) {
		EasyMock.reportMatcher(new HttpPostMatcher(expectedRequest));
		return null;
	}

	private class HttpPostMatcher implements IArgumentMatcher {

		private HttpPost expectedRequest;

		public HttpPostMatcher(HttpPost expectedRequest) {
			this.expectedRequest = expectedRequest;
		}

		@Override
		public boolean matches(Object argument) {
			HttpPost actualRequest = (HttpPost) argument;
			URI uri = expectedRequest.getURI();
			Header[] headers = expectedRequest.getAllHeaders();
			HttpEntity entity = expectedRequest.getEntity();

			try {
				return ObjectUtils.equals(uri, actualRequest.getURI()) //
						&& ObjectUtils.equals(headers, actualRequest.getAllHeaders()) //
						&& ObjectUtils.equals(EntityUtils.toString(entity),
								EntityUtils.toString(actualRequest.getEntity()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append("requestMatches(");
			buffer.append(ObjectUtils.toString(expectedRequest.getURI()) + ", ");
			buffer.append(ObjectUtils.toString(expectedRequest.getAllHeaders()) + ", ");
			buffer.append("[...]");
			buffer.append(")");
		}

	}

	protected HttpResponse createHttpResponse(String responseContents) {
		HttpResponse response = new BasicHttpResponse( //
				new BasicStatusLine( //
						new ProtocolVersion("HTTP", 1, 1), 201, "OK"));
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ReaderInputStream(new StringReader(responseContents)));
		response.setEntity(entity);
		return response;
	}
}
