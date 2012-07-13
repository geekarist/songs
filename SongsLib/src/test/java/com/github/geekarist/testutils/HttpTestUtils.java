package com.github.geekarist.testutils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import com.github.geekarist.songs.http.PrintableHttpPost;

public class HttpTestUtils {

	public static HttpResponse createHttpResponse(String responseContents) throws UnsupportedEncodingException {
		HttpResponse response = new BasicHttpResponse( //
				new BasicStatusLine( //
						new ProtocolVersion("HTTP", 1, 1), 201, "OK"));
		response.setEntity(new StringEntity(responseContents));
		return response;
	}

	public static HttpPost requestMatches(HttpPost expectedRequest) {
		EasyMock.reportMatcher(new HttpPostMatcher(expectedRequest));
		return null;
	}

	private static class HttpPostMatcher implements IArgumentMatcher {

		private HttpPost expectedRequest;

		public HttpPostMatcher(HttpPost expectedRequest) {
			this.expectedRequest = expectedRequest;
		}

		@Override
		public boolean matches(Object argument) {
			URI expectedUri = expectedRequest.getURI();
			String expectedHeaders = ReflectionToStringBuilder.toString( //
					expectedRequest.getAllHeaders(), //
					ToStringStyle.SHORT_PREFIX_STYLE);

			HttpPost actualRequest = (HttpPost) argument;
			URI actualUri = actualRequest.getURI();
			String actualHeaders = ReflectionToStringBuilder.toString( //
					actualRequest.getAllHeaders(), //
					ToStringStyle.SHORT_PREFIX_STYLE);

			try {
				String expectedEntity = StringUtils.trim(EntityUtils.toString(expectedRequest.getEntity()));
				String actualEntity = StringUtils.trim(EntityUtils.toString(actualRequest.getEntity()));

				boolean urisEqual = ObjectUtils.equals(expectedUri, actualUri);
				boolean headersEqual = ObjectUtils.equals(expectedHeaders, actualHeaders);
				boolean entitiesEqual = ObjectUtils.equals(expectedEntity, actualEntity);

				return urisEqual && headersEqual && entitiesEqual;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append(ObjectUtils.toString(expectedRequest));
		}

	}

	public static HttpPost createHttpRequest(String requestContents) throws IOException {
		HttpPost httpPost = new PrintableHttpPost("/feeds/api/users/default/playlists?alt=jsonc");
		httpPost.addHeader("Host", "gdata.youtube.com");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Authorization", "AuthSub token=\"AUTHORIZATION_TOKEN\"");
		httpPost.addHeader("GData-Version", "2");
		httpPost.addHeader("X-GData-Key", "API_KEY");
		HttpEntity entity = new StringEntity(requestContents);
		httpPost.setEntity(entity);
		return httpPost;
	}

}
