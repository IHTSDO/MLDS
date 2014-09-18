package ca.intelliware.ihtsdo.mlds.service;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;

public class AffiliateDetailsDifferenceCalculatorTest {
	AffiliateDetailsDifferenceCalculator affiliateDetailsDifferenceCalculator = new AffiliateDetailsDifferenceCalculator();
	AffiliateDetails originalDetails = new AffiliateDetails();
	AffiliateDetails newDetails = new AffiliateDetails();
	Map<String, String> auditData = Maps.newHashMap();

	@Test
	public void changeOnlyFirstNameProducesOldAndNewValues() {
		originalDetails.setFirstName("Fred");
		cloneOldToNew();
		newDetails.setFirstName("Freddy");
		
		calculate();
		
		assertEquals("Freddy", auditData.get("affiliate.affiliateDetails.firstName"));
		assertEquals("Fred", auditData.get("original.affiliate.affiliateDetails.firstName"));
		assertEquals("Only one change, with old and new keys", 2, auditData.size());
	}
	
	@Test
	public void noChangeYieldsEmpty() {
		originalDetails.setFirstName("Fred");
		originalDetails.setLastName("Jones");
		cloneOldToNew();
		newDetails.setFirstName("Fred");
		newDetails.setLastName("Jones");
		
		calculate();
		
		assertEquals("No change yields no audit data", 0, auditData.size());
	}

	private void calculate() {
		affiliateDetailsDifferenceCalculator.calculateDifferences(originalDetails, newDetails);
		affiliateDetailsDifferenceCalculator.addDifferencesTo(auditData);
	}

	private void cloneOldToNew() {
		newDetails = (AffiliateDetails) originalDetails.clone();
	}

}
