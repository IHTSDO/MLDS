package ca.intelliware.ihtsdo.mlds.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;

@Service
public class AffiliateAuditEvents {
	static final String EVENT_AFFILIATE_CREATED = "AFFILIATE_CREATED";
	static final String EVENT_AFFILIATE_CREATED_BY_IMPORT = "AFFILIATE_CREATED_BY_IMPORT";
	static final String EVENT_AFFILIATE_UPDATED = "AFFILIATE_UPDATED";
	static final String EVENT_AFFILIATE_UPDATED_BY_IMPORT = "AFFILIATE_UPDATED_BY_IMPORT";
	static final String EVENT_AFFILIATEDETAILS_UPDATED = "AFFILIATEDETAILS_UPDATED";
	static final String EVENT_AFFILIATE_LOGIN_CREATED = "AFFILIATE_LOGIN_CREATED";
	static final String EVENT_AFFILIATE_STANDING_STATE_CHANGED = "AFFILIATE_STANDING_STATE_CHANGED";
	
	@Resource
	AuditEventService auditEventService;

	
	public void logCreationOf(Affiliate affiliate) {
		logEvent(EVENT_AFFILIATE_CREATED, affiliate);
	}

	public void logCreationByImport(Affiliate affiliate) {
		logEvent(EVENT_AFFILIATE_CREATED_BY_IMPORT, affiliate);
	}

	public void logUpdateByImport(Affiliate affiliate) {
		logEvent(EVENT_AFFILIATE_UPDATED_BY_IMPORT, affiliate);
	}

	public void logCreationOfAffiliateLogin(Affiliate affiliate) {
		logEvent(EVENT_AFFILIATE_LOGIN_CREATED, affiliate);
	}

	public void logStandingStateChange(Affiliate affiliate) {
		Map<String, String> auditData = createAffiliateData(affiliate);
		auditData.put("affiliate.standingState", ""+affiliate.getStandingState());
		logEvent(EVENT_AFFILIATE_STANDING_STATE_CHANGED, affiliate, auditData);
	}

	private void logEvent(String eventType, Affiliate affiliate) {
		Map<String, String> auditData = createAffiliateData(affiliate);
		logEvent(eventType, affiliate, auditData);
	}

	private void logEvent(String eventType, Affiliate affiliate, Map<String, String> auditData) {
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setAffiliateId(affiliate.getAffiliateId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createAffiliateData(Affiliate affiliate) {
		return AuditDataBuilder.start()
			.addAffiliateCreator(affiliate)
			.addAffiliateId(affiliate)
			.addAffiliateHomeMember(affiliate)
			.toAuditData();
	}
	
	public void logUpdateOfAffiliate(Affiliate affiliate) {
		logAffiliateUpdate(EVENT_AFFILIATE_UPDATED, affiliate);
	}

	private void logAffiliateUpdate(String eventType, Affiliate affiliate) {
		Map<String,String> auditData = createAffiliateData(affiliate);
    	auditData.put("affiliate.notesInternal", affiliate.getNotesInternal());
		logEvent(eventType, affiliate, auditData);
	}

	public void logUpdateOfAffiliateDetails(Affiliate affiliate) {
		logDetailsUpdate(EVENT_AFFILIATEDETAILS_UPDATED, affiliate);
	}

	private void logDetailsUpdate(String eventType, Affiliate affiliate) {
		Map<String, String> auditData = createAffiliateDetailsData(affiliate);
		logEvent(eventType, affiliate, auditData);
	}

	private Map<String, String> createAffiliateDetailsData(Affiliate affiliate) {
		Map<String,String> auditData = createAffiliateData(affiliate);
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
