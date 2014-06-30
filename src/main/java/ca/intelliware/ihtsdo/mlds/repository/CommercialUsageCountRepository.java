package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCount;

public interface CommercialUsageCountRepository extends JpaRepository<CommercialUsageCount, Long> {

}
