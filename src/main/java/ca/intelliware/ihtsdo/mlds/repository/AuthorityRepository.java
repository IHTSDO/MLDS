package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
