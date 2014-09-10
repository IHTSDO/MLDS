package ca.intelliware.ihtsdo.mlds.service;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

public class AffiliateAuditEventsTest {
	@Mock
	AuditEventService auditEventService;

	AffiliateAuditEvents affiliateAuditEvents;

	@SuppressWarnings("unchecked")
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        affiliateAuditEvents = new AffiliateAuditEvents();
        
        affiliateAuditEvents.auditEventService = auditEventService;
        
        Mockito.when(auditEventService.createAuditEvent(Mockito.anyString(), Mockito.anyMap())).thenReturn(new PersistentAuditEvent());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void logUpdateOfAffiliate() {
		Affiliate affiliate = new Affiliate(123L);
		
		affiliateAuditEvents.logUpdateOfAffiliate(affiliate);

		Mockito.verify(auditEventService).createAuditEvent(Mockito.eq("AFFILIATE_UPDATED"),Mockito.anyMap());
		
		ArgumentCaptor<PersistentAuditEvent> auditEvent = ArgumentCaptor.forClass(PersistentAuditEvent.class);
		Mockito.verify(auditEventService).logAuditableEvent(auditEvent.capture());
		
		Assert.assertThat(auditEvent.getValue().getAffiliateId(), Matchers.equalTo(123L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logStandingStateChangeShouldIndicateNewState() {
		Affiliate affiliate = new Affiliate(123L);
		affiliate.setStandingState(StandingState.REJECTED);
		
		affiliateAuditEvents.logStandingStateChange(affiliate);

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map<String,String>> auditEvent = ArgumentCaptor.forClass((Class)Map.class);
		Mockito.verify(auditEventService).createAuditEvent(
				Mockito.eq(AffiliateAuditEvents.EVENT_AFFILIATE_STANDING_STATE_CHANGED),
				auditEvent.capture());
		
		Assert.assertThat(auditEvent.getValue(), Matchers.hasEntry("affiliate.standingState", "REJECTED"));
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.any(PersistentAuditEvent.class));
	}

}
