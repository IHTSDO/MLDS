package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.Member;

@Repository
public interface FileRepository extends PagingAndSortingRepository<Member, Long>{
	
	List<Member> findByKey(String key);
}
