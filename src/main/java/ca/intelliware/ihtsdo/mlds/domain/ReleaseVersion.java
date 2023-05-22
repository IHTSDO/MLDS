package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

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
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

@Entity
@Table(name="release_version")
public class ReleaseVersion extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name="release_version_id")
	Long releaseVersionId;

    @GeneratedValue
    @Column(name = "id")
    private String id;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="release_package_id")
	ReleasePackage releasePackage;

	@Column(name="created_at")
	Instant createdAt = Instant.now();

	@JsonIgnore
	@Column(name="created_by")
	String createdBy;

	String name;

    @Column(name="summary")
    String summary;

    @Column(name="release_type")
    String releaseType;

	String description;

	boolean online;

    @Column(name="versionDependentDerivativeURI")
    String versionDependentDerivativeURI;

    @Column(name="versionDependentURI")
    String versionDependentURI;

    @Column(name="versionURI")
    String versionURI;

	@Column(name="published_at")
	LocalDate publishedAt;

	@OneToMany(mappedBy="releaseVersion")
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

	// for tests

    public void setReleaseVersionId(Long releaseVersionId) {
		this.releaseVersionId = releaseVersionId;
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

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getPublishedAt() {
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
		if (online && publishedAt == null) {
			this.publishedAt = new LocalDate();
		}

		this.online = online;
	}

	@Override
	protected Object getPK() {
		return releaseVersionId;
	}

	public void setReleaseFiles(Set<ReleaseFile> releaseFiles) {
		this.releaseFiles = releaseFiles;
	}

	public void setPublishedAt(LocalDate publishedAt2) {
		publishedAt = publishedAt2;
	}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getVersionDependentDerivativeURI() {
        return versionDependentDerivativeURI;
    }

    public void setVersionDependentDerivativeURI(String versionDependentDerivativeURI) {
        this.versionDependentDerivativeURI = versionDependentDerivativeURI;
    }

    public String getVersionDependentURI() {
        return versionDependentURI;
    }

    public void setVersionDependentURI(String versionDependentURI) {
        this.versionDependentURI = versionDependentURI;
    }

    public String getVersionURI() {
        return versionURI;
    }

    public void setVersionURI(String versionURI) {
        this.versionURI = versionURI;
    }
}
