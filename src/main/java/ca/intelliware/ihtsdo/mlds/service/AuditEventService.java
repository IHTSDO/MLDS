package ca.intelliware.ihtsdo.mlds.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.Instant;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.intelliware.ihtsdo.mlds.config.audit.AuditEventConverter;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;

/**
 * Service for managing audit events.
 * <p/>
 * <p>
 * This is the default implementation to support SpringBoot Actuator AuditEventRepository
 * </p>
 */
@Service
@Transactional
public class AuditEventService {

    @Inject
    PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Inject
    AuditEventConverter auditEventConverter;

	@Inject
	CurrentSecurityContext currentSecurityContext;

    public List<AuditEvent> findAll() {
        return auditEventConverter.convertToAuditEvent(persistenceAuditEventRepository.findAll());
    }

    public List<AuditEvent> findByDates(Instant fromDate, Instant toDate) {
        final List<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findByDates(fromDate, toDate);

        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    public List<AuditEvent> findByAuditEventType(String auditEventType) {
        final List<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findByAuditEventType(auditEventType);

        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

	public List<AuditEvent> findByAffiliateId(Long affiliateId) {
        final List<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findByAffiliateId(affiliateId);

        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
	}

    

	public void logAuditableEvent(String eventType, Map<String,String> auditData) {
		logAuditableEvent(createAuditEvent(eventType, auditData));
	}

	public void logAuditableEvent(PersistentAuditEvent auditEvent) {
		persistenceAuditEventRepository.save(auditEvent);
	}

	public PersistentAuditEvent createAuditEvent(String eventType, Map<String, String> auditData) {
		PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
		persistentAuditEvent.setPrincipal(currentSecurityContext.getCurrentUserName());
		persistentAuditEvent.setAuditEventType(eventType);
		persistentAuditEvent.setData(auditData);
		return persistentAuditEvent;
	}

}
