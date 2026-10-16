package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.json.InternalPrivacyFilter;
import ca.intelliware.ihtsdo.mlds.domain.json.MLDSJacksonModule;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.MemberDTO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

@Configuration
public class JacksonConfigurer {

    private static final Logger logger =
        LoggerFactory.getLogger(JacksonConfigurer.class);

    @Resource
    private MemberRepository memberRepository;

    @Resource
    private CurrentSecurityContext currentSecurityContext;

    /**
     * Registers the MLDS privacy filters and custom modules with the Spring Boot Jackson 3 mapper.
     */
    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.constructorDetector(ConstructorDetector.EXPLICIT_ONLY);
            registerFilters(builder);
            builder.addModule(
                new MLDSJacksonModule(
                    memberRepository,
                    currentSecurityContext
                )
            );
        };
    }

    /**
     * Registers all MLDS privacy filters with a Jackson 3 JsonMapper builder.
     *
     * This method is also used by ObjectMapperTestBuilder.
     */
    public static void registerFilters(JsonMapper.Builder builder) {

        logger.info("Registering Jackson privacy filters");

        SimpleFilterProvider filterProvider =
            new SimpleFilterProvider();

        filterProvider.addFilter(
            "affiliatePrivacyFilter",
            new InternalPrivacyFilter(Affiliate.PRIVATE_FIELDS)
        );

        filterProvider.addFilter(
            "memberDtoPrivacyFilter",
            new InternalPrivacyFilter(MemberDTO.getPrivateFields())
        );

        builder.filterProvider(filterProvider);

        logger.info("Registered filter: affiliatePrivacyFilter");
        logger.info("Registered filter: memberDtoPrivacyFilter");

        if (logger.isInfoEnabled()) {
            logger.info(
                "MemberDTO private fields: {}",
                String.join(", ", MemberDTO.getPrivateFields())
            );
        }

    }
}
