package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ResetToken entity.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
}
