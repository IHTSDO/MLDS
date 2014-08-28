package ca.intelliware.ihtsdo.mlds.domain;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.web.rest.AffiliateResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class AffiliateJsonTest {
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	private Affiliate affiliate;
	private CommercialUsage usage;
	private ObjectMapper objectMapper;

	@Before
	public void buildGraph() {
		affiliate = new Affiliate();
		affiliate.setAffiliateDetails(new AffiliateDetails());
		usage = new CommercialUsage(55L,affiliate);
		affiliate.addCommercialUsage(usage);
		usage.addEntry(new CommercialUsageEntry(22,usage));
		usage.addCount(new CommercialUsageCountry(23,usage));
	}
	
	@Before
	public void configureJackson() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
	}
	
	@Test
	public void roundTripWithChildrenIsOK() throws JsonParseException, JsonMappingException, IOException {
		
		String jsonOutput = objectMapper.writeValueAsString(affiliate);
		objectMapper.readValue(jsonOutput, Affiliate.class);
	}
	
	@Test
	public void adminCanSeeNotesInteral() throws Exception {
		securityContextSetup.asAdmin();
		affiliate.setNotesInternal("our note");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(affiliate));
		
		String serializedNotes = tree.get("notesInternal").asText();
		assertEquals("our note", serializedNotes);
		
	}

	//@Test
	public void userCanNotSeeNotesInteral() throws Exception {
		securityContextSetup.asAdmin();
		affiliate.setNotesInternal("our note");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(affiliate));
		
		assertNull("notesInternal node removed",tree.get("notesInternal"));
		
	}
	
}
