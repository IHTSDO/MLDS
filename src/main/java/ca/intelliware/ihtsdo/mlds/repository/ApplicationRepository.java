package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
