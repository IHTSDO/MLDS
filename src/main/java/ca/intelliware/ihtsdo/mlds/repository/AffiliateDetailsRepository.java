package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffiliateDetailsRepository extends JpaRepository<AffiliateDetails, Long> {
}