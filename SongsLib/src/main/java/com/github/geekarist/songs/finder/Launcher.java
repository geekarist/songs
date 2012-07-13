package com.github.geekarist.songs.finder;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import com.github.geekarist.songs.Configuration;
import com.github.geekarist.songs.Song;
import com.github.geekarist.songs.SongsLibException;

public class Launcher {

	public static class MyScanner {
		Scanner scanner;

		public MyScanner(InputStream in) {
			this.scanner = new Scanner(in);
		}

		public int nextInt() {
			return scanner.nextInt();
		}

		public String next() {
			return scanner.next();
		}
	}

	private MyScanner scanner;
	private PrintStream out;
	private String confPath;

	public Launcher(MyScanner scanner, PrintStream out, String confPath) {
		this.scanner = scanner;
		this.out = out;
		this.confPath = confPath;
	}

	public static void main(String[] args) throws SongsLibException {
		Launcher launcher = new Launcher(new MyScanner(System.in), System.out, "src/main/resources/songs.properties");
		launcher.launch();
	}

	public void launch() throws SongsLibException {
		out.print("Enter bpm: ");
		int bpm = scanner.nextInt();
		out.print("Enter style: ");
		String style = scanner.next();
		out.print("Enter number of results: ");
		int nbResults = scanner.nextInt();

		Finder finder = new Finder(new Configuration(confPath));
		finder.chooseBpm(bpm);
		finder.chooseNbResults(nbResults);
		finder.chooseStyle(style);
		List<Song> findSongs = finder.findSongs();
		out.printf("Found %d songs\n", findSongs.size());
		for (Song s : findSongs) {
			out.println(s);
		}
	}

}
