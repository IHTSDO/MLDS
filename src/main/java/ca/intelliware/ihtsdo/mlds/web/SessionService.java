package ca.intelliware.ihtsdo.mlds.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
	public boolean isLoggedIn() {
		return getUsernameOrNull() != null;
	}
	
	public String getUsernameOrNull() {
		Authentication authentication = SecurityContextHolder
				.getContext()
				.getAuthentication();
		Object principal = authentication.getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			username = userDetails.getUsername();
		}
		if (principal instanceof String) {
			username = (String) principal;
		}
		return username;
	}
}
