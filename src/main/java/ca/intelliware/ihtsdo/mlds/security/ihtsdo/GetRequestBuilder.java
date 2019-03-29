package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.net.HttpCookie;

public class GetRequestBuilder implements HeaderConstants {
	
	private final HttpGet request;

	public GetRequestBuilder(String queryUrl) {
		queryUrl += queryUrl.contains("?")?"&":"?";
		request = new HttpGet(queryUrl);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
		request.setConfig(requestConfig);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", "MLDS Application");
	}

	public HttpGet toRequest(HttpCookie authenticationCookie) {
		if (authenticationCookie != null) {
			request.setHeader(SET_COOKIE, authenticationCookie.toString());
		}
		return request;
	}
	
}
