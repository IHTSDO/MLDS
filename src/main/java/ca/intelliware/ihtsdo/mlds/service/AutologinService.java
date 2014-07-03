package ca.intelliware.ihtsdo.mlds.service;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.User;

/**
 * Service to log a user in from application code.
 * @author buckleym
 */
@Service
public class AutologinService {
	@Resource
	UserDetailsService userDetailsService;
	
	@Resource
	HttpServletRequest request;

	public void loginUser(User user) {
		// force creation of http session so Spring Security can persist the auth on filter exit.
		request.getSession(true);
		
		Collection<? extends GrantedAuthority> authorities = userDetailsService.loadUserByUsername(user.getLogin()).getAuthorities();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), "", authorities);
		
		SecurityContextHolder.getContext().setAuthentication(authentication );
	}
}