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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                .requestMatchers("/fonts/**")
                .requestMatchers("/images/**")
                .requestMatchers("/scripts/**")
                .requestMatchers("/styles/**")
                .requestMatchers("/views/**")
                .requestMatchers("/swagger-ui/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling((exception)-> exception.authenticationEntryPoint(authenticationEntryPoint))
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
                ).logout(
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
                                .requestMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/websocket/**").permitAll()
                                .requestMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/protected/**").authenticated()
                                //MLDS-988
                                .requestMatchers("/.htaccess/**").denyAll()
                                .requestMatchers("/bower_components/html5shiv/package.json").denyAll()
                                .requestMatchers("/bower_components/es5-shim/package.json").denyAll()
                                .requestMatchers("/bower_components/placeholders/package.json").denyAll()
                                .requestMatchers("/bower_components/ng-csv/package.json").denyAll()
                                .requestMatchers("/bower_components/jquery/package.json").denyAll()
                                .requestMatchers("/bower_components/intl-tel-input/package.json").denyAll()
                                .requestMatchers("/bower_components/angular-dynamic-locale/package.json").denyAll()
                                .requestMatchers("/bower_components/ngInfiniteScroll/package.json").denyAll()
                                .requestMatchers("/bower_components/ng-csv/package.json").denyAll()
                                .requestMatchers("/bower_components/modernizr/.travis.yml").denyAll()
                                .requestMatchers("/bower_components/ng-csv/.travis.yml").denyAll()
                                .requestMatchers("/bower_components/intl-tel-input/.travis.yml").denyAll()
                                .requestMatchers("/bower_components/angular-dynamic-locale/.travis.yml").denyAll()
                                .requestMatchers("/bower_components/ngInfiniteScroll/.travis.yml").denyAll()
                                .requestMatchers("/bower_components/jquery/composer.json").denyAll();
//                );
        return http.build();
    }

}
