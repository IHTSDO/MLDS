package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "release_package_access")
@IdClass(ReleasePackageAccessId.class)
public class ReleasePackageAccess {

    @Id
    @Column(name = "release_package_id")
    private Long releasePackageId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    public Long getReleasePackageId() {
        return releasePackageId;
    }

    public void setReleasePackageId(Long releasePackageId) {
        this.releasePackageId = releasePackageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
