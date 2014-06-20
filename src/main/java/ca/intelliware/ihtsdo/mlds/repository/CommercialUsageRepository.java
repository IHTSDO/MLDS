package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;

public interface CommercialUsageRepository extends JpaRepository<CommercialUsage, Long> {

}
