package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class AuditEventRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;

    public List<String> getExcludeUsers() {
        return excludeUsers;
    }

    public void setExcludeUsers(List<String> excludeUsers) {
        this.excludeUsers = excludeUsers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    private List<String> excludeUsers;
}
