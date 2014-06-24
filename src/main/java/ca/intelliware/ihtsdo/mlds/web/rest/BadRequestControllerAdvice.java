package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BadRequestControllerAdvice {
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleBadJSONException(HttpMessageNotReadableException ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
