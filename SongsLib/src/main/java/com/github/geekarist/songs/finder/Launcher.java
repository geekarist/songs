package com.github.geekarist.songs.finder;

import java.util.List;
import java.util.Scanner;

import com.github.geekarist.songs.Song;
import com.github.geekarist.songs.SongsLibException;

public class Launcher {

	public static void main(String[] args) throws SongsLibException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter bpm: ");
		int bpm = scanner.nextInt();
		System.out.print("Enter style: ");
		String style = scanner.next();
		System.out.print("Enter number of results: ");
		int nbResults = scanner.nextInt();
		
		Finder finder = new Finder(new Configuration("src/main/resources/songfinder.properties"));
		finder.chooseBpm(bpm);
		finder.chooseNbResults(nbResults);
		finder.chooseStyle(style);
		List<Song> findSongs = finder.findSongs();
		System.out.printf("Found %d songs\n", findSongs.size());
		for (Song s : findSongs) {
			System.out.println(s);
		}
	}
	
}
