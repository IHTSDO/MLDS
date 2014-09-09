package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Closeables;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
@Service
public class HttpAuthAdaptor {
	static final String PARAM_USERNAME = "username";
	static final String PARAM_QUERY_NAME = "queryName";
	static final String PARAM_APP_NAME = "appName";
	static final String PARAM_PASSWORD = "password";

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

	public boolean checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException, ClientProtocolException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addParam(PARAM_QUERY_NAME, "getUserByNameAuth");
		builder.addParam(PARAM_USERNAME, username);
		builder.addParam(PARAM_PASSWORD, password);
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

	public CentralAuthUserInfo getUserInfo(String username) throws IOException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl);
		builder.addParam(PARAM_QUERY_NAME, "getUserByName");
		builder.addParam(PARAM_USERNAME, username);
		HttpPost request = builder.toRequest();
	    
		CloseableHttpResponse response = httpClient.execute(request);
		
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
		}
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
		builder.addParam(PARAM_QUERY_NAME, "getUserAppPerms");
		builder.addParam(PARAM_USERNAME, username);
		//FIXME MLDS-170 MB extract to config
		builder.addParam(PARAM_APP_NAME, "MLDS");
		HttpPost request = builder.toRequest();
		
		CloseableHttpResponse response = httpClient.execute(request);
		
		AuthoritiesResponse authResponse = objectMapper.readValue(response.getEntity().getContent(), AuthoritiesResponse.class);
		
		return authResponse.getPerms();
	}
	
	
}