package ca.intelliware.ihtsdo.mlds.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import org.springframework.data.repository.query.Param;

public interface CommercialUsageRepository extends JpaRepository<CommercialUsage, Long> {
    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.startDate = ?2 AND p.endDate = ?3 AND p.effectiveTo IS NULL")
    List<CommercialUsage> findActiveBySamePeriod(Affiliate affiliate, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM CommercialUsage p WHERE p.affiliate = ?1 AND p.effectiveTo IS NULL ORDER BY p.startDate DESC")
    List<CommercialUsage> findActiveByMostRecentPeriod(Affiliate affiliate);

	Collection<CommercialUsage> findByStateIn(Collection<UsageReportState> states);

	Collection<CommercialUsage> findByStateInAndEffectiveToIsNull(Collection<UsageReportState> states);

	@Query("SELECT p FROM CommercialUsage p WHERE p.state != ?1 AND p.effectiveTo IS NULL ORDER BY p.startDate DESC")
	Collection<CommercialUsage> findByNotStateAndEffectiveToIsNull(UsageReportState state);

    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/

    @Query(value="\n" + "SELECT commercial_usage.affiliate_id, \n" + "m2.\"key\" as \"Member Key\",\n" + "commercial_usage_entry.country_iso_code_2 as \"Deplyment Country\",\n" + "commercial_usage_count.country_iso_code_2 as \"Affiliate Country\",\n" + "commercial_usage.start_date, commercial_usage.end_date, affiliate.standing_state, \n" + "affiliate.created, affiliate_details.agreement_type, \n" + "concat (affiliate_details.first_name, ' ', affiliate_details.last_name) AS applicant, \n" + "concat (affiliate_details.type, '-', affiliate_details.subtype) AS type, \n" + "affiliate_details.organization_name, affiliate_details.organization_type, \n" + "current_usage, planned_usage, purpose, implementation_status, other_activities, \n" + "commercial_usage_count.snomed_practices, commercial_usage_count.hospitals_staffing_practices, \n" + "commercial_usage_count.databases_per_deployment, commercial_usage_count.deployed_data_analysis_systems, \n" + "COUNT(commercial_usage_entry.commercial_usage_id) AS hospitals\n" + "FROM affiliate, affiliate_details, \"member\" m2, commercial_usage_count, commercial_usage\n" + "LEFT JOIN commercial_usage_entry ON commercial_usage.commercial_usage_id = commercial_usage_entry.commercial_usage_id\n" + "WHERE commercial_usage.affiliate_id = affiliate.affiliate_id\n" + "and affiliate.home_member_id = m2.member_id \n" + "AND affiliate.affiliate_details_id = affiliate_details.affiliate_details_id\n" + "AND commercial_usage.commercial_usage_id = commercial_usage_count.commercial_usage_id\n" + "AND commercial_usage.state = 'SUBMITTED'\n" + "AND affiliate_details.email NOT LIKE '%ihtsdo.org%'\n" + "GROUP BY commercial_usage.affiliate_id, m2.\"key\",commercial_usage_entry.country_iso_code_2, commercial_usage_count.country_iso_code_2,\n" + "commercial_usage.start_date, commercial_usage.end_date, affiliate.standing_state, affiliate.created, affiliate_details.first_name, affiliate_details.last_name, affiliate_details.organization_name, affiliate_details.organization_type, current_usage, planned_usage, purpose, implementation_status, other_activities,\n" + "commercial_usage_count.snomed_practices, commercial_usage_count.hospitals_staffing_practices, commercial_usage_count.databases_per_deployment, commercial_usage_count.deployed_data_analysis_systems, affiliate_details.agreement_type, affiliate.notes_internal, affiliate_details.type, affiliate_details.subtype\n" + "ORDER BY organization_name, commercial_usage.affiliate_id \n" + "\n",nativeQuery = true)
    Collection<Collections> findUsageReport();
    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/

}
