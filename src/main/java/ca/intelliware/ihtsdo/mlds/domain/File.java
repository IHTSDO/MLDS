package ca.intelliware.ihtsdo.mlds.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.Instant;

@Entity
public class File extends BaseEntity {
	@Id
	@Column(name="file_id")
    private long fileId;
	
	private String filename;
	
	private Blob blob;
	
	private String creator;
	
	private String mimetype;
	
	@Column(name="updated_at")
	Instant updatedAt;
	
	@Override
	protected Object getPK() {
		return fileId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public Instant getLastUpdated() {
		return updatedAt;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.updatedAt = lastUpdated;
	}
}
