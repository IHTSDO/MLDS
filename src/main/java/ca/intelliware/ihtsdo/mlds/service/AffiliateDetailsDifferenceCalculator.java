package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;

public class AffiliateDetailsDifferenceCalculator {
	Map<String, String> oldValues = Maps.newHashMap();
	Map<String, String> newValues = Maps.newHashMap();

	public void calculateDifferences(AffiliateDetails originalAffiliateDetails, AffiliateDetails newDetails) {
		fillAffiliateDetails(oldValues, originalAffiliateDetails);
		fillAffiliateDetails(newValues, newDetails);
	}

	public void addDifferencesTo(Map<String, String> auditData) {
		HashSet<String> totalKeys = Sets.newHashSet();
		totalKeys.addAll(oldValues.keySet());
		totalKeys.addAll(newValues.keySet());
		for (String key : totalKeys) {
			if (!Objects.equal(oldValues.get(key), newValues.get(key))) {
				auditData.put(key, newValues.get(key));
				auditData.put("original." + key, oldValues.get(key));
			}
		}
	}
	
	private void fillAffiliateDetails(Map<String, String> auditData, AffiliateDetails originalAffiliateDetails) {
		safePut(auditData, "affiliate.affiliateDetails.type", originalAffiliateDetails.getType());
		safePut(auditData, "affiliate.affiliateDetails.otherText", originalAffiliateDetails.getOtherText());
		
		safePut(auditData, "affiliate.affiliateDetails.subType", originalAffiliateDetails.getSubType());
		
		safePut(auditData, "affiliate.affiliateDetails.agreementType", originalAffiliateDetails.getAgreementType());
		
		safePut(auditData, "affiliate.affiliateDetails.organizationType", originalAffiliateDetails.getOrganizationType());
		safePut(auditData, "affiliate.affiliateDetails.organizationTypeOther", originalAffiliateDetails.getOrganizationTypeOther());
		
		safePut(auditData, "affiliate.affiliateDetails.organizationName", originalAffiliateDetails.getOrganizationName());
		
		safePut(auditData, "affiliate.affiliateDetails.firstName", originalAffiliateDetails.getFirstName());
    	safePut(auditData, "affiliate.affiliateDetails.lastName", originalAffiliateDetails.getLastName());
    	
    	safePut(auditData, "affiliate.affiliateDetails.email", originalAffiliateDetails.getEmail());
    	safePut(auditData, "affiliate.affiliateDetails.alternateEmail", originalAffiliateDetails.getAlternateEmail());
    	safePut(auditData, "affiliate.affiliateDetails.thirdEmail", originalAffiliateDetails.getThirdEmail());
    	
    	safePut(auditData, "affiliate.affiliateDetails.landlineNumber", originalAffiliateDetails.getLandlineNumber());
    	safePut(auditData, "affiliate.affiliateDetails.landlineExtension", originalAffiliateDetails.getLandlineExtension());
    	safePut(auditData, "affiliate.affiliateDetails.mobileNumber", originalAffiliateDetails.getMobileNumber());
    	
    	putAddressData(auditData, "affiliate.affiliateDetails.address.", originalAffiliateDetails.getAddress());
    	putAddressData(auditData, "affiliate.affiliateDetails.billingAddress.", originalAffiliateDetails.getBillingAddress());
	}

	private void safePut(Map<String, String> auditData, String key, Object object) {
		if (object != null) {
			auditData.put(key, object.toString());
		}
	}

	private void putAddressData(Map<String, String> auditData, String addressPropertyPrefix, MailingAddress address) {
		if (address == null) {
			return;
		}
		auditData.put(addressPropertyPrefix + "street", address.getStreet());
		auditData.put(addressPropertyPrefix + "city", address.getCity());
		auditData.put(addressPropertyPrefix + "post", address.getPost());
		if (address.getCountry() != null) {
			auditData.put(addressPropertyPrefix + "country", address.getCountry().getIsoCode2());
		}
	}
}