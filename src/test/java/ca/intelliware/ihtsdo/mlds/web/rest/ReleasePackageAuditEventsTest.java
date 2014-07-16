package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

public class ReleasePackageAuditEventsTest {

	@Mock
	AuditEventService auditEventService;

	ReleasePackageAuditEvents releasePackageAuditEvents;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        releasePackageAuditEvents = new ReleasePackageAuditEvents();
        
        releasePackageAuditEvents.auditEventService = auditEventService;
	}
	
	@Test
	public void testLogReleasePackageCreated() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logCreationOf(releasePackage);
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("RELEASE_PACKAGE_CREATED"),Mockito.anyMap());
	}

	@Test
	public void testLogReleasePackageCreatedShouldIncludePackageDetails() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logCreationOf(releasePackage);
		
		HashMap<String, String> expected = new HashMap<String,String>();
		expected.put("releasePackage.name", "Test Name");
		expected.put("releasePackage.releasePackageId", "123");
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("RELEASE_PACKAGE_CREATED"), Mockito.eq(expected));		
	}


	@Test
	public void testLogReleasePackageDeleted() {
		ReleasePackage releasePackage = new ReleasePackage(123L);
		releasePackage.setName("Test Name");
		
		releasePackageAuditEvents.logDeletionOf(releasePackage);
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("RELEASE_PACKAGE_DELETED"),Mockito.anyMap());
	}
}
