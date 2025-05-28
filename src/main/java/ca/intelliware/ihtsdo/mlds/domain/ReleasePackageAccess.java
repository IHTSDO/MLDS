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

//    @ManyToOne
//    @JoinColumn(name = "release_package_id", referencedColumnName = "release_package_id", insertable = false, updatable = false)
//    private ReleasePackage releasePackage;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
//    private User user;


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

//    public ReleasePackage getReleasePackage() {
//        return releasePackage;
//    }
//
//    public void setReleasePackage(ReleasePackage releasePackage) {
//        this.releasePackage = releasePackage;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
