package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.time.LocalDate;

public class AuditEventRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isExcludeAdminAndStaff;


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

    public boolean isExcludeAdminAndStaff() {
        return isExcludeAdminAndStaff;
    }

    public void setExcludeAdminAndStaff(boolean excludeAdminAndStaff) {
        isExcludeAdminAndStaff = excludeAdminAndStaff;
    }
}
