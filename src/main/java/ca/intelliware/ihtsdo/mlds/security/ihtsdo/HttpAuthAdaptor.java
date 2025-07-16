package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.config.UserTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
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
    private UserTokenStore cookieTokenStore;
	private String queryUrl;
    private RestTemplate restTemplate;


	@Value("${ims.cookie}")
    private String authenticatedCookieName;

    // Constructor injection – no need for @Autowired
    public HttpAuthAdaptor(@Value("${your.url.property}") String url,
                           UserTokenStore cookieTokenStore,
                           RestTemplateBuilder restTemplateBuilder) {
        this.queryUrl = url;
        this.cookieTokenStore = cookieTokenStore;
        this.restTemplate = restTemplateBuilder
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
        String tokenToUse;
        if (authenticationCookie != null && !authenticationCookie.isBlank()) {
            tokenToUse = authenticationCookie;
            cookieTokenStore.store(username, authenticationCookie);
        } else {
            tokenToUse = cookieTokenStore.get(username);
            if (tokenToUse == null || tokenToUse.isBlank()) {
                throw new IOException("Missing authentication cookie for user " + username + " and no fallback token found.");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", tokenToUse);

        try {
            ResponseEntity<CentralAuthUserInfo> exchange = restTemplate.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(queryUrl + "api/account")),
                CentralAuthUserInfo.class
            );

            logger.info("Made remote call to get user details for '{}' HTTP {}", username, exchange.getStatusCode());
            return exchange.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                cookieTokenStore.remove(username);
                logger.warn("Removed stored token for user '{}' due to HTTP {}", username,e.getStatusCode());
            }
            throw new IOException("Unable to recover user account details for " + username + ". Received HTTP: " + e.getStatusCode());
        }

    }

}
