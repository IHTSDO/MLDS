package ca.intellware.ihtsdo.mlds.domain.json;

import java.util.Arrays;

import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.json.ApplicationCollection;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerialiationCycleTest {
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void cycleFromAffiliateToApplicationIsOK() throws Exception {
		Affiliate affiliate = new Affiliate();
		PrimaryApplication primaryApplication = new PrimaryApplication(55);
		ExtensionApplication extension = new ExtensionApplication(56);
		affiliate.setApplication(primaryApplication);
		affiliate.addApplication(primaryApplication);
		affiliate.addApplication(extension);

		String affiliateJson = objectMapper.writeValueAsString(affiliate);
		String applicationJson = objectMapper.writeValueAsString(primaryApplication);
		
		System.out.println("affiliate " + affiliateJson);
		System.out.println("primaryApplication " + applicationJson);
		System.out.println("list of apps " + objectMapper.writeValueAsString(Arrays.asList(extension, primaryApplication)));
		System.out.println("list of apps " + objectMapper.writeValueAsString(new ApplicationCollection(Arrays.asList(extension, primaryApplication))));
	}
}
