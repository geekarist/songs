package com.github.geekarist.songs;

import junit.framework.Assert;

import org.junit.Test;

public class SongFinderConfigurationTest {

	@Test
	public void test() throws SongsLibException {
		SongFinderConfiguration configuration = new SongFinderConfiguration(
				"src/test/resources/testsongfinder.properties");

		Assert.assertEquals(true, configuration.isProxyEnabled());
		Assert.assertEquals("username", configuration.getProxyUser());
		Assert.assertEquals("password", configuration.getProxyPass());
		Assert.assertEquals(9099, configuration.getProxyPort());
		Assert.assertEquals("proxy.host.com", configuration.getProxyUrl());
		Assert.assertEquals("http://url.to.api/xx/yy/zz", configuration.getEchoNestUrl());
		Assert.assertEquals("FQDKK41945DJ4JF0", configuration.getEchoNestApiKey());
	}

}
