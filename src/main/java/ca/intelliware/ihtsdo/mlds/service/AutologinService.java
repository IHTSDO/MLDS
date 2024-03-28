package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;


import java.util.Collection;

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

    @Resource
    HttpServletResponse response;

	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

	public void loginUser(User user) {
		// force creation of http session so Spring Security can persist the auth on filter exit.
		request.getSession(true);

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLogin());
		userDetailsChecker.check(userDetails);
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), "", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        HttpSessionSecurityContextRepository secRepo = new HttpSessionSecurityContextRepository();
        secRepo.saveContext(context, request, response);
	}
}
