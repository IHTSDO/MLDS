package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.security.*;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@CrossOrigin(origins = "*")
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
            .requestMatchers(AntPathRequestMatcher.antMatcher("/fonts/**"))
            .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**"))
            .requestMatchers(AntPathRequestMatcher.antMatcher("/scripts/**"))
            .requestMatchers(AntPathRequestMatcher.antMatcher("/styles/**"))
            .requestMatchers(AntPathRequestMatcher.antMatcher("/views/**"))
            .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**"));
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
            .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests((authorize) ->
//                        authorize
            .authorizeRequests()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/logs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/websocket/tracker")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/websocket/**")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/metrics/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/health/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/trace/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/dump/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/shutdown/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/beans/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/info/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/autoconfig/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/env/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/trace/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            .requestMatchers(AntPathRequestMatcher.antMatcher("/protected/**")).authenticated()
            //MLDS-988
            .requestMatchers(AntPathRequestMatcher.antMatcher("/.htaccess/**")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/html5shiv/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/es5-shim/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/placeholders/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/ng-csv/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/jquery/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/intl-tel-input/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/angular-dynamic-locale/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/ngInfiniteScroll/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/ng-csv/package.json")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/modernizr/.travis.yml")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/ng-csv/.travis.yml")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/intl-tel-input/.travis.yml")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/angular-dynamic-locale/.travis.yml")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/ngInfiniteScroll/.travis.yml")).denyAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/bower_components/jquery/composer.json")).denyAll();
//                );
        return http.build();
    }

}
