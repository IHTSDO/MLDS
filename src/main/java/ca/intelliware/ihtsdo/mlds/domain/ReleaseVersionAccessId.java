package ca.intelliware.ihtsdo.mlds.domain;

import java.io.Serializable;
import java.util.Objects;

public class ReleaseVersionAccessId implements Serializable {

    private Long releaseVersionId;
    private Long userId;

    public ReleaseVersionAccessId() {
    }

    public ReleaseVersionAccessId(Long releaseVersionId, Long userId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseVersionAccessId that = (ReleaseVersionAccessId) o;
        return Objects.equals(releaseVersionId, that.releaseVersionId) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseVersionId, userId);
    }
}
