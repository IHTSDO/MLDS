package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.config.locale.AngularCookieLocaleResolver;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;


@Configuration
public class LocaleConfiguration implements EnvironmentAware, WebMvcConfigurer {


    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        final AngularCookieLocaleResolver cookieLocaleResolver = new AngularCookieLocaleResolver();
        cookieLocaleResolver.setCookieName("NG_TRANSLATE_LANG_KEY");
        /*MLDS-992 Missing Cookie*/
        cookieLocaleResolver.setCookieSecure(true);
        cookieLocaleResolver.setCookieHttpOnly(true);
        /*MLDS-992 Missing Cookie*/
        return cookieLocaleResolver;
    }

    @Bean
    public MessageSource messageSources() {
        final ReloadableResourceBundleMessageSource messageSources = new ReloadableResourceBundleMessageSource();
        //FIXME DGJ workaround - why?
        messageSources.setBasenames("classpath:/i18n/messages", "classpath:/mails/messages/messages");
        messageSources.setDefaultEncoding("UTF-8");
        Binder binder = Binder.get(env);
        messageSources.setCacheSeconds(binder.bind("spring.messagesource.cacheseconds", Integer.class).orElse(1));
        return messageSources;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");

        registry.addInterceptor(localeChangeInterceptor);
    }
}

