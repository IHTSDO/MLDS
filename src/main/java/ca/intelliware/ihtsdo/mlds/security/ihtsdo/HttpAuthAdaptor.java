package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

@Service
public class HttpAuthAdaptor {

	//FIXME MLDS-170 MB extract to config
	String queryUrl = "https://usermanagement.ihtsdotools.org/security-web/query/";
	CloseableHttpClient httpClient = HttpClients.createDefault();
	
	@Resource
	ObjectMapper objectMapper;

	public HttpAuthAdaptor() {
		// TODO Auto-generated constructor stub
	}
	
	public HttpAuthAdaptor(String url) {
		this();
		queryUrl = url;
	}

	static class PostRequestBuilder {
		final HttpPost request;
		List<NameValuePair> nameValuePairs = Lists.newArrayList();

		public PostRequestBuilder(String queryUrl) {
			request = new HttpPost(queryUrl);
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
			request.setConfig(requestConfig);
		}
		
		public PostRequestBuilder addQueryName(String queryName) {
			nameValuePairs.add(new BasicNameValuePair("queryName", queryName));
			return this;
		}

		public void addUsername(String username) {
			nameValuePairs.add(new BasicNameValuePair("username", username));
		}

		public void addPassword(String password) {
			addParam("password", password);
		}

		public void addAppName(String appName) {
			addParam("appName", appName);
		}
		
		public void addParam(String paramName, String paramValue) {
			nameValuePairs.add(new BasicNameValuePair(paramName, paramValue));
		}

		public HttpPost toRequest() {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs, Charsets.ISO_8859_1));
			return request;
		}
	}

	public boolean checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException, ClientProtocolException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addQueryName("getUserByNameAuth");
		builder.addUsername(username);
		builder.addPassword(password);
		HttpPost request = builder.toRequest();
	    
		CloseableHttpResponse response = httpClient.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		boolean result;
		if (statusCode == 200) {
			result = true;
		} else if (statusCode == 401) {
			result = false;
		} else {
			throw new IOException("Authentication service returned unexpected value: " + statusCode);
		}
		
		Closeables.close(response, true);
		
		return result;
	}

	public CentralAuthUserInfo getUserInfo(String username) throws ClientProtocolException, IOException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addQueryName("getUserByName");
		builder.addUsername(username);
		HttpPost request = builder.toRequest();
	    
		CloseableHttpResponse response = httpClient.execute(request);
		
		return objectMapper.readValue(response.getEntity().getContent(), CentralAuthUserInfo.class);
	}
	
	static class AuthoritiesResponse {
		List<CentralAuthUserPermission> perms;

		public List<CentralAuthUserPermission> getPerms() {
			return perms;
		}

		public void setPerms(List<CentralAuthUserPermission> perms) {
			this.perms = perms;
		}
	}
	
	public List<CentralAuthUserPermission> getUserPermissions(String username) throws ClientProtocolException, IOException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addQueryName("getUserAppPerms");
		builder.addUsername(username);
		//FIXME MLDS-170 MB extract to config
		builder.addAppName("MLDS");
		HttpPost request = builder.toRequest();
		
		CloseableHttpResponse response = httpClient.execute(request);
		
		AuthoritiesResponse authResponse = objectMapper.readValue(response.getEntity().getContent(), AuthoritiesResponse.class);
		
		return authResponse.getPerms();
	}
	
	
}