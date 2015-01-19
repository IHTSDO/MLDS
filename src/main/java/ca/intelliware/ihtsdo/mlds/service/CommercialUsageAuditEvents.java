package ca.intelliware.ihtsdo.mlds.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;

import com.google.common.collect.Maps;

@Service
public class CommercialUsageAuditEvents {

	public static final String EVENT_USAGE_CREATED = "USAGE_CREATED";
	public static final String EVENT_USAGE_APPROVAL_STATE_CHANGED = "USAGE_APPROVAL_STATE_CHANGED";

	@Resource
	AuditEventService auditEventService;

	private Map<String, String> createAuditData(CommercialUsage usage) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("usage.type", ""+usage.getType());
    	auditData.put("usage.commercialUsageId", ""+usage.getCommercialUsageId());
    	auditData.put("usage.period.start", ""+usage.getStartDate());
    	auditData.put("usage.period.end", ""+usage.getEndDate());
		return auditData;
	}
	
	public void logCreationOf(CommercialUsage usage) {
		Map<String, String> auditData = createAuditData(usage);
		logEvent(usage, EVENT_USAGE_CREATED, auditData);
	}
	
	public void logUsageReportStateChange(CommercialUsage usage) {
		Map<String, String> auditData = createAuditData(usage);
		auditData.put("usage.approvalState", ""+usage.getState());
		logEvent(usage, EVENT_USAGE_APPROVAL_STATE_CHANGED, auditData);
	}

	private void logEvent(CommercialUsage usage, String eventType, Map<String, String> auditData) {
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
    	auditEvent.setCommercialUsageId(usage.getCommercialUsageId());
    	if (usage.getAffiliate() != null) {
    		auditEvent.setAffiliateId(usage.getAffiliate().getAffiliateId());
    	}
    	auditEventService.logAuditableEvent(auditEvent);
	}
}
