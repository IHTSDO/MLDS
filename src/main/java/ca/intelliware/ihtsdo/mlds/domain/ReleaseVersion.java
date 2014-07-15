package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

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
	
	boolean online;

	@Column(name="published_at")
	Instant publishedAt;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="releaseVersion")
	Set<ReleaseFile> releaseFiles = Sets.newHashSet();

	public ReleaseVersion() {
		
	}

	//For tests
	public ReleaseVersion(Long releaseVersionId) {
		this();
		this.releaseVersionId = releaseVersionId;
	}
	
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

	public void addReleaseFile(ReleaseFile newReleaseFile) {
		Validate.notNull(newReleaseFile.releaseFileId);
		
		if (newReleaseFile.releaseVersion != null) {
			newReleaseFile.releaseVersion.releaseFiles.remove(newReleaseFile);
		}
		newReleaseFile.releaseVersion = this;
		releaseFiles.add(newReleaseFile);
	}

	public Set<ReleaseFile> getReleaseFiles() {
		return Collections.unmodifiableSet(releaseFiles);
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
