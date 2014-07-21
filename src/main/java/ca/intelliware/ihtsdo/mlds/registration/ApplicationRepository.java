package ca.intelliware.ihtsdo.mlds.registration;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;

@Repository
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
	List<Application> findByUsername(String username);
	
	Iterable<Application> findByApprovalStateIn(Collection<ApprovalState> approvalState);
}
