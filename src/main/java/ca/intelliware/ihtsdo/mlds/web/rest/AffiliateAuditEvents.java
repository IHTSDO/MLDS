package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

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
		auditEvent.setAffiliate(affiliate);
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleasePackageData(Affiliate affiliate) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("affiliate.creator", affiliate.getCreator());
		return auditData;
	}
}
