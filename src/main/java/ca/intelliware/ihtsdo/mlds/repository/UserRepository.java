package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.User;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    @Query("SELECT u FROM User u WHERE LOWER(u.login) = LOWER(:login)")
    User findByLoginIgnoreCaseSafe(@Param("login") String login);

	List<User> findByLoginIgnoreCaseIn(List<String> logins);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserIdForUpdate(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(
        value = """
        UPDATE user
        SET unsubscribe_key = UUID()
        WHERE (unsubscribe_key IS NULL OR unsubscribe_key = '')
        AND login IN (:logins)
    """,
        nativeQuery = true
    )
    int updateUnsubscribeKeysForUsers(@Param("logins") List<String> logins);
}
