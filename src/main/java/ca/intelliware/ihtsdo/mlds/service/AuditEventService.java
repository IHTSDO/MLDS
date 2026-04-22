package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.config.audit.AuditEventConverter;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AuditEventRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public static final String FILTER_BY_AUDIT_EVENT_TYPE = "auditEventType eq '(\\w+)'";
    public static final String FILTER_BY_AFFILIATE_ID = "affiliateId eq '(\\w+)'";
    public static final String FILTER_BY_APPLICATION_ID = "applicationId eq '(\\w+)'";
    public static final String FILTER_BY_AUDIT_EVENT_DATE_BETWEEN =
        "auditEventDate ge '(\\d{4}-\\d{2}-\\d{2})' and auditEventDate le '(\\d{4}-\\d{2}-\\d{2})'";

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

    public List<AuditEvent> findByFilter(String filter) throws ParseException {

        boolean isAdmin = currentSecurityContext.isAdmin();

        if (StringUtils.isBlank(filter)) {
            if (!isAdmin) {
                return Collections.emptyList();
            }
            return findAll();
        }

        Matcher affiliateIdMatcher = Pattern.compile(FILTER_BY_AFFILIATE_ID).matcher(filter);
        if (affiliateIdMatcher.matches()) {
            Long affiliateId = Long.parseLong(affiliateIdMatcher.group(1));
            return findByAffiliateId(affiliateId);
        }

        Matcher applicationIdMatcher = Pattern.compile(FILTER_BY_APPLICATION_ID).matcher(filter);
        if (applicationIdMatcher.matches()) {
            Long applicationId = Long.parseLong(applicationIdMatcher.group(1));
            return findByApplicationId(applicationId);
        }

        if (!isAdmin) {
            return Collections.emptyList();
        }

        Matcher auditEventTypeMatcher = Pattern.compile(FILTER_BY_AUDIT_EVENT_TYPE).matcher(filter);
        if (auditEventTypeMatcher.matches()) {
            return findByAuditEventType(auditEventTypeMatcher.group(1));
        }

        Matcher dateMatcher = Pattern.compile(FILTER_BY_AUDIT_EVENT_DATE_BETWEEN).matcher(filter);
        if (dateMatcher.matches()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Instant fromDate = dateFormat.parse(dateMatcher.group(1)).toInstant();
            Instant toDate = dateFormat.parse(dateMatcher.group(2)).toInstant();

            return findByDates(fromDate, toDate);
        }

        return Collections.emptyList();
    }


}
