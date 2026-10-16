
package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.config.UserTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP query marshaller for the IHTSDO shared web authentication service.
 */
public class HttpAuthAdaptor {

    private static final Logger logger =
        LoggerFactory.getLogger(HttpAuthAdaptor.class);

    private static final String PARAM_LOGIN_USERNAME = "login";
    private static final String PARAM_LOGIN_PASSWORD = "password";

    private final UserTokenStore cookieTokenStore;
    private final String queryUrl;
    private final RestTemplate restTemplate;
    private final String authenticatedCookieName;

    public HttpAuthAdaptor(
        @Value("${your.url.property}") String url,
        UserTokenStore cookieTokenStore,
        RestTemplateBuilder restTemplateBuilder,
        @Value("${ims.cookie}") String authenticatedCookieName
    ) {
        this.queryUrl = url;
        this.cookieTokenStore = cookieTokenStore;
        this.authenticatedCookieName = authenticatedCookieName;

        JsonMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

        this.restTemplate = restTemplateBuilder
            .additionalMessageConverters(
                new JacksonJsonHttpMessageConverter(jsonMapper)
            )
            .build();
    }

    String checkUsernameAndPasswordValid(
        String username,
        String password
    ) throws IOException, IllegalStateException {

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(PARAM_LOGIN_USERNAME, username);
        requestBody.put(PARAM_LOGIN_PASSWORD, password);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                new RequestEntity<>(
                    requestBody,
                    HttpMethod.POST,
                    URI.create(queryUrl + "api/authenticate")
                ),
                Void.class
            );

            if (response.getStatusCode().value() == 200) {
                return recoverAuthenticationCookie(response);
            }

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().value() == 404) {
                logger.info(
                    "IMS response is 404 = incorrect username/password."
                );
            } else {
                throw new IOException(
                    "Authentication service returned unexpected value: "
                        + exception.getStatusCode().value(),
                    exception
                );
            }
        }

        return null;
    }

    private String recoverAuthenticationCookie(
        ResponseEntity<Void> response
    ) {
        for (String header :
            response.getHeaders().getOrEmpty(HeaderConstants.SET_COOKIE)) {

            if (header.startsWith(authenticatedCookieName)) {
                return header;
            }
        }

        return null;
    }

    public CentralAuthUserInfo getUserAccountInfo(
        String username,
        String authenticationCookie
    ) throws IOException {

        String tokenToUse;

        if (authenticationCookie != null
            && !authenticationCookie.isBlank()) {

            tokenToUse = authenticationCookie;
            cookieTokenStore.store(username, authenticationCookie);

        } else {
            tokenToUse = cookieTokenStore.get(username);

            if (tokenToUse == null || tokenToUse.isBlank()) {
                throw new IOException(
                    "Missing authentication cookie for user "
                        + username
                        + " and no fallback token found."
                );
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HeaderConstants.COOKIE, tokenToUse);

        try {
            ResponseEntity<CentralAuthUserInfo> response =
                restTemplate.exchange(
                    new RequestEntity<>(
                        headers,
                        HttpMethod.GET,
                        URI.create(queryUrl + "api/account")
                    ),
                    CentralAuthUserInfo.class
                );

            logger.info(
                "Made remote call to get user details for '{}' HTTP {}",
                username,
                response.getStatusCode()
            );

            return response.getBody();

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED
                || exception.getStatusCode() == HttpStatus.FORBIDDEN) {

                cookieTokenStore.remove(username);

                logger.warn(
                    "Removed stored token for user '{}' due to HTTP {}",
                    username,
                    exception.getStatusCode()
                );
            }

            throw new IOException(
                "Unable to recover user account details for "
                    + username
                    + ". Received HTTP: "
                    + exception.getStatusCode(),
                exception
            );
        }
    }

    public String getAuthenticatedCookieName() {
        return authenticatedCookieName;
    }

    public CentralAuthUserInfo getUserAccountInfoByCookie(
        String cookieValue
    ) throws IOException {

        String tokenToUse =
            authenticatedCookieName + "=" + cookieValue;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HeaderConstants.COOKIE, tokenToUse);

        try {
            ResponseEntity<CentralAuthUserInfo> response =
                restTemplate.exchange(
                    new RequestEntity<>(
                        headers,
                        HttpMethod.GET,
                        URI.create(queryUrl + "api/account")
                    ),
                    CentralAuthUserInfo.class
                );

            CentralAuthUserInfo userInfo = response.getBody();

            if (userInfo != null && userInfo.getLogin() != null) {
                cookieTokenStore.store(
                    userInfo.getLogin(),
                    tokenToUse
                );
            }

            logger.info(
                "Made remote call to get user details by cookie HTTP {}",
                response.getStatusCode()
            );

            return userInfo;

        } catch (HttpClientErrorException exception) {
            throw new IOException(
                "Unable to recover user account details by cookie. "
                    + "Received HTTP: "
                    + exception.getStatusCode(),
                exception
            );
        }
    }
}
