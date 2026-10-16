
package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RemoteAuthenticationConfiguration {

    @Bean
    public HttpAuthAdaptor httpAuthAdaptor(
        Environment environment,
        UserTokenStore userTokenStore,
        RestTemplateBuilder restTemplateBuilder
    ) {

        Binder binder = Binder.get(environment);

        String url = binder
            .bind("webauth.url", String.class)
            .orElse(null);

        String authenticatedCookieName = binder
            .bind("ims.cookie", String.class)
            .orElseThrow(() ->
                new IllegalStateException(
                    "Missing required configuration property: ims.cookie"
                )
            );

        return new HttpAuthAdaptor(
            url,
            userTokenStore,
            restTemplateBuilder,
            authenticatedCookieName
        );
    }
}

