package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
	static final long AFFILIATE_ID_OPTIONAL_VALUE = -1L;

	List<Affiliate> findByCreatorIgnoreCase(String userName);

	@Query(value="SELECT l.* from affiliate l JOIN affiliate_details b on b.affiliate_details_id = l.affiliate_details_id "
			+ "where lower(b.first_name) like '%' || ?1 || '%' "
			+ "or lower(b.last_name) like  '%' || ?1 || '%' "
			+ "or lower(b.organization_name) like  '%' || ?1 || '%' "
			+ "or lower(b.street) like  '%' || ?1 || '%'", nativeQuery=true)
	List<Affiliate> findByTextQuery(String q);

	Affiliate findByImportKeyAndHomeMember(String importKey, Member member);

	@Query(value="SELECT a from Affiliate a where "
			+ "a.homeMember = :homeMember "
			+ "and (LOWER(a.application.affiliateDetails.lastName) like :q "
				+ "OR LOWER(a.application.affiliateDetails.firstName) like :q "
				+ "OR LOWER(a.application.affiliateDetails.organizationName) like :q "
				+ "OR LOWER(a.application.affiliateDetails.address.street) like :q)")
	Page<Affiliate> findByHomeMemberAndTextQuery(@Param("homeMember") Member homeMember, @Param("q") String q, Pageable pageable);


	@Query(value = "SELECT a FROM Affiliate a LEFT JOIN a.applications b"
			+ " WHERE a.homeMember = :homeMember "
			+ " OR b.member = :homeMember ")
	Page<Affiliate> findByHomeMember(@Param("homeMember") Member homeMember, Pageable pageable);

	Iterable<Affiliate> findByStandingStateInAndCreatorNotNull(Collection<StandingState> standingState);

	Page<Affiliate> findByHomeMemberAndStandingState(Member homeMember, StandingState standingState, Pageable pageable);
	Page<Affiliate> findByHomeMemberAndStandingStateNot(Member homeMember, StandingState standingState, Pageable pageable);

	Page<Affiliate> findByStandingState(StandingState standingState, Pageable pageable);
	Page<Affiliate> findByStandingStateNot(StandingState standingState, Pageable pageable);

	@Query(value="SELECT DISTINCT a from Affiliate a INNER JOIN a.applications b "
			+ "WHERE (a.affiliateId = :affiliateIdOptional OR :affiliateIdOptional = ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository.AFFILIATE_ID_OPTIONAL_VALUE) "
			+ "AND a.standingState = ca.intelliware.ihtsdo.mlds.domain.StandingState.IN_GOOD_STANDING "
			+ "AND (LOWER(a.affiliateDetails.lastName) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.firstName) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.organizationName) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.address.street) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.email) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.alternateEmail) like '%' || :q || '%' "
			+ "  OR LOWER(a.affiliateDetails.thirdEmail) like '%' || :q || '%' "
			+ ")"
			+ "AND b.member = :member "
			+ "AND b.approvalState = ca.intelliware.ihtsdo.mlds.domain.ApprovalState.APPROVED "
			)
	Page<Affiliate> findForCheck(@Param("affiliateIdOptional") long affiliateIdOptional, @Param("member") Member member, @Param("q") String q, Pageable pageable);

	@Query(value="SELECT DISTINCT a from Affiliate a INNER JOIN a.applications b "
			+ "WHERE  a.creator IS NOT NULL "
			+ "AND a.standingState IN :standingStates "
			+ "AND b.member = :member "
			+ "AND b.approvalState = ca.intelliware.ihtsdo.mlds.domain.ApprovalState.APPROVED "
			)
	Iterable<Affiliate> findByUsersAndStandingStateInAndApprovedMembership(@Param("standingStates") List<StandingState> standingStates, @Param("member") Member member);

	@Query(value="SELECT a from Affiliate a  "
			+ "WHERE  a.creator IS NOT NULL "
			+ "AND a.standingState IN :standingStates "
			+ "AND a.application.approvalState = ca.intelliware.ihtsdo.mlds.domain.ApprovalState.APPROVED "
			)
	Iterable<Affiliate> findByUsersAndStandingStateInAndApprovedPrimaryApplication(@Param("standingStates") List<StandingState> standingStates);

	@Query(value="SELECT a from Affiliate a  "
			+ "WHERE  a.creator IS NOT NULL "
			+ "AND a.standingState IN :standingStates "
			+ "AND a.homeMember = :member "
			+ "AND a.application.member = :member "
			+ "AND a.application.approvalState = ca.intelliware.ihtsdo.mlds.domain.ApprovalState.APPROVED "
			)
	Iterable<Affiliate> findByUsersAndStandingStateInAndApprovedHomeMembership(@Param("standingStates") List<StandingState> standingStates,  @Param("member") Member member);

    @Query(value = "select * from affiliate where affiliate_details_id = :affiliateDetailsId",nativeQuery = true)
    Affiliate getAllAffiliateByAffiliateDetailsId(Long affiliateDetailsId);

    @Query(value = "SELECT * FROM mlds.affiliate where home_member_id=1 and standing_state='PENDING_INVOICE' and last_processed is null",nativeQuery = true)
    List<Affiliate> getIHTSDOPendingInvoices();


    @Query("SELECT a.id FROM Affiliate a WHERE a.id IN :affiliateIds AND a.deactivated = false")
    List<Long> findActiveAffiliateIds(@Param("affiliateIds") List<Long> affiliateIds);

    @Modifying
    @Query("UPDATE Affiliate a SET a.deactivated = true, a.reasonForDeactivation = :reason WHERE a.id = :affiliateId")
    int updateAffiliateDeactivationReason(@Param("affiliateId") Long affiliateId, @Param("reason") ReasonForDeactivation reason);

    @Modifying
    @Query("UPDATE Affiliate a SET a.lastProcessed = :timestamp WHERE a.id IN :affiliateIds")
    void updateLastProcessed(@Param("affiliateIds") List<Long> affiliateIds, @Param("timestamp") Instant timestamp);


}
