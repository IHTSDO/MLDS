package ca.intelliware.ihtsdo.mlds.service;

import java.util.HashSet;
import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;

import org.apache.lucene.analysis.standard.UAX29URLEmailAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.IndexedEmbedded;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateSubType;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.AgreementType;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.OrganizationType;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
		auditData.put("affiliate.affiliateDetails.type", originalAffiliateDetails.getType().toString());
		auditData.put("affiliate.affiliateDetails.otherText", originalAffiliateDetails.getOtherText());
		
		auditData.put("affiliate.affiliateDetails.subType", originalAffiliateDetails.getSubType().toString());
		
		auditData.put("affiliate.affiliateDetails.agreementType", originalAffiliateDetails.getAgreementType().toString());
		
		auditData.put("affiliate.affiliateDetails.organizationType", originalAffiliateDetails.getOrganizationType().toString());
		auditData.put("affiliate.affiliateDetails.organizationTypeOther", originalAffiliateDetails.getOrganizationTypeOther());
		
		auditData.put("affiliate.affiliateDetails.organizationName", originalAffiliateDetails.getOrganizationName());
		
		auditData.put("affiliate.affiliateDetails.firstName", originalAffiliateDetails.getFirstName());
    	auditData.put("affiliate.affiliateDetails.lastName", originalAffiliateDetails.getLastName());
    	
    	auditData.put("affiliate.affiliateDetails.email", originalAffiliateDetails.getEmail());
    	auditData.put("affiliate.affiliateDetails.alternateEmail", originalAffiliateDetails.getAlternateEmail());
    	auditData.put("affiliate.affiliateDetails.thirdEmail", originalAffiliateDetails.getThirdEmail());
    	
    	auditData.put("affiliate.affiliateDetails.landlineNumber", originalAffiliateDetails.getLandlineNumber());
    	auditData.put("affiliate.affiliateDetails.landlineExtension", originalAffiliateDetails.getLandlineExtension());
    	auditData.put("affiliate.affiliateDetails.mobileNumber", originalAffiliateDetails.getMobileNumber());
    	
    	putAddressData(auditData, "affiliate.affiliateDetails.address.", originalAffiliateDetails.getAddress());
    	putAddressData(auditData, "affiliate.affiliateDetails.billingAddress.", originalAffiliateDetails.getBillingAddress());
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