package com.github.geekarist.songs.finder;

import java.io.IOException;
import java.io.PrintStream;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import com.github.geekarist.songs.Song;
import com.github.geekarist.songs.SongsLibException;
import com.github.geekarist.songs.finder.Launcher.MyScanner;

public class LauncherTest {

	@Test
	@Ignore
	public void testLaunch() throws IOException, SongsLibException {
		MyScanner scanner = EasyMock.createMock(MyScanner.class);
		PrintStream out = EasyMock.createMock(PrintStream.class);
		String confPath = "src/main/resources/songs.properties";

		expectPrint(out, "Enter bpm: ");
		expectNextInt(scanner, 100);
		expectPrint(out, "Enter style: ");
		expectNext(scanner, "rock");
		expectPrint(out, "Enter number of results: ");
		expectNextInt(scanner, 3);

		expectPrintf(out, "Found %d songs\n", 3);
		expectPrintln(out, new Song("Living Colour", "Cult of Personality"));
		expectPrintln(out, new Song("Alice Cooper", "I'm Eighteen"));
		expectPrintln(out, new Song("The Used", "I Caught Fire"));

		expectPrintf(out, "Please go to %s in your web browser and enter this code: %s",
				"http://www.google.com/device", "user_code");
		expectPrintfAnyStringArg(out, "Authorization OK, token = [%s]");

		EasyMock.replay(scanner, out);

		Launcher launcher = new Launcher(scanner, out, confPath);
		launcher.launch();

		EasyMock.verify(scanner, out);
	}

	private void expectPrintfAnyStringArg(PrintStream out, String string) {
		out.printf(EasyMock.eq(string), EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(out);
	}

	private void expectPrintf(PrintStream out, String fmt, String arg1, String arg2) {
		out.printf(EasyMock.eq(fmt), EasyMock.eq(arg1), EasyMock.eq(arg2));
		EasyMock.expectLastCall().andReturn(out);
	}

	private void expectPrintln(PrintStream out, Song song) {
		out.println(song);
		EasyMock.expectLastCall();
	}

	private void expectPrintf(PrintStream out, String format, int i) {
		out.printf(EasyMock.eq(format), EasyMock.eq(i));
		EasyMock.expectLastCall().andReturn(out);
	}

	private void expectNext(MyScanner scanner, String string) {
		scanner.next();
		EasyMock.expectLastCall().andReturn(string);
	}

	private void expectPrint(PrintStream out, String str) {
		out.print(EasyMock.eq(str));
		EasyMock.expectLastCall();
	}

	private void expectNextInt(MyScanner scanner, int i) {
		scanner.nextInt();
		EasyMock.expectLastCall().andReturn(i);
	}

}
