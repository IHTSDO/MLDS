package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.Country;

public interface CountryRepository extends JpaRepository<Country, String> {

	List<Country> findByCommonName(String commonName);
}
