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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="release_file")
@JsonIgnoreProperties(ignoreUnknown=true) // we're attaching a clientDownloadUrl that we should ignore when de-serializing
public class ReleaseFile extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name="release_file_id")
	Long releaseFileId;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="release_version_id")
	ReleaseVersion releaseVersion;

	@Column(name="created_at")
	Instant createdAt = Instant.now();
	
	String label;
	
	@Column(name="download_url")
	String downloadUrl;

	public ReleaseFile() { }
	
	public ReleaseFile(long releaseFileId) {
		this.releaseFileId = releaseFileId;
	}
	
	// Note:
	// String clientDownloadUrl = rest endpoint calculated in JSON JacksonConfigurer

	public Instant getCreatedAt() {
		return createdAt;
	}


	public Long getReleaseFileId() {
		return releaseFileId;
	}


	public ReleaseVersion getReleaseVersion() {
		return releaseVersion;
	}


	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	protected Object getPK() {
		return releaseFileId;
	}

}
