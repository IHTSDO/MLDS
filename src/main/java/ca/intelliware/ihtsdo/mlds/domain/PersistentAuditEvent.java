package ca.intelliware.ihtsdo.mlds.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Instant;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */

@Entity
@Table(name = "T_PERSISTENT_AUDIT_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersistentAuditEvent extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "event_id")
    private long id;

    @NotNull
    private String principal;

    // FIXME MLDS-256 change column definition to timestamp
    @Column(name = "event_date")
    private Instant auditEventDate = Instant.now();
    
    @Column(name = "event_type")
    private String auditEventType;

    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="T_PERSISTENT_AUDIT_EVENT_DATA", joinColumns=@JoinColumn(name="event_id"))
    private Map<String, String> data = new HashMap<>();
    
	@Column(name="affiliate_id")
    private Long affiliateId;

	@Column(name="application_id")
    private Long applicationId;
    
	@Column(name="release_package_id")
    private Long releasePackageId;
    
	@Column(name="release_version_id")
    private Long releaseVersionId;

	@Column(name="release_file_id")
    private Long releaseFileId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Instant getAuditEventDate() {
        return auditEventDate;
    }

    public void setAuditEventDate(Instant auditEventDate) {
        this.auditEventDate = auditEventDate;
    }

    public String getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(String auditEventType) {
        this.auditEventType = auditEventType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

	public Long getAffiliateId() {
		return affiliateId;
	}

	public void setAffiliateId(Long affiliateId) {
		this.affiliateId = affiliateId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public Long getReleasePackageId() {
		return releasePackageId;
	}

	public void setReleasePackageId(Long releasePackageId) {
		this.releasePackageId = releasePackageId;
	}

	public Long getReleaseVersionId() {
		return releaseVersionId;
	}

	public void setReleaseVersionId(Long releaseVersionId) {
		this.releaseVersionId = releaseVersionId;
	}

	public Long getReleaseFileId() {
		return releaseFileId;
	}

	public void setReleaseFileId(Long releaseFileId) {
		this.releaseFileId = releaseFileId;
	}
	
	@Override
	protected Object getPK() {
		return id;
	}
}
