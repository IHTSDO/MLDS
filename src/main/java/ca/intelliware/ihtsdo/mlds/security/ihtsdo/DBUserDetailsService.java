package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.intelliware.ihtsdo.mlds.domain.Authority;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.UserNotActivatedException;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DBUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DBUserDetailsService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserStandingCalculator userStandingCalculator;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Attepting to authenticate '{}' using local database", login);
        String lowercaseLogin = login.toLowerCase();

        User userFromDatabase = userRepository.findByLoginIgnoreCase(lowercaseLogin);
        if (userFromDatabase == null) {
        	String msg = "User " + lowercaseLogin + " was not found in the database";
        	log.debug(msg);
            throw new UsernameNotFoundException(msg);
        } else if (!userFromDatabase.getActivated()) {
        	String msg = "User " + lowercaseLogin + " was not activated";
        	log.debug (msg);
            throw new UserNotActivatedException(msg);
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : userFromDatabase.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        StandingState standingState = userStandingCalculator.getUserAffiliateStanding(login);
        boolean enabled = standingState == null || standingState.canLogin();
        
		return new org.springframework.security.core.userdetails.User(lowercaseLogin, userFromDatabase.getPassword(),
        		enabled , true, true, true, 
                grantedAuthorities);
    }
}
