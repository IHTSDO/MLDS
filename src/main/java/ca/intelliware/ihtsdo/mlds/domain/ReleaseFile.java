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
@Table(name="release_file")
public class ReleaseFile {

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


	public String getDownloadUrl() {
		return downloadUrl;
	}


}
