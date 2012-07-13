package com.github.geekarist.songs.listcreator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.easymock.EasyMock;
import org.junit.Test;

import com.github.geekarist.songs.Configuration;
import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.testutils.HttpTestUtils;

public class ListCreatorTest {

	@Test
	public void testCreate() throws ClientProtocolException, IOException, SongsLibException {
		HttpClient httpClientMock = EasyMock.createMock(HttpClient.class);
		ClientConnectionManager connectionManagerMock = EasyMock.createMock(ClientConnectionManager.class);

		HttpHost expectedTarget = new HttpHost("gdata.youtube.com");
		HttpPost expectedRequest = HttpTestUtils.createHttpRequest(FileUtils.readFileToString(new File(
				"src/test/resources/listcreator/createPlayListRequestEntity.txt")));
		HttpResponse response = HttpTestUtils.createHttpResponse(FileUtils.readFileToString(new File(
				"src/test/resources/listcreator/createPlayListResponse.txt")));

		expectExecute(httpClientMock, expectedTarget, expectedRequest, response);
		expectGetConnectionManager(httpClientMock, connectionManagerMock);
		expectShutdown(connectionManagerMock);

		Configuration configurationMock = EasyMock.createMock(Configuration.class);
		expectGetListApiKey(configurationMock, "API_KEY");

		EasyMock.replay(httpClientMock, connectionManagerMock, configurationMock);

		ListCreator creator = new ListCreator(httpClientMock, configurationMock);
		creator.create("List Title", "List Description", Arrays.asList("tag1", "tag2"));

		EasyMock.verify(httpClientMock, connectionManagerMock, configurationMock);
	}

	private void expectGetListApiKey(Configuration configurationMock, String apiKey) {
		configurationMock.getListApiKey();
		EasyMock.expectLastCall().andReturn(apiKey);
	}

	private void expectShutdown(ClientConnectionManager connectionManagerMock) {
		connectionManagerMock.shutdown();
		EasyMock.expectLastCall();
	}

	private void expectGetConnectionManager(HttpClient httpClientMock, ClientConnectionManager connectionManagerMock) {
		httpClientMock.getConnectionManager();
		EasyMock.expectLastCall().andReturn(connectionManagerMock);
	}

	private void expectExecute(HttpClient mock, HttpHost expectedTarget, HttpPost expectedRequest, HttpResponse response)
			throws ClientProtocolException, IOException {
		mock.execute(EasyMock.eq(expectedTarget), HttpTestUtils.requestMatches(expectedRequest));
		EasyMock.expectLastCall().andReturn(response);
	}

}
