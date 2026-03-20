package ca.intelliware.ihtsdo.mlds.security;


import org.springframework.http.HttpStatus;

public class DownloadException extends RuntimeException{

    private final HttpStatus status;
    private final String title;
    private final String subtitle;

    public DownloadException(HttpStatus status, String title, String subtitle, String message) {
        super(message);
        this.status = status;
        this.title = title;
        this.subtitle = subtitle;
    }

    public DownloadException(String title, String subtitle, String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.title = title;
        this.subtitle = subtitle;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
