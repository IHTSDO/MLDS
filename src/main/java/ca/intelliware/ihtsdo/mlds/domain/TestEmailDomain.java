package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "test_email_domain")
public class TestEmailDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
    @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
    @Column(name="id")
    private Long id;

    @Column(name = "domain_name", nullable = false, unique = true)
    private String domainName;

    public TestEmailDomain() {
    }

    public TestEmailDomain(String domainName) {
        this.domainName = domainName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
