package ca.intelliware.ihtsdo.mlds.repository;


import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleaseVersionCheckViewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReleaseVersionRepository extends JpaRepository<ReleaseVersion, Long> {


    @Query(value="SELECT CONCAT(release_package.name, '-', release_version.name) AS title, release_file.download_url, member.memberOrgName, member.memberOrgURL, member.contactEmail, release_version.id, release_package.copyrights, release_version.updated_at, release_version.published_at, release_version.summary, release_package.releasePackageURI, release_version.versionURI, release_version.versionDependentURI, release_version.versionDependentDerivativeURI, release_package.release_package_id, release_version.release_version_id, release_file.release_file_id, release_file.primary_file, release_file.md5_hash, release_file.file_size, release_version.package_type FROM release_package JOIN release_version ON release_version.release_package_id = release_package.release_package_id JOIN release_file ON release_file.release_version_id = release_version.release_version_id JOIN member ON member.member_id = release_package.member_id WHERE release_version.release_type = 'online' AND release_package.releasePackageURI <> '' AND release_version.versionURI <> '' ",nativeQuery = true)
    Collection<Object[]> listAtomFeed();

    @Query(value = "SELECT CASE WHEN :releaseVersionURI IS NULL OR TRIM(:releaseVersionURI) = '' THEN 0 ELSE CASE WHEN EXISTS (SELECT 1 FROM release_version rv JOIN release_package rp ON rv.release_package_id = rp.release_package_id WHERE (TRIM(rv.versionDependentURI) = TRIM(:releaseVersionURI) OR TRIM(rv.versionDependentDerivativeURI) = TRIM(:releaseVersionURI)) AND rp.inactive_at IS NULL) THEN 1 ELSE 0 END END AS is_present;", nativeQuery = true)
    Long checkDependent(@Param("releaseVersionURI") String releaseVersionURI);

    @Query(value = "SELECT rv.name AS releaseVersionName, rv.release_package_id AS releasePackageId, rp.inactive_at AS inactiveAt FROM release_version rv JOIN release_package rp ON rv.release_package_id = rp.release_package_id WHERE rv.versionDependentURI = :releaseVersionURI AND rp.inactive_at IS NULL", nativeQuery = true)
    List<ReleaseVersionCheckViewDTO> getDependentVersionNames(@Param("releaseVersionURI") String releaseVersionURI);

}
