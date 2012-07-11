package com.github.geekarist.songs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.easymock.EasyMock;
import org.junit.Test;

public class FinderTest {

	@Test
	public void testFind() throws SongsLibException, ClientProtocolException, IOException {
		// Setup
		Configuration songFinderConfiguration = new Configuration();
		Finder finder = EasyMock.createMockBuilder(Finder.class) //
				.addMockedMethod("buildHttpClientCreator") //
				.withConstructor(Configuration.class) //
				.withArgs(songFinderConfiguration) //
				.createMock();
		HttpClientCreator httpClientCreatorMock = EasyMock.createMockBuilder(HttpClientCreator.class) //
				.addMockedMethod("createHttpClient") //
				.withConstructor(boolean.class, String.class, String.class, String.class, String.class, int.class) //
				.withArgs(false, null, null, null, null, 0) //
				.createMock();
		DefaultHttpClient httpClientMock = EasyMock.createMock(DefaultHttpClient.class);

		expectBuildHttpClientCreator(finder, httpClientCreatorMock);
		expectCreateHttpClient(httpClientCreatorMock, httpClientMock);
		expectExecute(httpClientMock);
		expectGetConnectionManager(httpClientMock);

		EasyMock.replay(finder, httpClientCreatorMock, httpClientMock);

		// Test
		finder.chooseNbResults(10);
		finder.chooseStyle("rock");
		finder.chooseBpm(75);
		List<Song> result = finder.findSongs();

		// Assert
		EasyMock.verify(finder, httpClientCreatorMock, httpClientMock);
		Assert.assertEquals(createExpectedResult(), result);
	}

	private void expectBuildHttpClientCreator(Finder finder, HttpClientCreator httpClientCreatorMock) {
		finder.buildHttpClientCreator();
		EasyMock.expectLastCall().andReturn(httpClientCreatorMock);
	}

	private void expectGetConnectionManager(DefaultHttpClient httpClientMock) {
		httpClientMock.getConnectionManager();
		EasyMock.expectLastCall().andReturn(new BasicClientConnectionManager());
	}

	private void expectCreateHttpClient(HttpClientCreator creator, DefaultHttpClient httpClientMock)
			throws SongsLibException {
		creator.createHttpClient();
		EasyMock.expectLastCall().andReturn(httpClientMock);
	}

	private void expectExecute(HttpClient httpClientMock) throws IOException, ClientProtocolException {
		httpClientMock.execute(EasyMock.isA(HttpGet.class), EasyMock.isA(BasicResponseHandler.class));
		EasyMock.expectLastCall().andReturn(
				FileUtils.readFileToString(new File("src/test/resources/songfinder/echoNestTestResponse.txt")));
	}

	private List<Song> createExpectedResult() {
		return Arrays.asList( //
				new Song("Pearl Jam", "Once"), //
				new Song("Nazareth", "Love Hurts"), //
				new Song("Elvis Costello", "Welcome To The Working Week"), //
				new Song("Pearl Jam", "Alive (2004 Remix)"), //
				new Song("Chris Isaak", "Two Hearts"), //
				new Song("Dispatch", "Mission"), //
				new Song("Phish", "Fee"), //
				new Song("Neil Diamond", "If You Know What I Mean"), //
				new Song("Pearl Jam", "Insignificance"), //
				new Song("Pearl Jam", "Insignificance"));
	}

}
