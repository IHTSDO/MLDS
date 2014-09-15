package ca.intelliware.ihtsdo.mlds.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import ca.intelliware.ihtsdo.mlds.domain.ApplicationErrorCodes;

/**
 * Returns a 401 error code (Unauthorized) to the client, when Ajax authentication fails.
 */
@Component
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Authentication failed: " + exception.getLocalizedMessage();
        if (exception instanceof DisabledException) {
			errorMessage = "Authentication failed: " + ApplicationErrorCodes.MLDS_ERR_AUTH_DEREGISTERED + " " + exception.getLocalizedMessage();
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }
}
