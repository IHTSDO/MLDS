package ca.intelliware.ihtsdo.mlds.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainBlacklistRespository extends JpaRepository<DomainBlacklist, Long> {
//		extends PagingAndSortingRepository<DomainBlacklist, Long> {

	List<DomainBlacklist> findByDomainName(String domainName);

}
