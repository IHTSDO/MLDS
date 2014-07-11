package ca.intelliware.ihtsdo.mlds.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuditEventServiceTest {

	@Mock PersistenceAuditEventRepository mockPersistenceAuditEventRepository;
	@Mock CurrentSecurityContext mockCurrentSecurityContext;
	
	private AuditEventService auditEventService;

	@Before
	public void setUp() {
		auditEventService = new AuditEventService();
		auditEventService.persistenceAuditEventRepository = mockPersistenceAuditEventRepository;
		auditEventService.currentSecurityContext = mockCurrentSecurityContext;
	}

	@Test
	public void createAuditEventDelegatesToRepositorySave() {
		
		auditEventService.logAuditableEvent();
		
		Mockito.verify(mockPersistenceAuditEventRepository).save(Mockito.any(PersistentAuditEvent.class));
	}

	@Test
	public void createAuditEventSetsUsername() {
		Mockito.stub(mockCurrentSecurityContext.getCurrentUserName()).toReturn("our_username");
		
		PersistentAuditEvent event =  auditEventService.createAuditEvent();
		
		assertEquals(event.getPrincipal(),"our_username");
	}

}
