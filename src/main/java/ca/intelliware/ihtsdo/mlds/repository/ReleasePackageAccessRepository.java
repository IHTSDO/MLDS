package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackageAccess;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackageAccessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReleasePackageAccessRepository extends JpaRepository<ReleasePackageAccess, ReleasePackageAccessId> {
    List<ReleasePackageAccess> findByReleasePackageId(Long releasePackageId);

    boolean existsByReleasePackageIdAndUserId(long releasePackageId, long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleasePackageAccess rpa WHERE rpa.releasePackageId = :packageId")
    void deleteReleasePackageAccessByPackageId(@Param("packageId") Long packageId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleasePackageAccess rpa WHERE rpa.releasePackageId IN :packageIds")
    void deleteReleasePackageAccessByPackageIds(@Param("packageIds") List<Long> packageIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleasePackageAccess rpa WHERE rpa.releasePackageId = :packageId AND rpa.userId = :userId")
    void deleteReleasePackageAccessByPackageIdAndUserId(@Param("packageId") Long packageId, @Param("userId") Long userId);

    @Query(value = "SELECT u.login FROM user u JOIN release_package_access rpa ON u.user_id = rpa.user_id WHERE rpa.release_package_id = :releasePackageId",nativeQuery = true)
    List<String> findLoginsByReleasePackageId(@Param("releasePackageId") Long releasePackageId);

    @Query(value = "SELECT u.login FROM user u WHERE u.user_id IN :userIds", nativeQuery = true)
    List<String> findLoginsByUserIds(@Param("userIds") List<Long> userIds);



}
