package ca.intelliware.ihtsdo.mlds.repository;


import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ReleaseVersionRepository extends JpaRepository<ReleaseVersion, Long> {


    @Query(value="SELECT CONCAT(release_package.name, '-', release_version.name) AS title, release_file.download_url, member.member_org_name, member.member_org_url, member.contact_email, release_version.id, release_package.copyrights, release_version.updated_at, release_version.published_at, release_version.summary, release_package.release_package_uri, release_version.version_uri, release_version.version_dependent_uri, release_version.version_dependent_derivative_uri, release_package.release_package_id, release_version.release_version_id, release_file.release_file_id, release_file.primary_file, release_file.md5_hash, release_file.file_size, release_version.package_type FROM release_package JOIN release_version ON release_version.release_package_id = release_package.release_package_id JOIN release_file ON release_file.release_version_id = release_version.release_version_id JOIN member ON member.member_id = release_package.member_id WHERE release_version.release_type = 'online' AND release_package.release_package_uri <> '' AND release_version.version_uri <> '' ",nativeQuery = true)
    Collection<Object[]> listAtomFeed();

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM release_version WHERE version_dependent_uri = :release_version_uri OR version_dependent_derivative_uri = :release_version_uri) THEN 1 ELSE 0 END as is_present", nativeQuery = true)
    Long checkDependent(@Param("release_version_uri") String releaseVersionURI);



}
