package ca.intelliware.ihtsdo.mlds.domain;

import ca.intelliware.ihtsdo.mlds.domain.json.ReleaseFileSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


import java.time.Instant;

@Entity
@Table(name = "release_file")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = ReleaseFileSerializer.class)
public class ReleaseFile extends BaseEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "hibernate_sequence_generator"
    )
    @SequenceGenerator(
        name = "hibernate_sequence_generator",
        sequenceName = "mlds.hibernate_sequence",
        allocationSize = 1
    )
    @Column(name = "release_file_id")
    private Long releaseFileId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "release_version_id")
    private ReleaseVersion releaseVersion;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    private String label;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "primary_file")
    private boolean primaryFile;

    @Column(name = "md5_hash")
    private String md5Hash;

    @Column(name = "file_size")
    private Long fileSize;

    public ReleaseFile() {
    }

    public ReleaseFile(Long releaseFileId) {
        this.releaseFileId = releaseFileId;
    }

    public Long getReleaseFileId() {
        return releaseFileId;
    }

    public void setReleaseFileId(Long releaseFileId) {
        this.releaseFileId = releaseFileId;
    }

    public ReleaseVersion getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(ReleaseVersion releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    public boolean getPrimaryFile() {
        return primaryFile;
    }

    public void setPrimaryFile(boolean primaryFile) {
        this.primaryFile = primaryFile;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    protected Object getPK() {
        return releaseFileId;
    }
}
