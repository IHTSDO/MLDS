package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.ImportResult;

import com.google.common.collect.Maps;

@Service
public class AffiliateAuditEvents {

	@Resource
	AuditEventService auditEventService;

	
	public void logCreationOf(Affiliate affiliate) {
		logEvent("AFFILIATE_CREATED", affiliate);
	}

	private void logEvent(String eventType, Affiliate affiliate) {
		Map<String, String> auditData = createReleasePackageData(affiliate);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setAffiliateId(affiliate.getAffiliateId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleasePackageData(Affiliate affiliate) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("affiliate.creator", affiliate.getCreator());
		return auditData;
	}
	
	public void logImport(ImportResult importResult) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("import.success", Boolean.toString(importResult.isSuccess()));
		auditData.put("import.rows", Long.toString(importResult.getReadRows()));
		auditData.put("import.affiliates", Long.toString(importResult.getImportedRecords()));
		auditData.put("import.newAffiliates", Long.toString(importResult.getNewRecords()));
		auditData.put("import.updatedAffiliates", Long.toString(importResult.getUpdatedRecords()));
		auditData.put("import.source", importResult.getSourceMemberKey());
		auditData.put("import.errors", Integer.toString(importResult.getErrors().size()));
		auditEventService.logAuditableEvent("AFFILIATE_IMPORT", auditData);
	}

	public void logExport() {
		Map<String,String> auditData = Maps.newHashMap();
		auditEventService.logAuditableEvent("AFFILIATE_EXPORT", auditData);
	}
	
	public void logUpdateOf(Affiliate affiliate) {
		logDetailsUpdate("AFFILATEDETAILS_UPDATED", affiliate);
	}

	private void logDetailsUpdate(String eventType, Affiliate affiliate) {
		Map<String, String> auditData = createAffiliateDetailsData(affiliate);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setAffiliateId(affiliate.getAffiliateId());
		auditEventService.logAuditableEvent(auditEvent);
	}

	private Map<String, String> createAffiliateDetailsData(Affiliate affiliate) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("affiliate.affiliateDetails.firstName", affiliate.getAffiliateDetails().getFirstName());
    	auditData.put("affiliate.affiliateDetails.lastName", affiliate.getAffiliateDetails().getLastName());
    	auditData.put("affiliate.affiliateDetails.landlineNumber", affiliate.getAffiliateDetails().getLandlineNumber());
    	auditData.put("affiliate.affiliateDetails.landlineExtension", affiliate.getAffiliateDetails().getLandlineExtension());
    	auditData.put("affiliate.affiliateDetails.mobileNumber", affiliate.getAffiliateDetails().getMobileNumber());
    	auditData.put("affiliate.affiliateDetails.address.street", affiliate.getAffiliateDetails().getAddress().getStreet());
    	auditData.put("affiliate.affiliateDetails.address.city", affiliate.getAffiliateDetails().getAddress().getCity());
    	auditData.put("affiliate.affiliateDetails.address.post", affiliate.getAffiliateDetails().getAddress().getPost());
    	auditData.put("affiliate.affiliateDetails.billingAddress.street", affiliate.getAffiliateDetails().getBillingAddress().getStreet());
    	auditData.put("affiliate.affiliateDetails.billingAddress.city", affiliate.getAffiliateDetails().getBillingAddress().getCity());
    	auditData.put("affiliate.affiliateDetails.billingAddress.post", affiliate.getAffiliateDetails().getBillingAddress().getPost());
    	Country billingCountry = affiliate.getAffiliateDetails().getBillingAddress().getCountry();
    	if (billingCountry != null) {
    		auditData.put("affiliate.affiliateDetails.billingAddress.country", billingCountry.getIsoCode2());
    	}
    	auditData.put("affiliate.affiliateDetails.alternateEmail", affiliate.getAffiliateDetails().getAlternateEmail());
    	auditData.put("affiliate.affiliateDetails.thirdEmail", affiliate.getAffiliateDetails().getThirdEmail());
		return auditData;
	}

}
