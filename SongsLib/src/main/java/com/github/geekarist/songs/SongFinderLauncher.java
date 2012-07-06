package com.github.geekarist.songs;

import java.util.List;
import java.util.Scanner;

public class SongFinderLauncher {

	public static void main(String[] args) throws SongsLibException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter bpm: ");
		int bpm = scanner.nextInt();
		System.out.print("Enter style: ");
		String style = scanner.next();
		System.out.print("Enter number of results: ");
		int nbResults = scanner.nextInt();
		
		SongFinder finder = new SongFinder();
		finder.chooseBpm(bpm);
		finder.chooseNbResults(nbResults);
		finder.chooseStyle(style);
		List<Song> findSongs = finder.findSongs();
		System.out.printf("Found %d songs", findSongs.size());
		for (Song s : findSongs) {
			System.out.println(s);
		}
	}
	
}
