package ca.intelliware.ihtsdo.mlds.web;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

@Service
public class ApplicationAuditEvents {

	public static final String APPLICATION_APPROVAL_STATE_CHANGED = "APPLICATION_APPROVAL_STATE_CHANGED";

	@Resource
	AuditEventService auditEventService;

	private Map<String, String> createAuditData(Application application) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("application.name", ""+Objects.firstNonNull(application.getOrganizationName(), application.getName()));
    	auditData.put("application.applicationId", ""+application.getApplicationId());
		return auditData;
	}
	
	public void logApprovalStateChange(Application application) {
    	Map<String, String> auditData = createAuditData(application);
    	auditData.put("application.approvalState", ""+application.getApprovalState());
    	auditEventService.logAuditableEvent(APPLICATION_APPROVAL_STATE_CHANGED, auditData);
	}
}
