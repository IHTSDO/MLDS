package ca.intelliware.ihtsdo.mlds.web.rest.dto;

public class AffiliateCheckDTO {

	private Boolean matched;
	private String error;
	
	public Boolean getMatched() {
		return matched;
	}
	public void setMatched(Boolean matched) {
		this.matched = matched;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
