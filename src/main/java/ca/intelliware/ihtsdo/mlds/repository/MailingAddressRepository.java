package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;

public interface MailingAddressRepository extends JpaRepository<AffiliateDetails, Long> {
}