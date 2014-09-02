package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@SuppressWarnings("unchecked")
public class ApplicationAuditEventsTest {

	@Mock
	AuditEventService auditEventService;

	ApplicationAuditEvents applicationAuditEvents;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        applicationAuditEvents = new ApplicationAuditEvents();
        
        applicationAuditEvents.auditEventService = auditEventService;
        
        Mockito.when(auditEventService.createAuditEvent(Mockito.anyString(), Mockito.anyMap())).thenReturn(new PersistentAuditEvent());
	}
	
	@Test
	public void logApprovalStateChange() {
		PrimaryApplication application = new PrimaryApplication();
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		Mockito.verify(auditEventService).createAuditEvent(Mockito.eq("APPLICATION_APPROVAL_STATE_CHANGED"),Mockito.anyMap());
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.any(PersistentAuditEvent.class));
	}

	@Test
	public void logApprovalStateChangeShouldIncludeApplicationDetails() {
		PrimaryApplication application = new PrimaryApplication(123L);
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		application.setAffiliateDetails(affiliateDetails);;
		affiliateDetails.setOrganizationName("Test Organization");
		application.setApprovalState(ApprovalState.APPROVED);
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		HashMap<String, String> expected = new HashMap<String,String>();
		expected.put("application.name", "Test Organization");
		expected.put("application.applicationId", "123");
		expected.put("application.approvalState", "APPROVED");
		
		Mockito.verify(auditEventService).createAuditEvent(Mockito.anyString(), Mockito.eq(expected));
	}
}
