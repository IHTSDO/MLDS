package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
public class HttpAuthAdaptor implements HeaderConstants {
	
	private final Logger logger = LoggerFactory.getLogger(HttpAuthAdaptor.class);
	
	private static final String PARAM_LOGIN_USERNAME = "login";
    private static final String PARAM_LOGIN_PASSWORD = "password";

	private String queryUrl;
    private RestTemplate restTemplate;

	@Value("${ims.cookie}")
    private String authenticatedCookieName;

	public HttpAuthAdaptor(String url) {
        queryUrl = url;
        this.restTemplate = new RestTemplateBuilder()
            .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }

	String checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(PARAM_LOGIN_USERNAME, username);
        requestBody.put(PARAM_LOGIN_PASSWORD, password);

		try {
            ResponseEntity<Void> exchange = restTemplate.exchange(new RequestEntity<>(requestBody, HttpMethod.POST, URI.create(queryUrl + "api/authenticate")), Void.class);
            if (exchange.getStatusCodeValue() == 200) {
                return recoverAuthenticationCookie(exchange);
            }
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 404) {
                logger.info("IMS response is 404 = incorrect username/password.");
            } else {
                throw new IOException("Authentication service returned unexpected value: " + e.getRawStatusCode());
            }
        }
		return null;
    }

	private String recoverAuthenticationCookie(ResponseEntity<Void> response) {
        for (String header : response.getHeaders().get(SET_COOKIE)) {
			if (header.startsWith(authenticatedCookieName)) {
				return header;
			}
		}
		return null;
	}

	public CentralAuthUserInfo getUserAccountInfo(String username, String authenticationCookie) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(COOKIE, authenticationCookie);
        try {
            ResponseEntity<CentralAuthUserInfo> exchange = restTemplate.exchange(new RequestEntity<>(headers, HttpMethod.GET, URI.create(queryUrl + "api/account")), CentralAuthUserInfo.class);
            logger.info("Made remote call to get user details for {} HTTP {}", username, exchange.getStatusCodeValue());
            return exchange.getBody();
        } catch (HttpClientErrorException e) {
            throw new IOException("Unable to recover user account details for " + username + " received: " + e.getRawStatusCode());
        }
	}

}
