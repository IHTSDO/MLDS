package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.Country;

public interface CommercialUsageEntryRepository extends JpaRepository<CommercialUsageEntry, Long> {

	// List<CommercialUsageEntry> findByCountry(Country country);

	List<CommercialUsageEntry> findByCountryAndCommercialUsage(Country country, CommercialUsage commercialUsage);

}
