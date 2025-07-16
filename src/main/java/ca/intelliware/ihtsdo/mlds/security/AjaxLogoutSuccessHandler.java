package ca.intelliware.ihtsdo.mlds.security;

import java.io.IOException;


import ca.intelliware.ihtsdo.mlds.config.UserTokenStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
    implements LogoutSuccessHandler {

    private final UserTokenStore userTokenStore;

    @Autowired
    public AjaxLogoutSuccessHandler(UserTokenStore userTokenStore) {
        this.userTokenStore = userTokenStore;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
        throws IOException, ServletException {

        if (authentication != null) {
            String username = authentication.getName();
            userTokenStore.remove(username);
            logger.info("Cleared stored token for user '{}'");
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Logout successful\"}");
    }
}

