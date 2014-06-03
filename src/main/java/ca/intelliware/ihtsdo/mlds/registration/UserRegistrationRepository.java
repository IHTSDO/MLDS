package ca.intelliware.ihtsdo.mlds.registration;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegistrationRepository extends PagingAndSortingRepository<UserRegistration, Long> {

	UserRegistration findByEmail(String email);

}
