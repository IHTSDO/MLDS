package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

import com.google.common.collect.Maps;

@Service
public class ApplicationAuditEvents {

	public static final String EVENT_APPLICATION_CREATED = "APPLICATION_CREATED";
	public static final String EVENT_APPLICATION_APPROVAL_STATE_CHANGED = "APPLICATION_APPROVAL_STATE_CHANGED";
	private static final String EVENT_APPLICATION_DELETED = "APPLICATION_DELETED";

	@Resource
	AuditEventService auditEventService;

	private Map<String, String> createAuditData(Application application) {
		Map<String,String> auditData = Maps.newHashMap();
		String name = (application.getAffiliateDetails() != null && application.getAffiliateDetails().getOrganizationName() != null) ? application.getAffiliateDetails().getOrganizationName() : application.getUsername(); 
		auditData.put("application.name", ""+name);
    	auditData.put("application.applicationId", ""+application.getApplicationId());
    	if (application.getMember() != null) {
    		auditData.put("application.member", ""+application.getMember().getKey());
    	}
    	auditData.put("application.type", ""+application.getApplicationType());
		return auditData;
	}
	
	public void logCreationOf(Application application) {
		Map<String, String> auditData = createAuditData(application);
		logEvent(application, EVENT_APPLICATION_CREATED, auditData);
	}
	
	public void logDeletionOf(Application application) {
		Map<String, String> auditData = createAuditData(application);
		logEvent(application, EVENT_APPLICATION_DELETED, auditData);
	}
	
	public void logApprovalStateChange(Application application) {
		Map<String, String> auditData = createAuditData(application);
		auditData.put("application.approvalState", ""+application.getApprovalState());
		logEvent(application, EVENT_APPLICATION_APPROVAL_STATE_CHANGED, auditData);
	}

	private void logEvent(Application application, String eventType, Map<String, String> auditData) {
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
    	auditEvent.setApplicationId(application.getApplicationId());
    	if (application.getAffiliate() != null) {
    		auditEvent.setAffiliateId(application.getAffiliate().getAffiliateId());
    	}
    	auditEventService.logAuditableEvent(auditEvent);
	}

}
