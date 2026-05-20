package ca.intelliware.ihtsdo.mlds.repository;


import ca.intelliware.ihtsdo.mlds.domain.TestEmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEmailDomainRepository
    extends JpaRepository<TestEmailDomain, Long> {
}
