package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Set;


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

    @Query(value="SELECT DISTINCT ae.principal FROM t_persistent_audit_event ae WHERE ae.event_type = 'RELEASE_FILE_DOWNLOADED' AND ae.event_date BETWEEN :start AND :end",nativeQuery = true)
    Set<String> findUniqueUsersByTypeAndEventDate(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT ae FROM PersistentAuditEvent ae WHERE ae.auditEventType = 'RELEASE_FILE_DOWNLOADED' AND ae.auditEventDate BETWEEN :start AND :end")
    List<PersistentAuditEvent> findTypeAndEventDate(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT ae FROM PersistentAuditEvent ae WHERE ae.auditEventType = 'RELEASE_FILE_DOWNLOADED' AND ae.auditEventDate BETWEEN :start AND :end AND ae.affiliateId IS NOT NULL")
    List<PersistentAuditEvent> findTypeAndEventDateWithAffiliateIdNotNull(@Param("start") Instant start, @Param("end") Instant end);
}
