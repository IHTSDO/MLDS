package ca.intelliware.ihtsdo.mlds.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfiguration {

	@Bean
    MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigFactory().createMultipartConfig();
    }
}
