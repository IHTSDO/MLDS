package ca.intelliware.ihtsdo.mlds.domain.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.MemberSerializer;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class MLDSJacksonModule extends SimpleModule {
	
	private final Logger logger = LoggerFactory.getLogger(MLDSJacksonModule.class);
	
	final MemberRepository memberRepository;
	final transient CurrentSecurityContext currentSecurityContext;
	private static final long serialVersionUID = 1L;

	public MLDSJacksonModule(MemberRepository memberRepository, CurrentSecurityContext securityConext) {
		super("MLDS Jackson");
		
		logger.debug("Initialising Jackson for MLDS, adding custom serializers");
		
		this.memberRepository = memberRepository;
		this.currentSecurityContext = securityConext;
		
		addSerializer(ReleaseFile.class, new ReleaseFileSerializer());
		
		addSerializer(Member.class, new MemberSerializer());
		
		addDeserializer(Member.class, new MemberJsonDeserializer(memberRepository));
	}
}