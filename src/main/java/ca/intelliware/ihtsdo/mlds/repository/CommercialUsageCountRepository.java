package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;

public interface CommercialUsageCountRepository extends JpaRepository<CommercialUsageCountry, Long> {

}
