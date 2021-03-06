package com.github.geekarist.songs;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void test() throws SongsLibException {
		Configuration configuration = new Configuration(
				"src/test/resources/testsongs.properties");

		Assert.assertEquals(true, configuration.isProxyEnabled());
		Assert.assertEquals("username", configuration.getProxyUser());
		Assert.assertEquals("password", configuration.getProxyPass());
		Assert.assertEquals("domain", configuration.getProxyDomain());
		Assert.assertEquals(9099, configuration.getProxyPort());
		Assert.assertEquals("proxy.host.com", configuration.getProxyUrl());
		Assert.assertEquals("http://url.to.api/xx/yy/zz", configuration.getEchoNestUrl());
		Assert.assertEquals("FQDKK41945DJ4JF0", configuration.getEchoNestApiKey());
		
		Assert.assertEquals("LKJSDQFQSKDJFQSLJF", configuration.getListApiKey());
	}

}
