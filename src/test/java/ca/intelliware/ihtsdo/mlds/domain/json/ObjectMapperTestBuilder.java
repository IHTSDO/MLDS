package ca.intelliware.ihtsdo.mlds.domain.json;

import ca.intelliware.ihtsdo.mlds.config.JacksonConfigurer;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class ObjectMapperTestBuilder {
	MemberRepository memberRepository;
	
	public ObjectMapperTestBuilder(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	public ObjectMapper buildObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		new JacksonConfigurer().registerFilters(objectMapper);
		objectMapper.registerModule(new JodaModule());
		objectMapper.registerModule(new MLDSJacksonModule(memberRepository, new CurrentSecurityContext()));
		return objectMapper;
	}
}