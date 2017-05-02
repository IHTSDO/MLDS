package ca.intelliware.ihtsdo.mlds.config;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.json.InternalPrivacyFilter;
import ca.intelliware.ihtsdo.mlds.domain.json.MLDSJacksonModule;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfigurer {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Bean
	public Module mldsModule(final MemberRepository memberRepository, CurrentSecurityContext securityContext) {
		SimpleModule mldsModule = new MLDSJacksonModule(memberRepository, securityContext);
		
		return mldsModule;
	}
	
	@PostConstruct
	public void init() {
		Collection<ObjectMapper> mappers = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(this.beanFactory, ObjectMapper.class)
				.values();
		for (ObjectMapper mapper : mappers) {
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			
			registerFilters(mapper);
		}
	}

	public void registerFilters(ObjectMapper mapper) {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("affiliatePrivacyFilter", new InternalPrivacyFilter(Affiliate.PRIVATE_FIELDS) );
		filterProvider.addFilter("memberDtoPrivacyFilter", new InternalPrivacyFilter(MemberDTO.PRIVATE_FIELDS) );
		
		mapper.setFilters(filterProvider);
	}

}
