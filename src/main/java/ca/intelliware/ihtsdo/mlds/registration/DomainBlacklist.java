package ca.intelliware.ihtsdo.mlds.registration;


import ca.intelliware.ihtsdo.mlds.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name="email_domain_blacklist")
public class DomainBlacklist extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_demo")
	@SequenceGenerator(name = "hibernate_demo", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
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
