package ca.intelliware.ihtsdo.mlds.web.filter.gzip;


import jakarta.servlet.ServletException;

public class GzipResponseHeadersNotModifiableException extends ServletException {
	private static final long serialVersionUID = 1L;
	

    public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}
