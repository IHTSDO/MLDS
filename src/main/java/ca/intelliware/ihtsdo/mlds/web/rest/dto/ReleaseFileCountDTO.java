package ca.intelliware.ihtsdo.mlds.web.rest.dto;

public class ReleaseFileCountDTO {
    public String getReleasePackageName() {
        return releasePackageName;
    }

    public void setReleasePackageName(String releasePackageName) {
        this.releasePackageName = releasePackageName;
    }

    public String getReleaseVersionName() {
        return releaseVersionName;
    }

    public void setReleaseVersionName(String releaseVersionName) {
        this.releaseVersionName = releaseVersionName;
    }

    public String getReleaseFileName() {
        return releaseFileName;
    }

    public void setReleaseFileName(String releaseFileName) {
        this.releaseFileName = releaseFileName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    private String releasePackageName;
    private String releaseVersionName;
    private String releaseFileName;
    private long count;
}
