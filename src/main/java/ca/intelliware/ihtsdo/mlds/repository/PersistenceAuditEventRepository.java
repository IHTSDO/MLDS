package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;

/**
 * Spring Data JPA repository for the PersistentAuditEvent entity.
 */
 public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, String> {

    List<PersistentAuditEvent> findByPrincipal(String principal);

    List<PersistentAuditEvent> findByPrincipalAndAuditEventDateGreaterThan(String principal, Instant after);
    
    @Query("select p from PersistentAuditEvent p where p.auditEventDate >= ?1 and p.auditEventDate <= ?2")
    List<PersistentAuditEvent> findByDates(Instant fromDate, Instant toDate);
    
    List<PersistentAuditEvent> findByAuditEventType(String auditEventType);

	List<PersistentAuditEvent> findByAffiliateId(String affiliateId);
}