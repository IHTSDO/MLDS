package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.List;

public class PostRequestBuilder implements HeaderConstants {
	
	private final HttpPost request;
    private List<NameValuePair> nameValuePairs = Lists.newArrayList();

	public PostRequestBuilder(String queryUrl) {
		request = new HttpPost(queryUrl);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
		request.setConfig(requestConfig);
		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", "MLDS Application");
	}
	
	public void addParam(String paramName, String paramValue) {
		nameValuePairs.add(new BasicNameValuePair(paramName, paramValue));
	}

	public HttpPost toRequest(HttpCookie authenticationCookie) throws UnsupportedEncodingException {
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		if (authenticationCookie != null) {
			request.setHeader(SET_COOKIE, authenticationCookie.toString());
		}
		return request;
	}
	
	public HttpPost toRequest() throws UnsupportedEncodingException {
		return toRequest(null);
	}

}
