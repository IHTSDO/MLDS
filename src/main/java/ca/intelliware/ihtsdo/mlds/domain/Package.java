package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.Instant;

@Entity
@Table(name="package")
public class Package {

	@Id
	@GeneratedValue
	@Column(name="package_id")
	Long packageId;

	@Column(name="created_at")
	Instant createdAt = Instant.now();
	
	@Column(name="created_by")
	String createdBy;

	String name;
	
	String description;

	public Long getPackageId() {
		return packageId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}
