package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.config.audit.AuditEventConverter;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AuditEventRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;


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

    @Autowired
    PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Autowired
    AuditEventConverter auditEventConverter;

	@Autowired
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

	public List<AuditEvent> findByApplicationId(long applicationId) {
        final List<PersistentAuditEvent> persistentAuditEvents =
                persistenceAuditEventRepository.findByApplicationId(applicationId);

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

    public Instant[] getStartEndInstant(AuditEventRequestDTO request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Instant start = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Instant end = endDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return new Instant[]{start, end};
    }

    public List<PersistentAuditEvent> getAuditEvents(boolean excludeAdminAndStaff, Instant start, Instant end) {
        if (excludeAdminAndStaff) {
            return persistenceAuditEventRepository.findTypeAndEventDateWithAffiliateIdNotNull(start, end);
        } else {
            return persistenceAuditEventRepository.findTypeAndEventDate(start, end);
        }
    }

    public List<PersistentAuditEvent> filterDownloadEvents(List<PersistentAuditEvent> events) {
        return events.stream()
            .filter(event -> event.getData() != null &&
                "200".equals(event.getData().get("download.statusCode")))
            .toList();
    }


}
