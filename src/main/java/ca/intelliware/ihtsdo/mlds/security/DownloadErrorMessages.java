package ca.intelliware.ihtsdo.mlds.security;

public final class DownloadErrorMessages {

    private DownloadErrorMessages() {}

    public static final String LOGIN_REQUIRED_TITLE = "Log In Required";
    public static final String LOGIN_REQUIRED_SUBTITLE = "This release download is only available when logged in.";
    public static final String LOGIN_REQUIRED_REASON = "You need to be logged in with appropriate permissions to download this content. Please login to access this release.";

    public static final String PERMISSION_TITLE = "Permission Needed";
    public static final String PERMISSION_SUBTITLE = "You need additional permissions to download this release.";
    public static final String PERMISSION_REASON = "Your account has not yet been granted appropriate permissions to download this content. Please request access to this release.";

    public static final String NOT_FOUND_TITLE = "Release Unavailable";
    public static final String NOT_FOUND_SUBTITLE = "We couldn't find the release you requested.";
    public static final String NOT_FOUND_REASON = "The release content was not found. It may have been removed or renamed.";

    public static final String DOWNLOAD_FAILED_TITLE = "Download Failed";
    public static final String DOWNLOAD_FAILED_SUBTITLE = "We couldn't download this release right now.";
    public static final String DOWNLOAD_FAILED_REASON = "A network connection or server problem prevented completion of your content download. Please try again.";
}
