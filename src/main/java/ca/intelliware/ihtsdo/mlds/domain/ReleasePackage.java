package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

@Entity
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE release_package SET inactive_at = now() WHERE release_package_id = ?")
@Table(name="release_package")
public class ReleasePackage extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name="release_package_id")
	Long releasePackageId;

	@Column(name="created_at")
	Instant createdAt = Instant.now();

	@JsonIgnore
	@Column(name="created_by")
	String createdBy;

    @Column(name="copyrights")
    String copyrights;

    @Column(name="releasePackageURI")
    String releasePackageURI;

    @Column(name="updated_at")
    Instant updatedAt = Instant.now();

	@JsonIgnore
	@Column(name="inactive_at")
	Instant inactiveAt;

	@ManyToOne(optional=false)
	@JoinColumn(name="member_id")
	Member member;

	String name;

	String description;

	private Integer priority;

	@JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="licence_file")
	private File licenceFile;

	@OneToMany(mappedBy="releasePackage")
	Set<ReleaseVersion> releaseVersions = Sets.newHashSet();

	public ReleasePackage() {

	}

	//For tests

    public ReleasePackage(Long releasePackageId) {
		this.releasePackageId = releasePackageId;
	}
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

	public void setReleaseVersions(Set<ReleaseVersion> releaseVersions) {
		this.releaseVersions = releaseVersions;
	}

	@Override
	protected Object getPK() {
		return releasePackageId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@JsonIgnore
	public File getLicenceFile() {
		return licenceFile;
	}

	public void setLicenceFile(File licenceFile) {
		this.licenceFile = licenceFile;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public String getReleasePackageURI() {
        return releasePackageURI;
    }

    public void setReleasePackageURI(String releasePackageURI) {
        this.releasePackageURI = releasePackageURI;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
