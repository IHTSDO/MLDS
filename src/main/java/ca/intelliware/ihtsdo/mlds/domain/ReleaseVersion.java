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
@Table(name="release_version")
public class ReleaseVersion {

	@Id
	@GeneratedValue
	@Column(name="release_version_id")
	Long releaseVersionId;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="release_package_id")
	ReleasePackage releasePackage;

	@Column(name="created_at")
	Instant createdAt = Instant.now();
	
	@Column(name="created_by")
	String createdBy;

	String name;
	
	String description;

	@Column(name="published_at")
	Instant publishedAt;

	public Long getReleaseVersionId() {
		return releaseVersionId;
	}

	public ReleasePackage getReleasePackage() {
		return releasePackage;
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

	public Instant getPublishedAt() {
		return publishedAt;
	}

}
