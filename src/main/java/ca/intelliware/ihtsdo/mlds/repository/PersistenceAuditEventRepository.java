package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;



/**
 * Spring Data JPA repository for the PersistentAuditEvent entity.
 */
 public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEvent, String> {

    List<PersistentAuditEvent> findByPrincipal(String principal);

    List<PersistentAuditEvent> findByPrincipalAndAuditEventDateGreaterThan(String principal, Instant after);

    @Query("select p from PersistentAuditEvent p where p.auditEventDate >= ?1 and p.auditEventDate <= ?2")
    List<PersistentAuditEvent> findByDates(Instant fromDate, Instant toDate);

    List<PersistentAuditEvent> findByAuditEventType(String auditEventType);

	List<PersistentAuditEvent> findByAffiliateId(Long affiliateId);

	List<PersistentAuditEvent> findByApplicationId(Long applicationId);
}
