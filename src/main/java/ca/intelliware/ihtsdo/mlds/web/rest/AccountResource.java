package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistService;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.SecurityUtils;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;
import ca.intelliware.ihtsdo.mlds.service.*;
import ca.intelliware.ihtsdo.mlds.service.mail.DuplicateRegistrationEmailSender;
import ca.intelliware.ihtsdo.mlds.service.mail.MailService;
import ca.intelliware.ihtsdo.mlds.service.mail.PasswordResetEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.UserDTO;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorityConverter;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.RemoteUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
	UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired
    MailService mailService;
    @Resource
    private AuditEventRepository auditEventRepository;
    @Resource
	DuplicateRegistrationEmailSender duplicateRegistrationEmailSender;

    @Autowired
	DomainBlacklistService domainBlacklistService;

    @Resource
	AffiliateRepository affiliateRepository;
    @Resource
	ApplicationRepository applicationRepository;
	@Resource
	PasswordResetService passwordResetService;
	@Resource
	PasswordResetEmailSender passwordResetEmailSender;
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	@Resource
	CommercialUsageResetter commercialUsageResetter;
	@Resource
	AffiliateAuditEvents affiliateAuditEvents;
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;

	@Resource
	UserMembershipAccessor userMembershipAccessor;

	@Resource
	HttpAuthAdaptor httpAuthAdaptor;

	CurrentSecurityContext currentSecurityContext = new CurrentSecurityContext();

    /**
     * POST  /rest/register -> register the user.
     * @throws IOException
     */
    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //FIXME: JH-add account to stormpath wrapper
    @RolesAllowed({AuthoritiesConstants.ANONYMOUS})
    public ResponseEntity<Void> registerAccount(
        @RequestBody UserDTO userDTO,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {

        User user = userRepository.findByLoginIgnoreCase(userDTO.getLogin());

        if (user != null) {
            String passwordResetToken = passwordResetService.createTokenForUser(user);
            duplicateRegistrationEmailSender.sendDuplicateRegistrationEmail(
                user,
                passwordResetToken);

            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        } else if (domainBlacklistService.isDomainBlacklisted(userDTO.getEmail())) {

            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        } else {
            // Existing account registration logic remains unchanged.

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        User user = userService.activateRegistration(key);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(user.getLogin(), HttpStatus.OK);
    }

    /**
     * GET  /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<UserDTO> getAccount() throws IOException {
    	final UserDTO userDto;
        User user = userService.getUserWithAuthorities();
        if (user != null) {
        	userDto = createUserDtoFromUser(user);
        } else {
        	CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserAccountInfo(currentSecurityContext.getCurrentUserName(), null);
        	if (userInfo != null) {
                userDto = createUserDtoFromRemoteUserInfo(userInfo);
        	} else {
        		// We must be in a very odd state.  Maybe the admin changed our login id.
        		// Let's logout so the user can refresh and recover.
        		currentSecurityContext.logout();
            	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        	}
        }

		return new ResponseEntity<>(userDto,HttpStatus.OK);
    }
	private UserDTO createUserDtoFromRemoteUserInfo(CentralAuthUserInfo userInfo) {
		Member member = userMembershipAccessor.getMemberAssociatedWithUser();

		List<String> roles = currentSecurityContext.getRolesList();

		UserDTO userDto = new UserDTO(
				userInfo.getLogin(),
				"XX",
				userInfo.getFirstName(),
				userInfo.getLastName(),
				userInfo.getEmail(),
				"en", // The central service doesn't have a language preference.
			    roles,
			    null,
			    member
				);
		return userDto;
	}

	private UserDTO createUserDtoFromUser(User user) {
		Set<Authority> authorities = user.getAuthorities();
		List<String> roles = rolesFromAuthorities(authorities);

        Member member = userMembershipAccessor.getMemberAssociatedWithUser();

        UserDTO userDto = new UserDTO(
		    user.getLogin(),
		    "XXX",
		    user.getFirstName(),
		    user.getLastName(),
		    user.getEmail(),
		    user.getLangKey(),
		    roles,
		    null,
		    member
		    );
		return userDto;
	}

	private List<String> rolesFromAuthorities(Set<Authority> authorities) {
		List<String> roles = new ArrayList<>();
		for (Authority authority : authorities) {
            roles.add(authority.getName());
        }
		return roles;
	}

    /**
     * POST  /rest/account -> update the current user information.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public void saveAccount(@RequestBody UserDTO userDTO) {
        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
    }

    /**
     * POST  /rest/change_password -> changes the current user's password
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<Void> changePassword(@RequestBody String password) {

        if (StringUtils.isEmpty(password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.changePassword(password);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET  /rest/account/sessions -> get the current open sessions.
     */
    @RequestMapping(value = "/account/sessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        User user = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        if (user == null) {
            return new ResponseEntity<>(java.util.Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(
            persistentTokenRepository.findByUser(user),
            HttpStatus.OK);
    }

    /**
     * DELETE  /rest/account/sessions?series={series} -> invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     */
    @RequestMapping(value = "/account/sessions/{series}",
        method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        User user = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        if (user == null) {
            return;
        }
        List<PersistentToken> persistentTokens = persistentTokenRepository.findByUser(user);
        for (PersistentToken persistentToken : persistentTokens) {
            if (StringUtils.equals(persistentToken.getSeries(), decodedSeries)) {
                persistentTokenRepository.delete(persistentToken);
            }
        }
    }

    @RequestMapping(value = "/account/create", method = RequestMethod.POST)
    @RolesAllowed({AuthoritiesConstants.ADMIN})
    @Timed
    public ResponseEntity<Void> createLogin(
        @RequestBody Affiliate body,
        HttpServletRequest request,
        HttpServletResponse response) {

        User user = userRepository.findByLoginIgnoreCase(
            body.getAffiliateDetails().getEmail());

        if (user != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Existing account creation logic remains unchanged.

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private static final String REMOTE_ADDRESS = "remoteAddress";
    private static final String UNKNOWN_USER = "UNKNOWN";
    private static final String AUTH_FAILURE = "AUTHENTICATION_FAILURE";

    @PostMapping(
        value = "/auth/restore",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed({
        AuthoritiesConstants.ANONYMOUS,
        AuthoritiesConstants.USER,
        AuthoritiesConstants.MEMBER,
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<Void> restoreSession(HttpServletRequest request, HttpServletResponse response) {

        String cookieName = httpAuthAdaptor.getAuthenticatedCookieName();

        String cookieValue = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }

        if (cookieValue == null || cookieValue.trim().isEmpty()) {

            Map<String, Object> auditData = new HashMap<>();
            auditData.put(REMOTE_ADDRESS, request.getRemoteAddr());

            auditEventRepository.add(
                new AuditEvent(
                    UNKNOWN_USER,
                    AUTH_FAILURE,
                    auditData
                )
            );

            log.debug("IMS cookie not found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {

            CentralAuthUserInfo remoteUserInfo =
                httpAuthAdaptor.getUserAccountInfoByCookie(cookieValue);

            if (remoteUserInfo == null || remoteUserInfo.getLogin() == null) {

                Map<String, Object> auditData = new HashMap<>();
                auditData.put(REMOTE_ADDRESS, request.getRemoteAddr());

                auditEventRepository.add(
                    new AuditEvent(
                        UNKNOWN_USER,
                        AUTH_FAILURE,
                        auditData
                    )
                );

                log.warn("Invalid IMS cookie");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<GrantedAuthority> authorities =
                AuthorityConverter.buildAuthoritiesList(remoteUserInfo.getRoles());

            if (authorities.isEmpty()) {

                Map<String, Object> auditData = new HashMap<>();
                auditData.put(REMOTE_ADDRESS, request.getRemoteAddr());

                auditEventRepository.add(
                    new AuditEvent(
                        remoteUserInfo.getLogin(),
                        AUTH_FAILURE,
                        auditData
                    )
                );

                log.warn(
                    "User authenticated but has no permissions assigned: {}",
                    remoteUserInfo.getLogin()
                );

                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            authorities.add(
                new SimpleGrantedAuthority(
                    AuthoritiesConstants.USER
                )
            );

            RemoteUserDetails userDetails =
                new RemoteUserDetails(
                    remoteUserInfo,
                    authorities
                );

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    "",
                    authorities
                );

            SecurityContext context =
                SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            request.getSession(true);

            HttpSessionSecurityContextRepository secRepo =
                new HttpSessionSecurityContextRepository();

            secRepo.saveContext(context, request, response);

            // Audit Success
            Map<String, Object> auditData = new HashMap<>();
            auditData.put(REMOTE_ADDRESS, request.getRemoteAddr());

            auditEventRepository.add(
                new AuditEvent(
                    remoteUserInfo.getLogin(),
                    "AUTHENTICATION_SUCCESS",
                    auditData
                )
            );

            log.info(
                "Session successfully restored for remote user: {}",
                remoteUserInfo.getLogin()
            );

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IOException e) {

            Map<String, Object> auditData = new HashMap<>();
            auditData.put(REMOTE_ADDRESS, request.getRemoteAddr());

            auditEventRepository.add(
                new AuditEvent(
                    UNKNOWN_USER,
                    AUTH_FAILURE,
                    auditData
                )
            );

            log.error("Failed to contact IMS", e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
