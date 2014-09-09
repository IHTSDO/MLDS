package ca.intelliware.ihtsdo.mlds.config;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;

@Configuration
public class RemoteAuthenticationConfiguration implements EnvironmentAware {
    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "webauth.");
    }

	@Bean
    public HttpAuthAdaptor httpAuthAdaptor() {
    	HttpAuthAdaptor httpAuthAdaptor = new HttpAuthAdaptor();
    	httpAuthAdaptor.setQueryUrl(propertyResolver.getProperty("url"));
    	httpAuthAdaptor.setApplicationName(propertyResolver.getProperty("applicationName"));
		return httpAuthAdaptor;
    }

}
