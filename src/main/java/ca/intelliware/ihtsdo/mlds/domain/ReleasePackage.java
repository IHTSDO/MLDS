package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.Instant;

@Entity
@Table(name="release_package")
public class ReleasePackage {

	@Id
	@GeneratedValue
	@Column(name="release_package_id")
	Long releasePackageId;

	@Column(name="created_at")
	Instant createdAt = Instant.now();
	
	@Column(name="created_by")
	String createdBy;

	String name;
	
	String description;

	public Long getReleasePackageId() {
		return releasePackageId;
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
