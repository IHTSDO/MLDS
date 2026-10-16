package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.security.*;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAuthenticationProvider;
import jakarta.servlet.SessionCookieConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

    private final Logger logger =
        LoggerFactory.getLogger(SecurityConfiguration.class);


    private final UserDetailsService userDetailsService;

    private final HttpAuthAuthenticationProvider httpAuthAuthenticationProvider;
    public SecurityConfiguration(UserDetailsService userDetailsService, HttpAuthAuthenticationProvider httpAuthAuthenticationProvider) {
        this.userDetailsService = userDetailsService;
        this.httpAuthAuthenticationProvider = httpAuthAuthenticationProvider;
    }

    @Bean
    public static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    /**
     * Uses BCrypt for password hashing.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory>
    forceSecureCookies() {

        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setScheme("https");
            connector.setSecure(true);
        });
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {

        return servletContext -> {

            SessionCookieConfig sessionCookieConfig =
                servletContext.getSessionCookieConfig();

            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(true);
            sessionCookieConfig.setName("JSESSIONID");
            sessionCookieConfig.setPath("/");
        };
    }

    @Autowired
    public void configureGlobal(
        AuthenticationManagerBuilder auth
    ) {

        logger.debug("Configuring Global Security");

        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());

        auth.authenticationProvider(
            httpAuthAuthenticationProvider
        );
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        Environment env,
        AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
        AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler,
        AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler,
        Http401UnauthorizedEntryPoint authenticationEntryPoint,
        RememberMeServices rememberMeServices
    )  {

        http
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    authenticationEntryPoint
                )
            )

            .rememberMe(rememberMe ->
                rememberMe
                    .rememberMeServices(rememberMeServices)
                    .key(
                        env.getProperty(
                            "jhipster.security.rememberme.key"
                        )
                    )
            )

            .formLogin(formLogin ->
                formLogin
                    .loginProcessingUrl("/app/authentication")
                    .successHandler(
                        ajaxAuthenticationSuccessHandler
                    )
                    .failureHandler(
                        ajaxAuthenticationFailureHandler
                    )
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
            )

            .securityContext(securityContext ->
                securityContext.requireExplicitSave(true)
            )

            .logout(logout ->
                logout
                    .logoutUrl("/app/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(
                        ajaxLogoutSuccessHandler
                    )
                    .permitAll()
            )

            .cors(cors -> {
                CorsConfiguration configuration =
                    new CorsConfiguration();

                configuration.setAllowedOriginPatterns(
                    Arrays.asList("*")
                );
                configuration.setAllowedMethods(
                    Arrays.asList("*")
                );
                configuration.setAllowedHeaders(
                    Arrays.asList("*")
                );
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source =
                    new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                    "/**",
                    configuration
                );

                cors.configurationSource(source);
            })

            .csrf(AbstractHttpConfigurer::disable)

            .httpBasic(httpBasic ->
                httpBasic.authenticationEntryPoint(
                    authenticationEntryPoint
                )
            )

            .authorizeHttpRequests(auth -> auth

                /*
                 * SWAGGER / OPENAPI
                 */
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/v3/api-docs/swagger-config",
                    "/api-docs/**"
                ).hasAuthority(
                    AuthoritiesConstants.ADMIN
                )

                .requestMatchers(
                    "/webjars/**"
                ).permitAll()

                /*
                 * Swagger authentication endpoints.
                 */
                .requestMatchers(
                    "/swagger-login",
                    "/swagger-authenticated",
                    "/swagger-custom.js"
                ).permitAll()

                /*
                 * Existing MLDS authentication endpoint.
                 */
                .requestMatchers(
                    "/app/authentication"
                ).permitAll()

                /*
                 * Error endpoint.
                 */
                .requestMatchers(
                    "/error"
                ).permitAll()

                /*
                 * ADMIN API
                 */
                .requestMatchers(
                    "/api/logs/**"
                ).hasAuthority(
                    AuthoritiesConstants.ADMIN
                )

                /*
                 * MLDS API
                 * Retained from the original configuration.
                 */
                .requestMatchers(
                    "/api/**"
                ).permitAll()

                /*
                 * WEBSOCKET
                 */
                .requestMatchers(
                    "/websocket/tracker"
                ).hasAuthority(
                    AuthoritiesConstants.ADMIN
                )

                .requestMatchers(
                    "/websocket/**"
                ).permitAll()

                /*
                 * ACTUATOR / MONITORING
                 */
                .requestMatchers(
                    "/metrics/**",
                    "/health/**",
                    "/trace/**",
                    "/dump/**",
                    "/shutdown/**",
                    "/beans/**",
                    "/info/**",
                    "/autoconfig/**",
                    "/env/**",
                    "/actuator/**"
                ).hasAuthority(
                    AuthoritiesConstants.ADMIN
                )

                /*
                 * EVERYTHING ELSE
                 */
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

