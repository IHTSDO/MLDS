package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.util.List;

import ca.intelliware.ihtsdo.mlds.domain.Member;

public class AnnouncementDTO {
	private String subject;
	private String body;
	private Member member;
	private List<String> additionalEmails;
	
	public AnnouncementDTO() {
		
	}

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

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<String> getAdditionalEmails() {
		return additionalEmails;
	}

	public void setAdditionalEmails(List<String> additionalEmails) {
		this.additionalEmails = additionalEmails;
	}


}
