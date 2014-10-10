package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
public class HttpAuthAdaptor {
	static final String PARAM_USERNAME = "username";
	static final String PARAM_QUERY_NAME = "queryName";
	static final String PARAM_APP_NAME = "appName";
	static final String PARAM_PASSWORD = "password";

	String queryUrl;
	String applicationName = "MLDS";
	HttpClient httpClient = HttpClients.createDefault();
	
	@Resource
	ObjectMapper objectMapper;

	public HttpAuthAdaptor() {
	}
	
	public HttpAuthAdaptor(String url) {
		this();
		queryUrl = url;
	}

	public boolean checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException, ClientProtocolException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addParam(PARAM_QUERY_NAME, "getUserByNameAuth");
		builder.addParam(PARAM_USERNAME, username);
		builder.addParam(PARAM_PASSWORD, password);
		HttpPost request = builder.toRequest();
	    
		HttpResponse response = httpClient.execute(request);
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			boolean result;
			if (statusCode == 200) {
				result = true;
			} else if (statusCode == 401) {
				result = false;
			} else {
				throw new IOException("Authentication service returned unexpected value: " + statusCode);
			}
			
			return result;
		} finally {
			request.releaseConnection();
		}
	}

	public CentralAuthUserInfo getUserInfo(String username) throws IOException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addParam(PARAM_QUERY_NAME, "getUserByName");
		builder.addParam(PARAM_USERNAME, username);
		HttpPost request = builder.toRequest();
	    
		HttpResponse response = httpClient.execute(request);
		
		HttpEntity entity = new BufferedHttpEntity(response.getEntity());
		
		try {
			return objectMapper.readValue(entity.getContent(), CentralAuthUserInfo.class);
		} catch (JsonParseException e) {
			// the system returns 200 "NO RESPONSE" instead of a 404 on no-user.  Return null
			String body = EntityUtils.toString(entity);
			if ("NO RESPONSE".equals(body)) {
				return null;
			} else {
				throw new IllegalStateException("Unexpected JSON: " + body, e);
			}
		} finally {
			request.releaseConnection();
		}
	}
	
	static class AuthoritiesResponse {
		public List<CentralAuthUserPermission> perms;
	}
	
	public List<CentralAuthUserPermission> getUserPermissions(String username) throws ClientProtocolException, IOException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addParam(PARAM_QUERY_NAME, "getUserAppPerms");
		builder.addParam(PARAM_USERNAME, username);
		builder.addParam(PARAM_APP_NAME, applicationName);
		HttpPost request = builder.toRequest();
		
		HttpResponse response = httpClient.execute(request);

		try {
			AuthoritiesResponse authResponse = objectMapper.readValue(response.getEntity().getContent(), AuthoritiesResponse.class);
			return authResponse.perms;
		} finally {
			request.releaseConnection();
		}
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	
}