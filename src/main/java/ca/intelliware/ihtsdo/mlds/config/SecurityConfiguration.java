package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.security.*;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAuthenticationProvider;
import jakarta.servlet.SessionCookieConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Autowired
    private Environment env;

    @Autowired
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    @Autowired
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
    @Autowired
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Autowired
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RememberMeServices rememberMeServices;
    @Autowired
    private HttpAuthAuthenticationProvider httpAuthAuthenticationProvider;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> forceSecureCookies() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setScheme("https");
            connector.setSecure(true);
        });
    }

        @Bean
        public ServletContextInitializer servletContextInitializer() {
            return servletContext -> {
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(true);
                sessionCookieConfig.setSecure(true);
                sessionCookieConfig.setName("JSESSIONID");
                sessionCookieConfig.setPath("/");
            };
        }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("Configuring Global Security");
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        auth.authenticationProvider(httpAuthAuthenticationProvider);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                "/fonts/**",
                "/images/**",
                "/scripts/**",
                "/styles/**",
                "/views/**",
                "/swagger-ui/**"
            );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .exceptionHandling((exception) -> exception.authenticationEntryPoint(authenticationEntryPoint))
            .rememberMe(rememberMe -> rememberMe
                .rememberMeServices(rememberMeServices)
                .key(env.getProperty("jhipster.security.rememberme.key")))
            .formLogin(formLogin -> formLogin
//                                .loginPage("/login")
                    .loginProcessingUrl("/app/authentication")
                    .successHandler(ajaxAuthenticationSuccessHandler)
                    .failureHandler(ajaxAuthenticationFailureHandler)
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
            )
            .securityContext((securityContext) -> securityContext
                .requireExplicitSave(true)
            )
            .logout(
                logout -> logout
                    .logoutUrl("/app/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .permitAll()
            )
            .cors(httpSecurityCorsConfigurer -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("*"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            })
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(httpBasic -> httpBasic
                    .authenticationEntryPoint(authenticationEntryPoint))
//                .authorizeHttpRequests((authorize) ->
//                        authorize
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/api/**").permitAll()

                .requestMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/websocket/**").permitAll()

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
                    "/api-docs/**",
                    "/actuator/**"
                ).hasAuthority(AuthoritiesConstants.ADMIN)

                .requestMatchers("/protected/**").authenticated()

                // MLDS-988 (block sensitive files)
                .requestMatchers(
                    "/.htaccess/**",
                    "/bower_components/html5shiv/package.json",
                    "/bower_components/es5-shim/package.json",
                    "/bower_components/placeholders/package.json",
                    "/bower_components/ng-csv/package.json",
                    "/bower_components/jquery/package.json",
                    "/bower_components/intl-tel-input/package.json",
                    "/bower_components/angular-dynamic-locale/package.json",
                    "/bower_components/ngInfiniteScroll/package.json",
                    "/bower_components/ng-csv/package.json",
                    "/bower_components/modernizr/.travis.yml",
                    "/bower_components/ng-csv/.travis.yml",
                    "/bower_components/intl-tel-input/.travis.yml",
                    "/bower_components/angular-dynamic-locale/.travis.yml",
                    "/bower_components/ngInfiniteScroll/.travis.yml",
                    "/bower_components/jquery/composer.json"

                ).denyAll()
            );
        return http.build();
    }

}
