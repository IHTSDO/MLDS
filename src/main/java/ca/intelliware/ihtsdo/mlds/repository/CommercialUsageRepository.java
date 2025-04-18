package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

public interface CommercialUsageRepository extends JpaRepository<CommercialUsage, Long> {
    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.startDate = ?2 AND p.endDate = ?3 AND p.effectiveTo IS NULL")
    List<CommercialUsage> findActiveBySamePeriod(Affiliate affiliate, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.effectiveTo IS NULL ORDER BY p.startDate DESC")
    List<CommercialUsage> findActiveByMostRecentPeriod(Affiliate affiliate);

	Collection<CommercialUsage> findByStateIn(Collection<UsageReportState> states);

	Collection<CommercialUsage> findByStateInAndEffectiveToIsNull(Collection<UsageReportState> states);


    @Query("SELECT p FROM CommercialUsage p WHERE p.state != :state AND p.effectiveTo IS NULL")
    Page<CommercialUsage> findByNotStateAndEffectiveToIsNull(@Param("state") UsageReportState state, Pageable pageable);

    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/
    @Query(value="SELECT commercial_usage.affiliate_id, m2.`key` as \"Member Key\", commercial_usage_entry.country_iso_code_2 as \"Deplyment Country\", commercial_usage_count.country_iso_code_2 as \"Affiliate Country\", commercial_usage.start_date, commercial_usage.end_date, affiliate.standing_state, affiliate.created, affiliate_details.agreement_type, concat (affiliate_details.first_name, ' ', affiliate_details.last_name) AS applicant, concat (affiliate_details.type, '-', affiliate_details.subtype) AS type, affiliate_details.organization_name, affiliate_details.organization_type, current_usage, planned_usage, purpose, implementation_status, other_activities, commercial_usage_count.snomed_practices, commercial_usage_count.hospitals_staffing_practices, commercial_usage_count.databases_per_deployment, commercial_usage_count.deployed_data_analysis_systems, COUNT(commercial_usage_entry.commercial_usage_id) AS hospitals FROM affiliate, affiliate_details, `member` m2, commercial_usage_count, commercial_usage LEFT JOIN commercial_usage_entry ON commercial_usage.commercial_usage_id = commercial_usage_entry.commercial_usage_id WHERE commercial_usage.affiliate_id = affiliate.affiliate_id and affiliate.home_member_id = m2.member_id AND affiliate.affiliate_details_id = affiliate_details.affiliate_details_id AND commercial_usage.commercial_usage_id = commercial_usage_count.commercial_usage_id AND commercial_usage.state = 'SUBMITTED' AND affiliate_details.email NOT LIKE '%ihtsdo.org%' GROUP BY commercial_usage.affiliate_id, m2.`key`,commercial_usage_entry.country_iso_code_2, commercial_usage_count.country_iso_code_2, commercial_usage.start_date, commercial_usage.end_date, affiliate.standing_state, affiliate.created, affiliate_details.first_name, affiliate_details.last_name, affiliate_details.organization_name, affiliate_details.organization_type, current_usage, planned_usage, purpose, implementation_status, other_activities, commercial_usage_count.snomed_practices, commercial_usage_count.hospitals_staffing_practices, commercial_usage_count.databases_per_deployment, commercial_usage_count.deployed_data_analysis_systems, affiliate_details.agreement_type, affiliate.notes_internal, affiliate_details.type, affiliate_details.subtype ORDER BY organization_name, commercial_usage.affiliate_id", nativeQuery = true)
    Collection<Object[]> findUsageReport();
    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/

    @Query("SELECT cu FROM CommercialUsage cu " +
        "JOIN cu.affiliate a " +
        "JOIN a.affiliateDetails ad " +
        "WHERE (LOWER(ad.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "       OR LOWER(ad.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
        "  AND cu.state != :state " +
        "  AND cu.effectiveTo IS NULL")
    Page<CommercialUsage> searchUsageReports(@Param("searchText") String searchText, @Param("state") UsageReportState state,  Pageable pageable);

}
