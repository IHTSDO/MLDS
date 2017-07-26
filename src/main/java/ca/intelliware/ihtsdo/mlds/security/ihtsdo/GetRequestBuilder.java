package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

public class GetRequestBuilder implements HeaderConstants {
	
	final HttpGet request;
	String csrfToken;

	public GetRequestBuilder(String queryUrl, String csrfToken) {
		queryUrl += queryUrl.contains("?")?"&":"?";
		queryUrl += "_csrf=" + csrfToken;
		request = new HttpGet(queryUrl);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
		request.setConfig(requestConfig);
		this.csrfToken = csrfToken;
		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", "MLDS Application");
	}

	public HttpGet toRequest(HttpCookie authenticationCookie) throws UnsupportedEncodingException {
		if (authenticationCookie != null) {
			request.setHeader(SET_COOKIE, authenticationCookie.toString());
		}
		return request;
	}
	
	public HttpGet toRequest() throws UnsupportedEncodingException {
		return toRequest(null);
	}
}