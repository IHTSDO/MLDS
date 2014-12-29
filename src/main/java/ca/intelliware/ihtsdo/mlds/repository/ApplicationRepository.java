package ca.intelliware.ihtsdo.mlds.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;

@Repository
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
	List<Application> findByUsernameIgnoreCase(String username);
	
	Iterable<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState);

	Page<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState, Pageable pageable);
	Page<Application> findByApprovalStateInAndMember(Collection<ApprovalState> approvalState, Member member, Pageable pageable);
	
	Page<Application> findByMember(Member member, Pageable pageable);
}
