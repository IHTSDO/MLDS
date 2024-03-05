package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {

	List<Country> findByCommonName(String commonName);

    @Query(value = "SELECT * FROM mlds.country WHERE iso_code_2 = :isoCode2", nativeQuery = true)
    Country findByIsoCode2(@Param("isoCode2") String isoCode2);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM mlds.country WHERE iso_code_2 = :isoCode2", nativeQuery = true)
    void deleteByIsoCode2(@Param("isoCode2") String isoCode2);
}
