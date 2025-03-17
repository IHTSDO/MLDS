package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findByUsernameIgnoreCase(String username);

	Iterable<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState);
	Iterable<Application> findByApprovalStateInAndMember(Collection<ApprovalState> approvalState, Member member);

	Page<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState, Pageable pageable);
	Page<Application> findByApprovalStateInAndMember(Collection<ApprovalState> approvalState, Member member, Pageable pageable);

	Page<Application> findByMember(Member member, Pageable pageable);

    @Query(value = "select * from application where member_id = :memberId",nativeQuery = true)
    List<Application> findMemberById(Long memberId);

    @Modifying
    @Query("UPDATE Application a SET a.lastProcessed = :timestamp WHERE a.affiliate.affiliateId IN :affiliateIds")
    void updateLastProcessed(@Param("affiliateIds") List<Long> affiliateIds, @Param("timestamp") Instant timestamp);

    @Query(value = "SELECT * FROM mlds.application WHERE (approval_state = 'REJECTED' OR approval_state = 'CHANGE_REQUESTED' OR approval_state = 'NOT_SUBMITTED') AND inactive_at IS NULL AND last_processed IS NULL",nativeQuery = true)
    List<Application> getAllApplication();



}
