package com.github.geekarist.songs.client;

import java.util.List;

import org.junit.Test;

import com.github.geekarist.songs.Song;

public class SongSearchPresenterTest {
	@Test
	public void testSearchSong() {
		Object searchSongService = null;
		Object displaySearchResultEvent = null;
		Object appController = null;
		List<Song> songs = null;

		expectSearchSong(searchSongService, "Radiohead", "Karma", songs);
		expectReceiveEvent(appController, displaySearchResultEvent);
		expectDoDisplaySearchResults(appController, songs);
		
		SearchPresenter searchPresenter = new SearchPresenter(searchSongService);
		fillText(searchPresenter.getDisplay().getArtistText(), "Radiohead");
		fillText(searchPresenter.getDisplay().getTitleText(), "Karma");
		click(searchPresenter.getDisplay().getSearchButton());
	}

	private void expectDoDisplaySearchResults(Object appController, List<Song> songs) {
		// TODO Auto-generated method stub
		
	}

	private void expectReceiveEvent(Object appController, Object displaySearchResultEvent) {
		// TODO Auto-generated method stub
		
	}

	private void expectAppControllerReceivesEvent(Object displaySearchResultEvent, Object displaySearchResultEvent2) {
		// TODO Auto-generated method stub
		
	}

	private void expectSearchSong(Object searchSongService, String string, String string2, List<Song> songs) {
		// TODO Auto-generated method stub
		
	}

	private void fillText(Object titleText, String string) {
		// TODO Auto-generated method stub
		
	}

	private void click(Object searchButton) {
		// TODO Auto-generated method stub
		
	}
}
