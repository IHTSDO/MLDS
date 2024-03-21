package ca.intelliware.ihtsdo.mlds.config;

import java.util.Collection;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.json.InternalPrivacyFilter;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
@DependsOn("passwordEncoder")
public class JacksonConfigurer {

    private final Logger logger = LoggerFactory.getLogger(JacksonConfigurer.class);

    @Autowired
    private ListableBeanFactory beanFactory;

    @Autowired
    ObjectMapper objectMapper;


    @PostConstruct
    public void init() {
        logger.debug("Initialising Jackson Mappers using {}", beanFactory.getClass().getName());

        Collection<ObjectMapper> mappers = BeanFactoryUtils
            .beansOfTypeIncludingAncestors(beanFactory, ObjectMapper.class,true, true)
            .values();
        int mappersConfigured = 0;
        for (ObjectMapper mapper : mappers) {
            logger.debug("Configuring Jackson mapper: {} with type factory {}", mapper, mapper.getTypeFactory().getClass().getName());
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
        mapper.setFilterProvider(filterProvider);
    }

}
