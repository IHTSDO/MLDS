package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
public class HttpAuthAdaptor implements HeaderConstants {
	static final String PARAM_QUERY_USERNAME = "username";
	static final String PARAM_LOGIN_USERNAME = "j_username";
	static final String PARAM_APP_NAME = "appName";
	static final String PARAM_PASSWORD = "j_password";
	static final String PARAM_REMEMBER_ME = "_spring_security_remember_me";
	static final String PARAM_SUBMIT = "submit";

	String queryUrl;
	HttpClient httpClient = HttpClients.createDefault();
	
	@Resource
	ObjectMapper objectMapper;
	
	@Value("${ims.cookie}")
	String authenticatedCookieName;

	public HttpAuthAdaptor() {
	}
	
	public HttpAuthAdaptor(String url) {
		this();
		queryUrl = url;
	}

	public HttpCookie checkUsernameAndPasswordValid(String username, String password, String csrfToken) throws IOException, IllegalStateException, ClientProtocolException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl + "j_security_check", csrfToken);
		builder.addParam(PARAM_LOGIN_USERNAME, username);
		builder.addParam(PARAM_PASSWORD, password);
		builder.addParam(PARAM_REMEMBER_ME, "true");
		builder.addParam(PARAM_SUBMIT, "login");
		HttpPost request = builder.toRequest();
		HttpCookie authenticatedToken = null;
		
		HttpResponse response = httpClient.execute(request);
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 302) {
				//IMS returns users to the home page with a valid cookie if they successfully authenticate
				//SecurityContextHolder.getContext().getAuthentication().
				authenticatedToken = recoverAuthenticationCookie(response);
			} else if (statusCode == 401) {
				authenticatedToken = null;
			} else {
				throw new IOException("Authentication service returned unexpected value: " + statusCode);
			}
			
			return authenticatedToken;
		} finally {
			request.releaseConnection();
		}
	}

	private HttpCookie recoverAuthenticationCookie(HttpResponse response) {
		for (Header header : response.getHeaders(SET_COOKIE)) {
			if (header.getValue().startsWith(authenticatedCookieName)) {
				return HttpCookie.parse(header.getValue()).get(0);
			}
		}
		return null;
	}

	public CentralAuthUserInfo getUserAccountInfo(String username, String csrfToken, HttpCookie authenticationCookie) throws IOException {
		if (csrfToken==null) {
			csrfToken = getCsrfToken();
		}
		GetRequestBuilder builder = new GetRequestBuilder(queryUrl + "api/account", csrfToken);
		//No username is passed, the service works it out from the login cookie
		HttpGet request = builder.toRequest(authenticationCookie);
		request.setHeader(ACCEPT, JSON_MIME_TYPE);
		HttpResponse response = httpClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new IOException("Unable to recover user account details for " + username + " received: " + statusCode);
		}
		
		HttpEntity entity = new BufferedHttpEntity(response.getEntity());
		try {
			return objectMapper.readValue(entity.getContent(), CentralAuthUserInfo.class);
		} catch (Exception e) {
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

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public boolean checkUserExists(String username, String csrfToken) throws IOException {
		String url = queryUrl + "api/pre-register-check?" + PARAM_QUERY_USERNAME + "=" + username;
		if (csrfToken==null) {
			csrfToken = getCsrfToken();
		}
		
		PostRequestBuilder builder = new PostRequestBuilder(url, csrfToken);
		HttpPost request = builder.toRequest();
		
		HttpResponse response = httpClient.execute(request);
		try {
			int statusCode = response.getStatusLine().getStatusCode(); 
			boolean result;
			if (statusCode == 200) {
				result = true;
			} else if (statusCode == 404) {
				result = false;
			} else {
				throw new IOException("Authentication service returned unexpected value while checking if user exists: " + statusCode);
			}
			
			return result;
		} finally {
			request.releaseConnection();
		}
	}
	
	
	public String getCsrfToken() throws IOException {
		HttpHead request = new HttpHead(queryUrl);
		HttpResponse response = httpClient.execute(request);
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				Header[] headers = response.getHeaders(SET_COOKIE);
				//CSRF-TOKEN
				for (Header header : headers) {
					if (header.getValue().startsWith(CSRF_COOKIE)) {
						String csrfToken = getCookieValue(header);
						return csrfToken;
					}
				}
				throw new IOException("Authentication service returned ok, but did not offer a CrossScripting token");
			} else {
				throw new IOException("Authentication service returned unexpected status while requesting CrossScripting Token value: " + statusCode);
			}
		} finally {
			request.releaseConnection();
		}
	}

	private String getCookieValue(Header header) {
		int idxFrom =  header.getValue().indexOf('=') + 1;
		int idxTo = header.getValue().indexOf(';');
		return header.getValue().substring(idxFrom, idxTo);
	}
}