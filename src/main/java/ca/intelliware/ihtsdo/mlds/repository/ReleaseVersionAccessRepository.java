package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersionAccess;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersionAccessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReleaseVersionAccessRepository extends JpaRepository<ReleaseVersionAccess, ReleaseVersionAccessId> {

    boolean existsByReleaseVersionIdAndUserId(Long releaseVersionId, Long userId);

    @Query("SELECT rva.user.login FROM ReleaseVersionAccess rva WHERE rva.releaseVersionId = :releaseVersionId")
    List<String> findLoginsByReleaseVersionId(@Param("releaseVersionId") Long releaseVersionId);

    @Query(value = "SELECT u.login FROM user u WHERE u.user_id IN :userIds", nativeQuery = true)
    List<String> findLoginsByUserIds(@Param("userIds") List<Long> userIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleaseVersionAccess rpa WHERE rpa.releaseVersionId = :versionId")
    void deleteReleaseVersionAccessByVersionId(@Param("versionId") Long versionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleaseVersionAccess rpa WHERE rpa.releaseVersionId = :versionId AND rpa.userId = :userId")
    void deleteReleaseVersionAccessByVersionIdAndUserId(@Param("versionId") Long versionId, @Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ReleaseVersionAccess rva
        WHERE rva.releaseVersionId IN :versionIds
        """)
    void deleteByReleaseVersionIds(@Param("versionIds") List<Long> versionIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReleaseVersionAccess")
    void deleteAllAccess();

    void deleteByReleaseVersionId(Long releaseVersionId);
}
