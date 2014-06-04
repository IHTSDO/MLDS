package ca.intelliware.ihtsdo.mlds.registration;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
	List<Application> findByUsername(String username);
}
