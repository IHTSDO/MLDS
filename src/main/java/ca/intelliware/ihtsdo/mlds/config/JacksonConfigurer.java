package ca.intelliware.ihtsdo.mlds.config;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.intelliware.ihtsdo.mlds.domain.Member;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfigurer {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Bean
	public Module mldsModule(final EntityManager em) {
		SimpleModule mldsModule = new SimpleModule("MLDS Jackson");
		
		mldsModule.addSerializer(Member.class, new JsonSerializer<Member>() {
			@Override
			public void serialize(Member value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
				jgen.writeStartObject();
				jgen.writeObjectField("key", value.getKey());
				jgen.writeEndObject();
			}});
		mldsModule.addDeserializer(Member.class, new JsonDeserializer<Member>() {
			@Override
			public Member deserialize(JsonParser jp, DeserializationContext ctxt)
					throws IOException, JsonProcessingException {
				JsonNode node = jp.getCodec().readTree(jp);
				String key = node.get("key").textValue();
				Member result = (Member) em
						.createQuery("from Member where key = :key")
						.setParameter("key", key)
						.getSingleResult();
				return result;
			}
		});
		
		return mldsModule;
	}
	
	@PostConstruct
	public void init() {
		Collection<ObjectMapper> mappers = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(this.beanFactory, ObjectMapper.class)
				.values();
		for (ObjectMapper mapper : mappers) {
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		}
	}

}
