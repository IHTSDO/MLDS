package ca.intelliware.ihtsdo.mlds.registration;


import ca.intelliware.ihtsdo.mlds.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name="email_domain_blacklist")
public class DomainBlacklist extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="domain_id")
    private Long domainId;

    @Column(name="domain_name")
	String domainName;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	protected Object getPK() {
		return domainId;
	}


}
