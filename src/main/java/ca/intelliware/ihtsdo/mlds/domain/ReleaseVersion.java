package ca.intelliware.ihtsdo.mlds.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name="release_version")
public class ReleaseVersion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="release_version_id")
	Long releaseVersionId;


    @Column(name = "id")
    private String id;

	// the parent
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
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

    @Column(name="version_dependent_derivative_uri")
    String versionDependentDerivativeURI;

    @Column(name="version_dependent_uri")
    String versionDependentURI;

    @Column(name="version_uri")
    String versionURI;

	@Column(name="published_at")
	LocalDate publishedAt;

    @Column(name="updated_at")
    Instant updatedAt = Instant.now();

    @Column(name="package_type")
    String packageType;

	@OneToMany(mappedBy="releaseVersion")
	Set<ReleaseFile> releaseFiles = Sets.newHashSet();

    @Column(name="archive")
    boolean archive;

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
		// Set publishedAt to current date if online is true and publishedAt is null
		this.publishedAt = LocalDate.now();
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastUpdated() {
        return updatedAt;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.updatedAt = lastUpdated;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
