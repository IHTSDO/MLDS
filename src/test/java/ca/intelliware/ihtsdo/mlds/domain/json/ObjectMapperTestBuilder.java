package ca.intelliware.ihtsdo.mlds.domain.json;

import javax.persistence.EntityManager;

import ca.intelliware.ihtsdo.mlds.config.JacksonConfigurer;
import ca.intelliware.ihtsdo.mlds.domain.json.MLDSJacksonModule;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class ObjectMapperTestBuilder {
	EntityManager em;
	
	public ObjectMapper buildObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		new JacksonConfigurer().registerFilters(objectMapper);
		objectMapper.registerModule(new JodaModule());
		objectMapper.registerModule(new MLDSJacksonModule(em, new CurrentSecurityContext()));
		return objectMapper;
	}
}