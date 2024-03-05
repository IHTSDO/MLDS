package ca.intelliware.ihtsdo.mlds.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ca.intelliware.ihtsdo.mlds.config.Constants;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

//    public String getCurrentAuditor() {
//        String userName = SecurityUtils.getCurrentLogin();
//        return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
//    }

    @Override
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentLogin();
        return Optional.ofNullable(userName != null ? userName : Constants.SYSTEM_ACCOUNT); // Wrap username in Optional
    }

}
