package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@SuppressWarnings("unchecked")
public class ReleasePackageAuditEventsTest {

	@Mock
	AuditEventService auditEventService;

	ReleasePackageAuditEvents releasePackageAuditEvents;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        releasePackageAuditEvents = new ReleasePackageAuditEvents();
        
        releasePackageAuditEvents.auditEventService = auditEventService;
        
        Mockito.when(auditEventService.createAuditEvent(Mockito.anyString(), Mockito.anyMap())).thenReturn(new PersistentAuditEvent());
	}
	
	@Test
	public void testLogReleasePackageCreated() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logCreationOf(releasePackage);
		
		Mockito.verify(auditEventService).createAuditEvent(Mockito.eq("RELEASE_PACKAGE_CREATED"),Mockito.anyMap());
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.any(PersistentAuditEvent.class));
	}

	@Test
	public void testLogReleasePackageCreatedShouldIncludePackageDetails() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logCreationOf(releasePackage);
		
		HashMap<String, String> expected = new HashMap<String,String>();
		expected.put("releasePackage.name", "Test Name");
		
		Mockito.verify(auditEventService).createAuditEvent(Mockito.eq("RELEASE_PACKAGE_CREATED"), Mockito.eq(expected));
	}


	@Test
	public void testLogReleasePackageDeleted() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logDeletionOf(releasePackage);
		
		Mockito.verify(auditEventService).createAuditEvent(Mockito.eq("RELEASE_PACKAGE_DELETED"),Mockito.anyMap());
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.any(PersistentAuditEvent.class));
	}
}
