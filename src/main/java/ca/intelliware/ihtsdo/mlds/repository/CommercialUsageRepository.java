package ca.intelliware.ihtsdo.mlds.repository;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;

public interface CommercialUsageRepository extends JpaRepository<CommercialUsage, Long> {
    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.startDate = ?2 AND p.endDate = ?3 AND p.effectiveTo IS NULL")
    List<CommercialUsage> findActiveBySamePeriod(Affiliate affiliate, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.effectiveTo IS NULL ORDER BY p.startDate DESC")
    List<CommercialUsage> findActiveByMostRecentPeriod(Affiliate affiliate);
    
	Collection<CommercialUsage> findByStateIn(Collection<UsageReportState> states);

	Collection<CommercialUsage> findByStateInAndEffectiveToIsNull(Collection<UsageReportState> states);
    
	@Query("SELECT p FROM CommercialUsage p WHERE p.state != ?1 AND p.effectiveTo IS NULL ORDER BY p.startDate DESC")
	Collection<CommercialUsage> findByNotStateAndEffectiveToIsNull(UsageReportState state);

}
