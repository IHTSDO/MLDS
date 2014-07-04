package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.Licensee;

public interface LicenseeRepository extends JpaRepository<Licensee, Long> {
	List<Licensee> findByCreator(String userName);
}
