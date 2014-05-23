package ca.intelliware.ihtsdo.mlds.registration;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegistrationRepository extends PagingAndSortingRepository<UserRegistration, Long> {

	List<UserRegistration> findByEmail(String email);

}
