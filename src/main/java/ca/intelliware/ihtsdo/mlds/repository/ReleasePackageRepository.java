package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;

public interface ReleasePackageRepository extends JpaRepository<ReleasePackage, Long> {

}
