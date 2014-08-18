package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
	List<Affiliate> findByCreator(String userName);

	@Query(value="SELECT l.* from affiliate l JOIN application a on a.application_id = l.application_id JOIN affiliate_details b on b.affiliate_details_id = a.affiliate_details_id "
			+ "where lower(b.first_name) like '%' || ?1 || '%' "
			+ "or lower(b.last_name) like  '%' || ?1 || '%' "
			+ "or lower(b.organization_name) like  '%' || ?1 || '%' "
			+ "or lower(b.street) like  '%' || ?1 || '%'", nativeQuery=true)
	List<Affiliate> findByTextQuery(String q);

	Affiliate findByImportKeyAndHomeMember(String importKey, Member member);

	@Query(value="SELECT a from Affiliate a where a.homeMember = :homeMember and (LOWER(a.application.affiliateDetails.lastName) like :q OR LOWER(a.application.affiliateDetails.firstName) like :q OR LOWER(a.application.affiliateDetails.organizationName) like :q OR LOWER(a.application.affiliateDetails.address.street) like :q)")
	Page<Affiliate> findByHomeMemberAndTextQuery(@Param("homeMember") Member homeMember, @Param("q") String q, Pageable pageable);

	@Query(value="SELECT a from Affiliate a where (LOWER(a.application.affiliateDetails.lastName) like :q OR LOWER(a.application.affiliateDetails.firstName) like :q OR LOWER(a.application.affiliateDetails.organizationName) like :q OR LOWER(a.application.affiliateDetails.address.street) like :q)")
	Page<Affiliate> findByTextQuery(@Param("q") String q, Pageable pageable);

	Page<Affiliate> findByHomeMember(Member homeMember, Pageable pageable);
}
