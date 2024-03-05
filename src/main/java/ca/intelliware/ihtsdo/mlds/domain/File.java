package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.sql.Blob;
import java.time.Instant;


@Entity
@Table(name="file")
public class File extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_demo")
	@SequenceGenerator(name = "hibernate_demo", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="file_id")
    private long fileId;
	
	private String filename;
	
	@Lob
	private Blob content;
	
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Blob getContent() {
		return content;
	}

	public void setContent(Blob content) {
		this.content = content;
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
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("fileId", fileId)
			.append("filename", filename)
			.append("updatedAt", updatedAt)
			.append("mimetype", mimetype)
			.toString();
	}
}
