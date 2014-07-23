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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Instant;

import ca.intelliware.ihtsdo.mlds.registration.Application;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */

@Entity
@Table(name = "T_PERSISTENT_AUDIT_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersistentAuditEvent  {

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
    
    @ManyToOne
	@JoinColumn(name="affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
	@JoinColumn(name="application_id")
    private Application application;
    
    @ManyToOne
	@JoinColumn(name="release_package_id")
    private ReleasePackage releasePackage;
    
    @ManyToOne
	@JoinColumn(name="release_version_id")
    private ReleaseVersion releaseVersion;

    @ManyToOne
	@JoinColumn(name="release_file_id")
    private ReleaseFile releaseFile;

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

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Affiliate affiliate) {
		this.affiliate = affiliate;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ReleasePackage getReleasePackage() {
		return releasePackage;
	}

	public void setReleasePackage(ReleasePackage releasePackage) {
		this.releasePackage = releasePackage;
	}

	public ReleaseVersion getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(ReleaseVersion releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public ReleaseFile getReleaseFile() {
		return releaseFile;
	}

	public void setReleaseFile(ReleaseFile releaseFile) {
		this.releaseFile = releaseFile;
	}
}
