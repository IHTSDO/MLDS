package ca.intelliware.ihtsdo.mlds.domain.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		objectMapper = new ObjectMapperTestBuilder().buildObjectMapper();
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

	@Test
	public void userCanNotSeeNotesInteral() throws Exception {
		securityContextSetup.asAffiliateUser();
		affiliate.setNotesInternal("our note");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(affiliate));
		
		assertNull("notesInternal node removed",tree.get("notesInternal"));
		
	}
	
	/**
	 * My first implementation was via a serialization config, but that was one-time thing.
	 * Make sure that our filter is re-evaluated every call.
	 */
	@Test
	public void propertyInclusionIsNotFixedForever() throws Exception {
		affiliate.setNotesInternal("our note");
		
		securityContextSetup.asAffiliateUser();
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(affiliate));
		assertNull("notesInternal node removed",tree.get("notesInternal"));
		
		securityContextSetup.asAdmin();
		JsonNode adminTree = objectMapper.readTree(objectMapper.writeValueAsString(affiliate));
		assertNotNull("admin still sees notes, after user has not",adminTree.get("notesInternal"));
		
	}
	
}
