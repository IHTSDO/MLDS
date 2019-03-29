package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.HttpCookie;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
public class HttpAuthAdaptor implements HeaderConstants {
	
	private final Logger logger = LoggerFactory.getLogger(HttpAuthAdaptor.class);
	
	private static final String PARAM_LOGIN_USERNAME = "login";
    private static final String PARAM_LOGIN_PASSWORD = "password";

	private String queryUrl;
    HttpClient httpClient = HttpClients.createDefault();
	
	@Resource
    ObjectMapper objectMapper;
	
	@Value("${ims.cookie}")
    private String authenticatedCookieName;

	public HttpAuthAdaptor() {
	}
	
	HttpAuthAdaptor(String url) {
		this();
		queryUrl = url;
	}

	HttpCookie checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException {
		PostRequestBuilder builder = new PostRequestBuilder(queryUrl + "api/authenticate");
		builder.addParam(PARAM_LOGIN_USERNAME, username);
		builder.addParam(PARAM_LOGIN_PASSWORD, password);
		HttpPost request = builder.toRequest();
		HttpCookie authenticatedToken;
		
		HttpResponse response = httpClient.execute(request);
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 302) {
				//IMS returns users to the home page with a valid cookie if they successfully authenticate
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

	public CentralAuthUserInfo getUserAccountInfo(String username, HttpCookie authenticationCookie) throws IOException {
		GetRequestBuilder builder = new GetRequestBuilder(queryUrl + "api/account");
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
			logger.info("Made remote call to get user details for {} HTTP {}", username, statusCode);
			request.releaseConnection();
		}
	}
	
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

}
