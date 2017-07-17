package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.Lists;

public class PostRequestBuilder implements HeaderConstants {
	
	final HttpPost request;
	List<NameValuePair> nameValuePairs = Lists.newArrayList();
	String csrfToken;

	public PostRequestBuilder(String queryUrl, String csrfToken) {
		request = new HttpPost(queryUrl);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
		request.setConfig(requestConfig);
		this.csrfToken = csrfToken;
		//request.setHeader(MIME_TYPE_HEADER, FORM_MIME_TYPE);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", "MLDS Application");
	}
	
	public void addParam(String paramName, String paramValue) {
		nameValuePairs.add(new BasicNameValuePair(paramName, paramValue));
	}

	public HttpPost toRequest(HttpCookie authenticationCookie) throws UnsupportedEncodingException {
		nameValuePairs.add(new BasicNameValuePair("_csrf", csrfToken));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		if (authenticationCookie != null) {
			request.setHeader(SET_COOKIE, authenticationCookie.toString());
		}
		return request;
	}
	
	public HttpPost toRequest() throws UnsupportedEncodingException {
		return toRequest(null);
	}

	public HttpGet toGetRequest(HttpCookie authenticationCookie) {
		// TODO Auto-generated method stub
		return null;
	}
}