package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "release_version_access")
@IdClass(ReleaseVersionAccessId.class)
public class ReleaseVersionAccess {

    @Id
    @Column(name = "release_version_id")
    private Long releaseVersionId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_version_id", insertable = false, updatable = false)
    private ReleaseVersion releaseVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public ReleaseVersionAccess() {
    }

    public ReleaseVersionAccess(Long releaseVersionId, Long userId) {
        this.releaseVersionId = releaseVersionId;
        this.userId = userId;
    }

    public Long getReleaseVersionId() {
        return releaseVersionId;
    }

    public void setReleaseVersionId(Long releaseVersionId) {
        this.releaseVersionId = releaseVersionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReleaseVersion getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(ReleaseVersion releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReleaseVersionAccess that)) return false;

        return new ReleaseVersionAccessId(releaseVersionId, userId)
            .equals(new ReleaseVersionAccessId(that.releaseVersionId, that.userId));
    }

    @Override
    public int hashCode() {
        return new ReleaseVersionAccessId(releaseVersionId, userId).hashCode();
    }
}
