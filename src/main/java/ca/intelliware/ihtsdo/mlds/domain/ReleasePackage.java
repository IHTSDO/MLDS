package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;

import com.google.common.collect.Sets;

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
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="releasePackage")
	Set<ReleaseVersion> releaseVersions = Sets.newHashSet();

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

	public void addReleaseVersion(ReleaseVersion newReleaseVersion) {
		Validate.notNull(newReleaseVersion.releaseVersionId);
		
		if (newReleaseVersion.releasePackage != null) {
			newReleaseVersion.releasePackage.releaseVersions.remove(newReleaseVersion);
		}
		newReleaseVersion.releasePackage = this;
		releaseVersions.add(newReleaseVersion);
	}

	public Set<ReleaseVersion> getReleaseVersions() {
		return Collections.unmodifiableSet(releaseVersions);
	}

	public void setCreatedBy(String currentUserName) {
		this.createdBy = currentUserName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
