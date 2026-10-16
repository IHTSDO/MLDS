package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import ca.intelliware.ihtsdo.mlds.domain.json.ObjectMapperTestBuilder;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import tools.jackson.databind.json.JsonMapper;

public class MockMvcJacksonTestSupport {

    public MemberRepository memberRepository;

    public static class WebMvcConfigurationSupportSpy
        extends WebMvcConfigurationSupport {

        public List<HttpMessageConverter<?>> getDefaultHttpMessageConverters() {
            return super.getMessageConverters();
        }
    }

    public HttpMessageConverter<?>[] getConfiguredMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters =
            new WebMvcConfigurationSupportSpy()
                .getDefaultHttpMessageConverters();

        replaceJacksonConverter(messageConverters);

        return messageConverters.toArray(new HttpMessageConverter<?>[0]);
    }

    private void replaceJacksonConverter(
        List<HttpMessageConverter<?>> messageConverters) {

        int jacksonIndex = -1;

        for (int i = 0; i < messageConverters.size(); i++) {
            if (messageConverters.get(i)
                instanceof JacksonJsonHttpMessageConverter) {
                jacksonIndex = i;
                break;
            }
        }

        if (jacksonIndex == -1) {
            throw new IllegalStateException(
                "JacksonJsonHttpMessageConverter not found");
        }

        JsonMapper mapper = (JsonMapper)
            new ObjectMapperTestBuilder(memberRepository)
                .buildObjectMapper();

        messageConverters.set(
            jacksonIndex,
            new JacksonJsonHttpMessageConverter(mapper));
    }
}
