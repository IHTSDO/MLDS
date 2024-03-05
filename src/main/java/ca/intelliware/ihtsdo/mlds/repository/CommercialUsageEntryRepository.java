package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommercialUsageEntryRepository extends JpaRepository<CommercialUsageEntry, Long> {

	// List<CommercialUsageEntry> findByCountry(Country country);

	List<CommercialUsageEntry> findByCountryAndCommercialUsage(Country country, CommercialUsage commercialUsage);

    @Query(value = "SELECT * FROM mlds.commercial_usage_entry WHERE commercial_usage_entry_id = :commercialUsageEntryId", nativeQuery = true)
    CommercialUsageEntry findByCommercialUsageEntryId(@Param("commercialUsageEntryId") Long commercialUsageEntryId);

}
