package com.github.geekarist.songs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easymock.EasyMock;
import org.junit.Test;

public class SongFinderTest {

	@Test
	public void testFind() throws SongsLibException, ClientProtocolException, IOException {
		// Setup
		SongFinder finder = EasyMock.createMockBuilder(SongFinder.class) //
				.addMockedMethod("createHttpClient") //
				.createMock();

		DefaultHttpClient httpClientMock = EasyMock.createMock(DefaultHttpClient.class);

		expectCreateHttpClient(finder, httpClientMock);
		expectExecute(httpClientMock);

		EasyMock.replay(finder, httpClientMock);

		// Test
		finder.chooseNbResults(10);
		finder.chooseStyle("rock");
		finder.chooseBpm(75);
		List<Song> result = finder.findSongs();

		// Assert
		EasyMock.verify(finder, httpClientMock);
		Assert.assertEquals(createExpectedResult(), result);
	}

	private void expectCreateHttpClient(SongFinder finder, DefaultHttpClient httpClientMock) {
		finder.createHttpClient();
		EasyMock.expectLastCall().andReturn(httpClientMock);
	}

	private void expectExecute(HttpClient httpClientMock) throws IOException, ClientProtocolException {
		httpClientMock.execute(EasyMock.isA(HttpHost.class), EasyMock.isA(HttpGet.class),
				EasyMock.isA(BasicResponseHandler.class));
		EasyMock.expectLastCall().andReturn(
				FileUtils.readFileToString(new File("src/test/resources/echoNestTestResponse.txt")));
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
