package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AffiliateCheckDTO {

	private Boolean matched;
	
	//FIXME embed error details here as unable to override status code and message for exceptions....
	private String error;
	private Integer status;
	private String message;
	
	
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
