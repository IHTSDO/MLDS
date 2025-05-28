package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReleasePackageRepository extends JpaRepository<ReleasePackage, Long> {

	List<ReleasePackage> findByMemberOrderByPriorityDesc(Member member);


    List<ReleasePackage> findAllByReleasePackageIdIn(List<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE ReleasePackage rp SET rp.permissionType = :permissionType WHERE rp.releasePackageId IN :packageIds")
    void updatePermissionTypeForPackages(@Param("permissionType") ReleasePermissionType permissionType, @Param("packageIds") List<Long> packageIds);

    @Transactional
    @Modifying
    @Query("UPDATE ReleasePackage rp SET rp.permissionType = :permissionType")
    void updatePermissionTypeForAllPackages(@Param("permissionType") ReleasePermissionType permissionType);
}
