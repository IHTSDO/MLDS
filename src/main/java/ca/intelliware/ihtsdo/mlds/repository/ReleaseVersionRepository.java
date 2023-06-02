package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ReleaseVersionRepository extends JpaRepository<ReleaseVersion, Long> {


    @Query(value="SELECT CONCAT(release_package.name, '-', release_version.name) AS title, release_file.download_url, member.memberOrgName, member.memberOrgURL, member.contactEmail, release_version.id, release_package.copyrights, release_package.updated_at, release_version.published_at, release_version.summary, release_package.releasePackageURI, release_version.versionURI, release_version.versionDependentURI, release_version.versionDependentDerivativeURI, release_package.release_package_id, release_version.release_version_id, release_file.release_file_id FROM release_package JOIN release_version ON release_version.release_package_id = release_package.release_package_id JOIN release_file ON release_file.release_version_id = release_version.release_version_id JOIN member ON member.member_id = release_package.member_id WHERE release_version.release_type = 'online' AND release_version.versionURI is NOT NULL",nativeQuery = true)
    Collection<Object[]> listAtomFeed();

}
