package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, String> {
    @Query("select u from User u where u.activationKey = ?1")
    User getUserByActivationKey(String activationKey);


    @Query("select u from User u where u.activated = false and u.createdDate > ?1")
    List<User> findNotActivatedUsersByCreationDateBefore(LocalDate localDate);


	User getUserByEmailIgnoreCase(String emailAddress);

	User findByLoginIgnoreCase(String login);

	List<User> findByLoginIgnoreCaseIn(List<String> logins);

}
