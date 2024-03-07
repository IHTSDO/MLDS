package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistService;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.*;
import ca.intelliware.ihtsdo.mlds.service.mail.DuplicateRegistrationEmailSender;
import ca.intelliware.ihtsdo.mlds.service.mail.MailService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountResource_Register_Test {

    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private UserMembershipAccessor userMembershipAccessor;
    @Mock private PasswordResetService passwordResetService;
    @Mock private DuplicateRegistrationEmailSender duplicateRegistrationEmailSender;
    @Mock private DomainBlacklistService domainBlacklistService;
    @Mock private HttpAuthAdaptor httpAuthAdaptor;
    @Mock private AffiliateRepository affiliateRepository;
    @Mock private AffiliateDetailsRepository affiliateDetailsRepository;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private CommercialUsageResetter commercialUsageResetter;
    @Mock private AffiliateAuditEvents affiliateAuditEvents;
    @Mock private MailService mailService;

    private MockMvc restUserMockMvc;

    private SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    private Member swedenMember = new Member("SE", 1);
    private Country country = null;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        AccountResource accountResource = new AccountResource();

        ReflectionTestUtils.setField(accountResource, "userService", userService);
        accountResource.userRepository = userRepository;
        accountResource.userMembershipAccessor = userMembershipAccessor;
        accountResource.passwordResetService = passwordResetService;
        accountResource.duplicateRegistrationEmailSender = duplicateRegistrationEmailSender;
        accountResource.domainBlacklistService = domainBlacklistService;
        accountResource.httpAuthAdaptor = httpAuthAdaptor;
        accountResource.affiliateRepository = affiliateRepository;
        accountResource.affiliateDetailsRepository = affiliateDetailsRepository;
        accountResource.applicationRepository = applicationRepository;
        accountResource.commercialUsageResetter = commercialUsageResetter;
        accountResource.affiliateAuditEvents = affiliateAuditEvents;
        accountResource.mailService = mailService;

        this.restUserMockMvc = MockMvcBuilders
        		.standaloneSetup(accountResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();

        country = new Country("SE", "SWE", "Sweden");
        country.setMember(swedenMember);
    }

    @Test
    public void shouldFailWhenRegisteringWithExistingLocalAccountEmail() throws Exception {
    	Mockito.when(userRepository.findByLoginIgnoreCase(Mockito.eq("existing@test.com"))).thenReturn(new User());

    	postRegister("existing@test.com")
    		.andExpect(status().isNotModified());
    }

    @Test
    public void shouldSendDuplicateEmailNotificationWhenAttemptingToRegisterWithExistingLocalAccountEmail() throws Exception {
    	Mockito.when(userRepository.findByLoginIgnoreCase(Mockito.eq("existing@test.com"))).thenReturn(new User());

    	Mockito.when(passwordResetService.createTokenForUser(Mockito.any(User.class))).thenReturn("TEST_TOKEN");

    	postRegister("existing@test.com")
    		.andExpect(status().isNotModified());

    	Mockito.verify(duplicateRegistrationEmailSender, Mockito.times(1)).sendDuplicateRegistrationEmail(Mockito.any(User.class), Mockito.eq("TEST_TOKEN"));
    }

    @Test
    public void shouldFailForBlacklistedEmailAddress() throws Exception {
    	Mockito.when(domainBlacklistService.isDomainBlacklisted(Mockito.eq("bad@test.com"))).thenReturn(true);

    	postRegister("bad@test.com")
    		.andExpect(status().isNotAcceptable());
    }

/*    @Test  //No longer using stormpath
    public void shouldFailWhenRegisteringWithExistingStormpathAccountEmail() throws Exception {
    	Mockito.when(httpAuthAdaptor.getUserAccountInfo(Mockito.eq("staff@test.com"), null)).thenReturn(new CentralAuthUserInfo());

    	postRegister("staff@test.com")
    		.andExpect(status().isNotAcceptable());
    }*/

    @Test
    @Disabled
    public void shouldCreateAffiliateAndRelatedRecords() throws Exception {

    	postRegister("user@test.com")
    		.andExpect(status().isOk());
    }

	private ResultActions postRegister(String login) throws Exception {
		String content = "{ \"login\": \""+login+"\", \"email\": \""+login+"\", \"country\":{ \"isoCode2\":\"SE\"} }";
		return restUserMockMvc.perform(
			MockMvcRequestBuilders
				.post("/api/register")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
	}

}
