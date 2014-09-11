package ca.intelliware.ihtsdo.mlds.domain.json;

import javax.persistence.EntityManager;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.MemberJsonDeserializer;
import ca.intelliware.ihtsdo.mlds.domain.MemberSerializer;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class MLDSJacksonModule extends SimpleModule {
	final EntityManager em;
	final CurrentSecurityContext currentSecurityContext;
	private static final long serialVersionUID = 1L;

	public MLDSJacksonModule(EntityManager em, CurrentSecurityContext securityConext) {
		super("MLDS Jackson");
		this.em = em;
		this.currentSecurityContext = securityConext;
		
		addSerializer(ReleaseFile.class, new ReleaseFileSerializer());
		
		addSerializer(Member.class, new MemberSerializer());
		
		addDeserializer(Member.class, new MemberJsonDeserializer(em));
	}
}