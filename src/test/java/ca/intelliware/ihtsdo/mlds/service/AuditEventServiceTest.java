package ca.intelliware.ihtsdo.mlds.service;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class AuditEventServiceTest {

	@Mock PersistenceAuditEventRepository mockPersistenceAuditEventRepository;
	@Mock CurrentSecurityContext mockCurrentSecurityContext;
	
	private String eventType = "eventType";
	private AuditEventService auditEventService;
	private Map<String, String> data = Maps.newHashMap();

	@Before
	public void setUp() {
		auditEventService = new AuditEventService();
		auditEventService.persistenceAuditEventRepository = mockPersistenceAuditEventRepository;
		auditEventService.currentSecurityContext = mockCurrentSecurityContext;
	}

	@Test
	public void createAuditEventDelegatesToRepositorySave() {
		
		auditEventService.logAuditableEvent(eventType, data);
		
		Mockito.verify(mockPersistenceAuditEventRepository).save(Mockito.any(PersistentAuditEvent.class));
	}

	@Test
	public void createAuditEventSetsType() {
		eventType = "foo";
		
		PersistentAuditEvent event =  auditEventService.createAuditEvent(eventType,data);
		
		assertEquals(event.getAuditEventType(),"foo");
	}

	@Test
	public void createAuditEventSetsData() {
		data.put("key", "value");
		
		PersistentAuditEvent event =  auditEventService.createAuditEvent(eventType,data);
		
		assertEquals(event.getData().get("key"),"value");
	}

	@Test
	public void createAuditEventSetsUsername() {
		Mockito.stub(mockCurrentSecurityContext.getCurrentUserName()).toReturn("our_username");
		
		PersistentAuditEvent event =  auditEventService.createAuditEvent(eventType,data);
		
		assertEquals(event.getPrincipal(),"our_username");
	}

}
