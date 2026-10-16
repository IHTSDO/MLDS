package ca.intelliware.ihtsdo.mlds.domain.json;

import ca.intelliware.ihtsdo.mlds.config.JacksonConfigurer;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.json.JsonMapper;

public class ObjectMapperTestBuilder {

    private final MemberRepository memberRepository;

    public ObjectMapperTestBuilder(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public JsonMapper buildObjectMapper() {

        JsonMapper.Builder builder = JsonMapper.builder();
        builder.constructorDetector(ConstructorDetector.EXPLICIT_ONLY);

        /*
         * Register the same privacy filters used by the
         * application's production ObjectMapper.
         */
        JacksonConfigurer.registerFilters(builder);

        /*
         * Register MLDS-specific Jackson functionality.
         */
        builder.addModule(
            new MLDSJacksonModule(
                memberRepository,
                new CurrentSecurityContext()
            )
        );

        return builder.build();
    }
}
