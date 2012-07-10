package com.github.geekarist.songs;

import org.junit.Test;

import junit.framework.Assert;

public class YoutubePlayListCreatorTest {

	public void testCreate() {
		YoutubePlaylistCreator creator = new YoutubePlaylistCreator();
		creator.create("List Title");

		Assert.assertTrue(playListExists("List Title"));
	}

	private boolean playListExists(String title) {
		// TODO Auto-generated method stub
		return false;
	}

}
