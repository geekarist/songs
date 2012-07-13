package com.github.geekarist.songs.listcreator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;

import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.http.PrintableHttpPost;

public class ListCreatorTest {

	@Test
	public void testCreate() throws ClientProtocolException, IOException, SongsLibException {
		HttpClient httpClientMock = EasyMock.createMock(HttpClient.class);
		ClientConnectionManager connectionManagerMock = EasyMock.createMock(ClientConnectionManager.class);

		HttpHost expectedTarget = new HttpHost("gdata.youtube.com");
		HttpPost expectedRequest = createHttpRequest(FileUtils.readFileToString(new File(
				"src/test/resources/listcreator/createPlayListRequestEntity.txt")));
		HttpResponse response = createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/listcreator/createPlayListResponse.txt")));

		expectExecute(httpClientMock, expectedTarget, expectedRequest, response);
		expectGetConnectionManager(httpClientMock, connectionManagerMock);
		expectShutdown(connectionManagerMock);

		EasyMock.replay(httpClientMock, connectionManagerMock);
		
		ListCreator creator = new ListCreator(httpClientMock);
		creator.create("List Title", "List Description", Arrays.asList("tag1", "tag2"));

		EasyMock.verify(httpClientMock, connectionManagerMock);
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
		HttpPost httpPost = new PrintableHttpPost("/feeds/api/users/default/playlists?alt=jsonc");
		httpPost.addHeader("Host", "gdata.youtube.com");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Authorization", "AuthSub token=\"AUTHORIZATION_TOKEN\"");
		httpPost.addHeader("GData-Version", "2");
		httpPost.addHeader("X-GData-Key",
				"AI39si4uPDnNHnZyEjzPz8rrHCJQ1s9Vy-cLhcaqgVYU6dr3SzUfi-TxOyHM0RZ6OeyNsuGI55TknpisiKRBHWlcczy3LNTvaA");
		HttpEntity entity = new StringEntity(requestContents);
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
			URI expectedUri = expectedRequest.getURI();
			String expectedHeaders = ReflectionToStringBuilder.toString( //
					expectedRequest.getAllHeaders(), //
					ToStringStyle.SHORT_PREFIX_STYLE);

			HttpPost actualRequest = (HttpPost) argument;
			URI actualUri = actualRequest.getURI();
			String actualHeaders = ReflectionToStringBuilder.toString( //
					actualRequest.getAllHeaders(), //
					ToStringStyle.SHORT_PREFIX_STYLE);

			try {
				String expectedEntity = StringUtils.trim(EntityUtils.toString(expectedRequest.getEntity()));
				String actualEntity = StringUtils.trim(EntityUtils.toString(actualRequest.getEntity()));

				boolean urisEqual = ObjectUtils.equals(expectedUri, actualUri);
				boolean headersEqual = ObjectUtils.equals(expectedHeaders, actualHeaders);
				boolean entitiesEqual = ObjectUtils.equals(expectedEntity, actualEntity);

				return urisEqual && headersEqual && entitiesEqual;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append(ObjectUtils.toString(expectedRequest));
		}

	}

	protected HttpResponse createHttpResponse(String responseContents) throws UnsupportedEncodingException {
		HttpResponse response = new BasicHttpResponse( //
				new BasicStatusLine( //
						new ProtocolVersion("HTTP", 1, 1), 201, "OK"));
		response.setEntity(new StringEntity(responseContents));
		return response;
	}
}
