package ca.intelliware.ihtsdo.mlds.domain;

import java.io.Serializable;
import java.util.Objects;

public class ReleasePackageAccessId implements Serializable {

    private Long releasePackageId;
    private Long userId;

    public ReleasePackageAccessId() {}

    public ReleasePackageAccessId(Long releasePackageId, Long userId) {
        this.releasePackageId = releasePackageId;
        this.userId = userId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleasePackageAccessId that = (ReleasePackageAccessId) o;
        return Objects.equals(releasePackageId, that.releasePackageId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releasePackageId, userId);
    }
}
