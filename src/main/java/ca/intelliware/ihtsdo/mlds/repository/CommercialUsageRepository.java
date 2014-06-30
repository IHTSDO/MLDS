package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.Licensee;

public interface CommercialUsageRepository extends JpaRepository<CommercialUsage, Long> {
    @Query("select p from CommercialUsage p where p.licensee = ?1 and p.startDate = ?2 and p.endDate = ?3")
    List<CommercialUsage> findBySamePeriod(Licensee licensee, LocalDate startDate, LocalDate endDate);

    @Query("select p from CommercialUsage p where p.licensee = ?1 order by p.startDate DESC")
    List<CommercialUsage> findByMostRecentPeriod(Licensee licensee);

}
