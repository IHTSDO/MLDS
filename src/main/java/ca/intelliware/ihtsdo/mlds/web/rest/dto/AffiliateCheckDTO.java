package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AffiliateCheckDTO {

	private Boolean matched;
	private ErrorDetails error;
	
	public Boolean getMatched() {
		return matched;
	}
	public void setMatched(Boolean matched) {
		this.matched = matched;
	}
	public ErrorDetails getError() {
		return error;
	}
	public void setError(ErrorDetails error) {
		this.error = error;
	}
	
	public void setErrorMessage(String message) {
		if (error == null) {
			error = new ErrorDetails();
		}
		error.setMessage(message);
	}
	
	public static class ErrorDetails {
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
