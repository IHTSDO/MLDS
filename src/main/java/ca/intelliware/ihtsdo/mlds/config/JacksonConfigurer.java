package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.json.InternalPrivacyFilter;
import ca.intelliware.ihtsdo.mlds.domain.json.MLDSJacksonModule;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;


import java.util.Collection;
import java.util.Map;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
@DependsOn("passwordEncoder")
public class JacksonConfigurer {

	private final Logger logger = LoggerFactory.getLogger(JacksonConfigurer.class);

	@Autowired
	private ListableBeanFactory beanFactory;


//	@Autowired
//	private ObjectMapper objectMapper;

	@Bean
	public Module mldsModule(final MemberRepository memberRepository, CurrentSecurityContext securityContext) {
		logger.debug("Creating MLDS Jackson Module bean");
		SimpleModule mldsModule = new MLDSJacksonModule(memberRepository, securityContext);
		return mldsModule;
	}

	@PostConstruct
    @Order(Integer.MAX_VALUE)
	public void init() {
		logger.debug("Initialising Jackson Mappers using {}", beanFactory.getClass().getName());

		Collection<ObjectMapper> mappersValue = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(beanFactory, ObjectMapper.class,true, true)
				.values();
        logger.info("mapper values {}", mappersValue.stream().count());
        Map<String, ObjectMapper> mappers = beanFactory.getBeansOfType(ObjectMapper.class);
		int mappersConfigured = 0;
        logger.info("mapper values {}", mappers.values());
		for (ObjectMapper mapper : mappers.values()) {
			logger.debug("Configuring Jackson mapper: {} with type factory {}", mapper.toString(), mapper.getTypeFactory().getClass().getName());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			registerFilters(mapper);
			mappersConfigured++;
		}

		if (mappersConfigured == 0) {
			logger.error("*** NO MAPPERS FOUND TO CONFIGURE! ***");
		}
	}

	public void registerFilters(ObjectMapper mapper) {
		logger.debug("Registering jackson privacy filters");
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("affiliatePrivacyFilter", new InternalPrivacyFilter(Affiliate.PRIVATE_FIELDS) );
		filterProvider.addFilter("memberDtoPrivacyFilter", new InternalPrivacyFilter(MemberDTO.PRIVATE_FIELDS) );
		mapper.setFilters(filterProvider);
	}

}
