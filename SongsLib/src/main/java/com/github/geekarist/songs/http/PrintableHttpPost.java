package com.github.geekarist.songs.http;

import java.io.IOException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

public class PrintableHttpPost extends HttpPost {
	public PrintableHttpPost(String uri) {
		super(uri);
	}

	public PrintableHttpPost() {
		super();
	}

	@Override
	public String toString() {
		String uri = ObjectUtils.toString(getURI());
		String headers = ReflectionToStringBuilder.toString(getAllHeaders(), ToStringStyle.SHORT_PREFIX_STYLE);
		String entityStr;
		try {
			HttpEntity entity = getEntity();
			if (entity == null) {
				entityStr = null;
			} else {
				entityStr = EntityUtils.toString(entity);
			}
		} catch (ParseException e) {
			entityStr = "[ParseException when getting entity]";
		} catch (IOException e) {
			entityStr = "[IOException when getting entity]";
		}
		return uri + '|' + headers + '|' + entityStr;
	}
}
