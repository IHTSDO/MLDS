package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackageConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleasePackageConfigRepository extends JpaRepository<ReleasePackageConfig, Long> {

    ReleasePackageConfig findByReleaseType(String releaseType);

    ReleasePackageConfig findByReleaseTypeIgnoreCase(String releaseType);

    List<ReleasePackageConfig> findByReleasePermissionTypeNot(String releasePermissionType);

    List<ReleasePackageConfig> findByReleasePermissionType(String releasePermissionType);
}
