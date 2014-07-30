package ca.intelliware.ihtsdo.mlds.security;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

public class SecurityContextSetup {
	public static final String USERNAME = "user";
	
	public void asAdmin() {
		withRoles(AuthoritiesConstants.ADMIN);
	}

	public void asIHTSDOStaff() {
		withRoles(AuthoritiesConstants.STAFF, AuthoritiesConstants.STAFF + "_IHTSDO");
	}
	
	private void withRoles(String... role) {
		Authentication authentication = new TestingAuthenticationToken(USERNAME, "password", role);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void asAffiliateUser() {
		withRoles(AuthoritiesConstants.USER);
	}

	public void asAnonymous() {
		withRoles(AuthoritiesConstants.ANONYMOUS);
	}
}