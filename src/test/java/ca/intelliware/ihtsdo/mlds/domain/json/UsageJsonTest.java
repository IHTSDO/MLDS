package ca.intelliware.ihtsdo.mlds.domain.json;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UsageJsonTest {
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	private CommercialUsage usage;
	private ObjectMapper objectMapper;
	private CommercialUsageCountry country;

	@Before
	public void buildGraph() {
		usage = new CommercialUsage(55L,null);
		usage.addEntry(new CommercialUsageEntry(22,usage));
		country = new CommercialUsageCountry(23,usage);
		country.setSnomedPractices(5);
		usage.addCount(country);
	}
	
	@Before
	public void configureJackson() {
		objectMapper = new ObjectMapperTestBuilder(null).buildObjectMapper();
	}
	
	@Test
	public void roundTripWithChildrenIsOK() throws JsonParseException, JsonMappingException, IOException {
		
		String jsonOutput = objectMapper.writeValueAsString(usage);
		objectMapper.readValue(jsonOutput, CommercialUsage.class);
	}
}
