package ca.intelliware.ihtsdo.mlds.web.rest.dto;



import ca.intelliware.ihtsdo.mlds.domain.File;

import java.time.Instant;

public class FileDTO {
    private long fileId;
	private String filename;
	//skip creator as we leak creator email address
	private String mimetype;
	Instant lastUpdated;
	//skip bytes of File
	
	public FileDTO() {
		
	}
	
	public FileDTO(File file) {
		this.fileId = file.getFileId();
		this.filename = file.getFilename();
		this.mimetype = file.getMimetype();
		this.lastUpdated = file.getLastUpdated();
	}

	public long getFileId() {
		return fileId;
	}

	public String getFilename() {
		return filename;
	}

	public String getMimetype() {
		return mimetype;
	}

	public Instant getLastUpdated() {
		return lastUpdated;
	}
}