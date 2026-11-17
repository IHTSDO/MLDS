package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class NotificationResponseDTO {

    private int totalRecipients;
    private int sentCount;
    private int failedCount;
    private String status;
    private List<String> errors = new ArrayList<>();

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(
        int totalRecipients,
        int sentCount,
        int failedCount,
        String status,
        List<String> errors
    ) {
        this.totalRecipients = totalRecipients;
        this.sentCount = sentCount;
        this.failedCount = failedCount;
        this.status = status;
        this.errors = errors;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void setSentCount(int sentCount) {
        this.sentCount = sentCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
