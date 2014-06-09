package ca.intelliware.ihtsdo.mlds.registration;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainBlacklistRespository extends PagingAndSortingRepository<DomainBlacklist, Long> {
	
	List<DomainBlacklist> findByDomainname(String domainName);

}
