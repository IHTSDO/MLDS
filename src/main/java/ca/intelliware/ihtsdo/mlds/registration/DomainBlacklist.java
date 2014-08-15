package ca.intelliware.ihtsdo.mlds.registration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import ca.intelliware.ihtsdo.mlds.domain.BaseEntity;

@Entity
@Table(name="email_domain_blacklist")
public class DomainBlacklist extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="domain_id")
    private Long domainId;
	
	String domainname;

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	@Override
	protected Object getPK() {
		return domainId;
	}
	
	
}
