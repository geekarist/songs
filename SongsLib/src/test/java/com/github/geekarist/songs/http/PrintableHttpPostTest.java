package com.github.geekarist.songs.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

public class PrintableHttpPostTest {

	@Test
	public void testToString() throws URISyntaxException, UnsupportedEncodingException {
		HttpPost httpPost = new PrintableHttpPost();
		httpPost.addHeader("name", "value");
		httpPost.addHeader("name2", "value2");
		httpPost.setURI(new URI("/xxx/yy"));
		httpPost.setEntity(new StringEntity("zzz"));

		Assert.assertEquals("/xxx/yy|Header[][{name: value,name2: value2}]|zzz", httpPost.toString());
	}

}
