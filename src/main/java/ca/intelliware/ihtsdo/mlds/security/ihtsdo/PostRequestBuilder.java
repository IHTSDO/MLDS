package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

public class PostRequestBuilder {
	final HttpPost request;
	List<NameValuePair> nameValuePairs = Lists.newArrayList();

	public PostRequestBuilder(String queryUrl) {
		request = new HttpPost(queryUrl);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
		request.setConfig(requestConfig);
	}
	
	public void addParam(String paramName, String paramValue) {
		nameValuePairs.add(new BasicNameValuePair(paramName, paramValue));
	}

	public HttpPost toRequest() {
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs, Charsets.ISO_8859_1));
		return request;
	}
}