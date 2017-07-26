package ca.intelliware.ihtsdo.mlds.config;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;

import ca.intelliware.ihtsdo.mlds.security.AjaxAuthenticationFailureHandler;
import ca.intelliware.ihtsdo.mlds.security.AjaxAuthenticationSuccessHandler;
import ca.intelliware.ihtsdo.mlds.security.AjaxLogoutSuccessHandler;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.Http401UnauthorizedEntryPoint;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAuthenticationProvider;

@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    
    @Inject
    private Environment env;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private UserDetailsService userDetailsService;
    
    @Inject
    private RememberMeServices rememberMeServices;
    
    @Inject
    private HttpAuthAuthenticationProvider httpAuthAuthenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	logger.debug("Configuring Global Security");
        auth
            .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        auth.authenticationProvider(httpAuthAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    	logger.debug("Configuring URL Security");
        web.ignoring()
            .antMatchers("/bower_components/**")
            .antMatchers("/fonts/**")
            .antMatchers("/images/**")
            .antMatchers("/scripts/**")
            .antMatchers("/styles/**")
            .antMatchers("/views/**")
            .antMatchers("/swagger-ui/**");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	logger.debug("Configuring HTTP Security");
        http
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
            .rememberMe()
                .rememberMeServices(rememberMeServices)
                .key(env.getProperty("jhipster.security.rememberme.key"))
                .and()
            .formLogin()
                .loginProcessingUrl("/app/authentication")
                .successHandler(ajaxAuthenticationSuccessHandler)
                .failureHandler(ajaxAuthenticationFailureHandler)
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .permitAll()
                .and()
            .httpBasic()
            	.and()
            .logout()
                .logoutUrl("/app/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
            .csrf()
                .disable()
            .headers()
                .frameOptions().disable()
                .and()
            .authorizeRequests()
                .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").permitAll()
                .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/websocket/**").permitAll()
                .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/protected/**").authenticated();
    }
}