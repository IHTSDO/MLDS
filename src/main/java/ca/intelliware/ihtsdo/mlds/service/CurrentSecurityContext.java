package ca.intelliware.ihtsdo.mlds.service;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentSecurityContext {

	public String getCurrentUserName() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return securityContext.getAuthentication().getName();
	}
	
}