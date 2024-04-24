package ca.intelliware.ihtsdo.mlds.repository;

import java.time.Instant;
import java.util.*;



import ca.intelliware.ihtsdo.mlds.config.audit.AuditEventConverter;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

/**
 * Wraps an implementation of Spring Boot's AuditEventRepository.
 */
@Repository
public class CustomAuditEventRepository {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final AuditEventConverter auditEventConverter;

    public CustomAuditEventRepository(
        PersistenceAuditEventRepository persistenceAuditEventRepository,
        AuditEventConverter auditEventConverter) {
        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new AuditEventRepository() {


            @Override
            public void add(AuditEvent event) {
                PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
                persistentAuditEvent.setPrincipal(event.getPrincipal());
                persistentAuditEvent.setAuditEventType(event.getType());
                persistentAuditEvent.setAuditEventDate(event.getTimestamp());
                persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));

                persistenceAuditEventRepository.save(persistentAuditEvent);
            }

            @Override
            public List<AuditEvent> find(String principal, Instant after, String type) {
                final Iterable<PersistentAuditEvent> persistentAuditEvents;
                if (principal == null && after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findAll();
                } else if (after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
                } else {
                    persistentAuditEvents =
                        persistenceAuditEventRepository.findByPrincipalAndAuditEventDateGreaterThan(principal, after);
                }

                return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
            }
        };
    }
}
