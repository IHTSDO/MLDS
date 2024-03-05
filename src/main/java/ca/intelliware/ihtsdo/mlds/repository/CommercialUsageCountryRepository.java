package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommercialUsageCountryRepository extends JpaRepository<CommercialUsageCountry, Long> {

    @Query(value = "SELECT * FROM mlds.commercial_usage_count WHERE commercial_usage_count_id = :commercialUsageCountId", nativeQuery = true)
    CommercialUsageCountry findByCommercialUsageCountId(@Param("commercialUsageCountId") Long commercialUsageCountId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM mlds.commercial_usage_count WHERE commercial_usage_count_id = :commercialUsageCountId", nativeQuery = true)
    void deleteByCommercialUsageCountId(@Param("commercialUsageCountId") Long commercialUsageCountId);

}
