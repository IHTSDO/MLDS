package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.util.List;

public class TestAnnouncementDTO {

    private String subject;

    private String body;

    private List<String> testEmails;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getTestEmails() {
        return testEmails;
    }

    public void setTestEmails(List<String> testEmails) {
        this.testEmails = testEmails;
    }
}
