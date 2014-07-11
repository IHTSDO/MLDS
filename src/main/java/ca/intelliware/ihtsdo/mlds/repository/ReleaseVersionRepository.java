package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;

public interface ReleaseVersionRepository extends JpaRepository<ReleaseVersion, Long> {

}
