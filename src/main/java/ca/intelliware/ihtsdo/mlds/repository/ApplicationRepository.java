package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
//        extends PagingAndSortingRepository<Application, Long> {
	List<Application> findByUsernameIgnoreCase(String username);

	Iterable<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState);
	Iterable<Application> findByApprovalStateInAndMember(Collection<ApprovalState> approvalState, Member member);

	Page<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState, Pageable pageable);
	Page<Application> findByApprovalStateInAndMember(Collection<ApprovalState> approvalState, Member member, Pageable pageable);

	Page<Application> findByMember(Member member, Pageable pageable);

//	@Query(value = "SELECT * FROM mlds.application where application_id = :id",nativeQuery = true)
//	Application findOneById(@Param("applicationId") Long applicationId);
//
//	@Modifying
//	@Transactional
//	@Query(value = "DELETE FROM mlds.application WHERE application_id = :applicationId", nativeQuery = true)
//	void deleteByApplicationId(@Param("applicationId") Long applicationId);
}
