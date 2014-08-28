package ca.intelliware.ihtsdo.mlds.config;

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
import ca.intelliware.ihtsdo.mlds.domain.MemberJsonDeserializer;
import ca.intelliware.ihtsdo.mlds.domain.MemberSerializer;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFileSerializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfigurer {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Bean
	public Module mldsModule(final EntityManager em) {
		SimpleModule mldsModule = new SimpleModule("MLDS Jackson");
		
		mldsModule.addSerializer(ReleaseFile.class, new ReleaseFileSerializer());
		
		mldsModule.addSerializer(Member.class, new MemberSerializer());
		
		mldsModule.addDeserializer(Member.class, new MemberJsonDeserializer(em));
		
		
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
