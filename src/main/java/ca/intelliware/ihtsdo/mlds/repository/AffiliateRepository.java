package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
	List<Affiliate> findByCreator(String userName);

	@Query(value="SELECT l.* from affiliate l JOIN affiliate_details b on b.affiliate_details_id = l.affiliate_details_id "
			+ "where lower(b.first_name) like '%' || ?1 || '%' "
			+ "or lower(b.last_name) like  '%' || ?1 || '%' "
			+ "or lower(b.organization_name) like  '%' || ?1 || '%' "
			+ "or lower(b.street) like  '%' || ?1 || '%'", nativeQuery=true)
	List<Affiliate> findByTextQuery(String q);

	Affiliate findByImportKeyAndHomeMember(String importKey, Member member);
}
