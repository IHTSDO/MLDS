package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="commercial_usage_count")
public class CommercialUsageCountry extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_count_id")
	Long commercialUsageCountId;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="commercial_usage_id")
	CommercialUsage commercialUsage;
	
	@ManyToOne
	Country country;

	Instant created = Instant.now();
	
	String notes;
	
	@Column(name="snomed_practices")
	Integer snomedPractices = 0;
	
	
	// Data Analysis
	
	@Column(name="hospitals_staffing_practices")
	Integer hospitalsStaffingPractices = 0;
	
	@Column(name="data_creation_practices_not_part_of_hospital")
	Integer dataCreationPracticesNotPartOfHospital = 0;
	
	@Column(name="non_practice_data_creation_systems")
	Integer nonPracticeDataCreationSystems = 0;
	
	@Column(name="deployed_data_analysis_systems")
	Integer deployedDataAnalysisSystems = 0;
	
	@Column(name="databases_per_deployment")
	Integer databasesPerDeployment = 0;
	
	public CommercialUsageCountry() {
		
	}
	
	// For Testing
	public CommercialUsageCountry(long commercialUsageCountId, CommercialUsage commercialUsage) {
		this.commercialUsageCountId = commercialUsageCountId;
		commercialUsage.addCount(this);
	}
	
	public Long getCommercialUsageCountId() {
		return commercialUsageCountId;
	}
	
	public void setCommercialUsageCountId(Long commercialUsageCountId) {
		this.commercialUsageCountId = commercialUsageCountId;
	}

	public Country getCountry() {
		return country;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}
		
	@Override
	protected Object getPK() {
		return commercialUsageCountId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getSnomedPractices() {
		return snomedPractices;
	}

	public void setSnomedPractices(int snomedPractices) {
		this.snomedPractices = snomedPractices;
	}

	public Integer getHospitalsStaffingPractices() {
		return hospitalsStaffingPractices;
	}

	public void setHospitalsStaffingPractices(Integer hospitalsStaffingPractices) {
		this.hospitalsStaffingPractices = hospitalsStaffingPractices;
	}

	public Integer getDataCreationPracticesNotPartOfHospital() {
		return dataCreationPracticesNotPartOfHospital;
	}

	public void setDataCreationPracticesNotPartOfHospital(Integer dataCreationPracticesNotPartOfHospital) {
		this.dataCreationPracticesNotPartOfHospital = dataCreationPracticesNotPartOfHospital;
	}

	public Integer getNonPracticeDataCreationSystems() {
		return nonPracticeDataCreationSystems;
	}

	public void setNonPracticeDataCreationSystems(Integer nonPracticeDataCreationSystems) {
		this.nonPracticeDataCreationSystems = nonPracticeDataCreationSystems;
	}

	public Integer getDeployedDataAnalysisSystems() {
		return deployedDataAnalysisSystems;
	}

	public void setDeployedDataAnalysisSystems(Integer deployedDataAnalysisSystems) {
		this.deployedDataAnalysisSystems = deployedDataAnalysisSystems;
	}

	public Integer getDatabasesPerDeployment() {
		return databasesPerDeployment;
	}

	public void setDatabasesPerDeployment(Integer databasesPerDeployment) {
		this.databasesPerDeployment = databasesPerDeployment;
	}
}
