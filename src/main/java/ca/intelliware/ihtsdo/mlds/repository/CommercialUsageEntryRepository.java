package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;

public interface CommercialUsageEntryRepository extends JpaRepository<CommercialUsageEntry, Long> {

}
