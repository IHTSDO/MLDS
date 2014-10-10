package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import ca.intelliware.ihtsdo.mlds.domain.json.ObjectMapperTestBuilder;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MockMvcJacksonTestSupport {
	public MemberRepository memberRepository;
	
    public static class WebMvcConfigurationSupportSpy extends WebMvcConfigurationSupport {
    	
    	public List<HttpMessageConverter<?>> getDefaultHttpMessageConverters() {
			List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
			super.addDefaultHttpMessageConverters(messageConverters);
			return messageConverters;
		}
    }

    public HttpMessageConverter<?>[] getConfiguredMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = 
				new WebMvcConfigurationSupportSpy().getDefaultHttpMessageConverters();
		
		findJacksonMapperAndReplaceObjectMapper(messageConverters);
		
		return messageConverters.toArray(new HttpMessageConverter[0]);
	}


	private void findJacksonMapperAndReplaceObjectMapper(List<HttpMessageConverter<?>> messageConverters) {
		MappingJackson2HttpMessageConverter jacksonMapper = 
				(MappingJackson2HttpMessageConverter) Iterables.find(
						messageConverters, 
						Predicates.instanceOf(MappingJackson2HttpMessageConverter.class));
		
		jacksonMapper.setObjectMapper(new ObjectMapperTestBuilder(memberRepository).buildObjectMapper());
	}
}
