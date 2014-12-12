package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.util.Validate;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Authority;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PersistentToken;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistService;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.PersistentTokenRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.SecurityUtils;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;
import ca.intelliware.ihtsdo.mlds.service.PasswordResetService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import ca.intelliware.ihtsdo.mlds.service.UserService;
import ca.intelliware.ihtsdo.mlds.service.mail.DuplicateRegistrationEmailSender;
import ca.intelliware.ihtsdo.mlds.service.mail.MailService;
import ca.intelliware.ihtsdo.mlds.service.mail.PasswordResetEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.UserDTO;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private MailService mailService;
    
    @Resource DuplicateRegistrationEmailSender duplicateRegistrationEmailSender;
    
    @Inject
	private DomainBlacklistService domainBlacklistService;
    
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
     */
    @RequestMapping(value = "/rest/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //FIXME: JH-add account to stormpath wrapper
    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS })
    public ResponseEntity<?> registerAccount(@RequestBody UserDTO userDTO, HttpServletRequest request,
                                             HttpServletResponse response) {
        User user = userRepository.findByLoginIgnoreCase(userDTO.getLogin());
        if (user != null) {
        	String passwordResetToken = passwordResetService.createTokenForUser(user);
			duplicateRegistrationEmailSender.sendDuplicateRegistrationEmail(user,passwordResetToken );
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } else {
        	if (domainBlacklistService.isDomainBlacklisted(userDTO.getEmail())) {
        		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        	}
        	
        	List<Application> applications = applicationRepository.findByUsernameIgnoreCase(userDTO.getLogin());
        	List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(userDTO.getLogin());
        	PrimaryApplication application = new PrimaryApplication();
        	Affiliate affiliate = new Affiliate();
        	AffiliateDetails affiliateDetails = new AffiliateDetails();
        	MailingAddress mailingAddress = new MailingAddress();
        	
        	if (applications.size() > 0) {
        		// FIXME MLDS-308 can we assume the first one is the primary?
        		application = (PrimaryApplication) applications.get(0);
        	}
        	
        	if (affiliates.size() > 0) {
        		affiliate = affiliates.get(0);
        	}
        	        	
        	application.setUsername(userDTO.getLogin());
        	affiliateDetails.setFirstName(userDTO.getFirstName());
        	affiliateDetails.setLastName(userDTO.getLastName());
        	affiliateDetails.setEmail(userDTO.getEmail());
        	mailingAddress.setCountry(userDTO.getCountry());
        	affiliateDetails.setAddress(mailingAddress);
        	application.setAffiliateDetails(affiliateDetails);
        	
        	//set a default type for application to create affiliate and usagelog
        	affiliate.setCreator(userDTO.getLogin());
        	// MLDS-719 don't default type affiliateDetails.setType(AffiliateType.COMMERCIAL);
        	//affiliate.setType(AffiliateType.COMMERCIAL);
        	
        	Validate.notNull(userDTO.getCountry(), "Country is mandatory");
        	Member member = userDTO.getCountry().getMember();
        	Validate.notNull(member, "Country must have a responsible member");
        	application.setMember(member);
        	affiliate.setHomeMember(member);
        	
        	affiliateRepository.save(affiliate);
        	affiliateDetailsRepository.save(affiliateDetails);

        	applicationRepository.save(application);
        	
        	affiliate.setApplication(application);
        	affiliateRepository.save(affiliate);
        	
        	CommercialUsage commercialUsage = new CommercialUsage();
	    	commercialUsage.setType(affiliate.getType());
        	
        	commercialUsageResetter.detachAndReset(commercialUsage, userDTO.getInitialUsagePeriod().getStartDate(), userDTO.getInitialUsagePeriod().getEndDate());
        	
        	commercialUsage = commercialUsageRepository.save(commercialUsage);
        	
        	affiliate.addCommercialUsage(commercialUsage);
        	
        	application.setCommercialUsage(commercialUsage);
        	
        	affiliateAuditEvents.logCreationOf(affiliate);
        	
        	//FIXME: JH-Add terms of service check and create new exception layer to pass back to angular
            user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(), userDTO.getFirstName(),
                    userDTO.getLastName(), userDTO.getEmail().toLowerCase(), userDTO.getLangKey(), false);
            final Locale locale = Locale.forLanguageTag(user.getLangKey());
            String content = createHtmlContentFromTemplate(user, locale, request, response);
            mailService.sendActivationEmail(user.getEmail(), content, locale);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/rest/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
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
    @RequestMapping(value = "/rest/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /rest/account -> get the current user.
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @RequestMapping(value = "/rest/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<UserDTO> getAccount() throws ClientProtocolException, IOException {
    	final UserDTO userDto;
        User user = userService.getUserWithAuthorities();
        if (user != null) {
        	userDto = createUserDtoFromUser(user);
        } else {
        	CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserInfo(currentSecurityContext.getCurrentUserName());
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
				userInfo.getName(),
				"XX",
				userInfo.getGivenName(),
				userInfo.getSurname(),
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
    @RequestMapping(value = "/rest/account",
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
    @RequestMapping(value = "/rest/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (StringUtils.isEmpty(password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET  /rest/account/sessions -> get the current open sessions.
     */
    @RequestMapping(value = "/rest/account/sessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        User user = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    @RequestMapping(value = "/rest/account/sessions/{series}",
            method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        User user = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        List<PersistentToken> persistentTokens = persistentTokenRepository.findByUser(user);
        for (PersistentToken persistentToken : persistentTokens) {
            if (StringUtils.equals(persistentToken.getSeries(), decodedSeries)) {
                persistentTokenRepository.delete(decodedSeries);
            }
        }
    }

    private String createHtmlContentFromTemplate(final User user, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("user", user);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
                                 request.getServerName() +       // "myhost"
                                 ":" + request.getServerPort());
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process(MailService.EMAIL_ACTIVATION_PREFIX + MailService.TEMPLATE_SUFFIX, context);
    }
    
    @RequestMapping(value = "/rest/account/create", method = RequestMethod.POST)
    @RolesAllowed({AuthoritiesConstants.ADMIN})
    @Timed
    public ResponseEntity<?> createLogin(@RequestBody Affiliate body, HttpServletRequest request, HttpServletResponse response) {
    	Affiliate affiliate = affiliateRepository.findOne(body.getAffiliateId());
    	User user = userRepository.findByLoginIgnoreCase(body.getAffiliateDetails().getEmail());
    	
    	if (user != null) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	
    	affiliate.setCreator(body.getAffiliateDetails().getEmail().toLowerCase());
    	affiliate.getAffiliateDetails().setEmail(body.getAffiliateDetails().getEmail().toLowerCase());
    	user = userService.createUserInformation(body.getAffiliateDetails().getEmail().toLowerCase(), "", body.getAffiliateDetails().getFirstName(),
    			body.getAffiliateDetails().getLastName(), body.getAffiliateDetails().getEmail().toLowerCase(), "en", true);
    	
    	final String tokenKey = passwordResetService.createTokenForUser(user);
		passwordResetEmailSender.sendPasswordResetEmail(user, tokenKey);
    	
		affiliateAuditEvents.logCreationOfAffiliateLogin(affiliate);
		
    	return new ResponseEntity<>(HttpStatus.CREATED);
    	
    	
    }
    
}
