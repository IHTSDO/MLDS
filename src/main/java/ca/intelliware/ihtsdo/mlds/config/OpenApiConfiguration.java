package ca.intelliware.ihtsdo.mlds.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpenApiConfiguration {

    private static final String SESSION_AUTH = "mldsSession";
    private static final String BODY_END_TAG = "</body>";

    @Bean
    public OpenAPI mldsOpenAPI() {

        Server defaultServer =
            new Server()
                .url("/")
                .description("Default Server URL");

        return new OpenAPI()
            .info(
                new Info()
                    .title("MLDS REST API")
                    .description(
                        "SNOMED International MLDS REST API\n\n"
                    )
                    .version("5.3.6")
            )
            .components(
                new Components()
                    .addSecuritySchemes(
                        SESSION_AUTH,
                        new SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .in(SecurityScheme.In.COOKIE)
                            .name("JSESSIONID")
                            .description("MLDS Session Cookie")
                    )
            )
            .addSecurityItem(
                new SecurityRequirement()
                    .addList(SESSION_AUTH)
            )
            .servers(
                java.util.List.of(defaultServer)
            );
    }

    /**
     * Injects /swagger-custom.js automatically into Swagger UI's
     * swagger-initializer.js / index.html.
     */
    @Bean
    @Primary
    public SwaggerIndexPageTransformer swaggerIndexPageTransformer(
        SwaggerUiConfigProperties swaggerUiConfig,
        SwaggerUiOAuthProperties swaggerUiOAuthProperties,
        SwaggerWelcomeCommon swaggerWelcomeCommon,
        ObjectMapperProvider objectMapperProvider) {

        return new SwaggerIndexPageTransformer(
            swaggerUiConfig,
            swaggerUiOAuthProperties,
            swaggerWelcomeCommon,
            objectMapperProvider) {

            @Override
            public Resource transform(
                HttpServletRequest request,
                Resource resource,
                ResourceTransformerChain transformerChain)
                throws IOException {

                Resource transformed =
                    super.transform(
                        request,
                        resource,
                        transformerChain
                    );

                String resourceUrl = resource.getURL().toString();

                if (resourceUrl.contains("index.html")) {
                    String content =
                        new String(
                            transformed.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                        );

                    if (!content.contains("/swagger-custom.js")) {
                        String scriptTag = """
    <script id="mlds-swagger-custom-script" src="/swagger-custom.js"></script>
    """;

                        if (content.contains(BODY_END_TAG)) {
                            content =
                                content.replace(
                                    BODY_END_TAG,
                                    scriptTag + BODY_END_TAG
                                );
                        } else {
                            content = content + scriptTag;
                        }
                    }

                    return new TransformedResource(
                        resource,
                        content.getBytes(StandardCharsets.UTF_8)
                    );
                }

                return transformed;
            }
        };
    }
}
