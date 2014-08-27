package ca.intelliware.ihtsdo.mlds.domain;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class AffiliateJsonTest {

	@Test
	public void roundTripWithChildrenIsOK() throws JsonParseException, JsonMappingException, IOException {
		Affiliate affiliate = new Affiliate();
		affiliate.setAffiliateDetails(new AffiliateDetails());
		CommercialUsage usage = new CommercialUsage(55L,affiliate);
		affiliate.addCommercialUsage(usage);
		usage.addEntry(new CommercialUsageEntry(22,usage));
		usage.addCount(new CommercialUsageCountry(23,usage));
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		
		String jsonOutput = objectMapper.writeValueAsString(affiliate);
		System.out.println(jsonOutput);
		objectMapper.readValue(jsonOutput, Affiliate.class);
	}

}
