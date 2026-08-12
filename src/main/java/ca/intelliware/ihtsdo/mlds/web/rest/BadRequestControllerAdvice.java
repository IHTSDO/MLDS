package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.security.DownloadErrorMessages;
import ca.intelliware.ihtsdo.mlds.security.DownloadException;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@ControllerAdvice
public class BadRequestControllerAdvice {

    private static final String ACCESS_DENIED = "Access Denied";

    private final TemplateEngine templateEngine;

    @Value("${support.contact.email}")
    private String supportEmail;

    @Resource
    private CurrentSecurityContext currentSecurityContext;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBadJSONException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    public BadRequestControllerAdvice(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @ExceptionHandler({
        DownloadException.class,
        org.springframework.security.access.AccessDeniedException.class
    })
    public ResponseEntity<String> handleDownloadExceptions(
        Exception ex,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {

        if (!isBrowserRequest(request) || response.isCommitted()) {
            return fallbackResponse(ex, request);
        }

        ErrorDetails error = resolveErrorDetails(ex, request);

        String contactMessage = resolveContactMessage(error.status, error.title);

        renderHtmlResponse(response, error, contactMessage);

        return null;
    }

    private boolean isBrowserRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }


    private ErrorDetails resolveErrorDetails(Exception ex, HttpServletRequest request) {

        if (ex instanceof DownloadException dex) {
            return new ErrorDetails(
                dex.getTitle(),
                dex.getSubtitle(),
                dex.getMessage(),
                dex.getStatus()
            );
        }

        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            boolean isDownload = request != null && request.getRequestURI() != null && request.getRequestURI().contains("/download");
            return new ErrorDetails(
                isDownload ? DownloadErrorMessages.LOGIN_REQUIRED_TITLE : ACCESS_DENIED,
                isDownload ? DownloadErrorMessages.LOGIN_REQUIRED_SUBTITLE : "Authentication required",
                isDownload ? DownloadErrorMessages.LOGIN_REQUIRED_REASON : "You must be logged in with appropriate permissions to access this content.",
                HttpStatus.UNAUTHORIZED
            );
        }

        return new ErrorDetails(
            ACCESS_DENIED,
            "Sorry, you do not have permission to access this content.",
            ex.getMessage(),
            HttpStatus.FORBIDDEN
        );
    }

    private void renderHtmlResponse(HttpServletResponse response,
                                    ErrorDetails error,
                                    String contactMessage) throws IOException {

        Context ctx = new Context();
        ctx.setVariable("title", error.title);
        ctx.setVariable("subtitle", error.subtitle);
        ctx.setVariable("reason", error.reason);
        ctx.setVariable("contactMessage", contactMessage);

        response.setStatus(error.status.value());
        response.setContentType("text/html;charset=UTF-8");
        templateEngine.process("permission-denied", ctx, response.getWriter());
    }

    private String resolveContactMessage(HttpStatus status, String title) {

        boolean showContact =
            (status == HttpStatus.NOT_FOUND
                || DownloadErrorMessages.DOWNLOAD_FAILED_TITLE.equals(title)
                || status == HttpStatus.FORBIDDEN)
                && currentSecurityContext.isUser()
                && !currentSecurityContext.isStaffOrAdmin();

        return showContact ? buildContactMessage() : null;
    }


    private ResponseEntity<String> fallbackResponse(Exception ex, HttpServletRequest request) {
        if (ex instanceof DownloadException dex) {
            return ResponseEntity.status(dex.getStatus()).body(dex.getMessage());
        }

        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            boolean isDownload = request != null && request.getRequestURI() != null && request.getRequestURI().contains("/download");
            String message = isDownload ? DownloadErrorMessages.LOGIN_REQUIRED_REASON : ACCESS_DENIED;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(message);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    private static class ErrorDetails {
        String title;
        String subtitle;
        String reason;
        HttpStatus status;

        ErrorDetails(String title, String subtitle, String reason, HttpStatus status) {
            this.title = title;
            this.subtitle = subtitle;
            this.reason = reason;
            this.status = status;
        }
    }


    private String buildContactMessage() {
        return String.format(
            "Please contact <a href=\"mailto:%s\">%s</a> for further assistance.",
            supportEmail,
            supportEmail
        );
    }
}
