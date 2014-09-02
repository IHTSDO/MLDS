package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.PasswordResetToken;

/**
 * Spring Data JPA repository for the ResetToken entity.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
}
