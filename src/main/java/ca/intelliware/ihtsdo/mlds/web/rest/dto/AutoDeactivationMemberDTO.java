package ca.intelliware.ihtsdo.mlds.web.rest.dto;

public class AutoDeactivationMemberDTO {

    private int pendingApplications;
    private int usageReports;

    public int getInvoicesPending() {
        return invoicesPending;
    }

    public void setInvoicesPending(int invoicesPending) {
        this.invoicesPending = invoicesPending;
    }

    public int getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(int pendinginvoices) {
        this.pendingApplications = pendinginvoices;
    }

    public int getUsageReports() {
        return usageReports;
    }

    public void setUsageReports(int usageReports) {
        this.usageReports = usageReports;
    }

    private int invoicesPending;
}
