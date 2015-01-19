package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.service.ApprovalTransition;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageService;
import ca.intelliware.ihtsdo.mlds.service.UsageReportTransition;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class ApplicationResource_SubmitApplication_Test {
	private MockMvc mockMvc;
	
	@Mock ApplicationRepository applicationRepository;
	@Mock ApplicationAuditEvents applicationAuditEvents;
	@Mock ApplicationAuthorizationChecker applicationAuthorizationChecker;
	@Mock MemberRepository memberRepository;
	@Mock CountryRepository countryRepository;
	@Mock ApplicationService applicationService;
	@Mock AffiliateRepository affiliateRepository;
	@Mock AffiliateDetailsRepository affiliateDetailsRepository;
	@Mock AffiliateDetailsResetter affiliateDetailsResetter;
	@Mock AffiliateAuditEvents affiliateAuditEvents;
	@Mock UserRepository userRepository;
	@Mock SessionService sessionService;
	@Mock ApplicationApprovedEmailSender applicationApprovedEmailSender;
	@Mock CommercialUsageService commercialUsageService;
	
	ApplicationResource applicationResource;
	
	Member swedenMember;
	Country sweden;
	
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
        
        applicationResource = new ApplicationResource();
        
        applicationResource.applicationRepository = applicationRepository;
        applicationResource.applicationAuditEvents = applicationAuditEvents;
        applicationResource.authorizationChecker = applicationAuthorizationChecker;
        applicationResource.applicationService = applicationService;
        applicationResource.memberRepository = memberRepository;
        applicationResource.countryRepository = countryRepository;
        applicationResource.affiliateRepository = affiliateRepository;
        applicationResource.affiliateDetailsRepository = affiliateDetailsRepository;
        applicationResource.affiliateDetailsResetter = affiliateDetailsResetter;
        applicationResource.affiliateAuditEvents = affiliateAuditEvents;
        applicationResource.userRepository = userRepository;
        applicationResource.applicationApprovedEmailSender = applicationApprovedEmailSender;
        applicationResource.sessionService = sessionService;
        applicationResource.commercialUsageService = commercialUsageService;
        
        applicationResource.routeLinkBuilder = new RouteLinkBuilder();

        this.mockMvc = MockMvcBuilders
        		.standaloneSetup(applicationResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();

        swedenMember = new Member("SE", 1);
        Mockito.when(memberRepository.findOneByKey("SE")).thenReturn(swedenMember);
        sweden = new Country("SE", "SWE", "Sweden");
        sweden.setMember(swedenMember);
        Mockito.when(countryRepository.findOne("SE")).thenReturn(sweden);

		Mockito.when(sessionService.getUsernameOrNull()).thenReturn("user");
	}

	@Test
	public void submitApplicationShouldFailForUnknownApplication() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isNotFound());

	}

	@Test
	public void submitApplicationShouldFailIfCheckerDenies() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		application.getCommercialUsage().setState(UsageReportState.NOT_SUBMITTED);
		
		Mockito.doThrow(new IllegalStateException("NO_ACCESS")).when(applicationAuthorizationChecker).checkCanAccessApplication(Mockito.any(Application.class));

		try {
			postSubmitApplication(1L, applicationRequestBody())
				.andExpect(status().is5xxServerError());
			Assert.fail();
        } catch (NestedServletException e) {
        	Assert.assertThat(e.getRootCause().getMessage(), Matchers.containsString("NO_ACCESS"));
        }
	}

	
	@Test
	public void submitApplicationShouldPromoteApplicationToSubmitted() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		application.getCommercialUsage().setState(UsageReportState.NOT_SUBMITTED);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isOk());

		Assert.assertEquals(ApprovalState.SUBMITTED, application.getApprovalState());
	}

	@Test
	public void submitApplicationShouldRefuseIllegalTransformation() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.SUBMITTED);
		application.getCommercialUsage().setState(UsageReportState.SUBMITTED);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isConflict());
	}

	@Test
	public void submitApplicationShouldLogSubmit() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		application.getCommercialUsage().setState(UsageReportState.NOT_SUBMITTED);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isOk());

		Mockito.verify(applicationAuditEvents).logApprovalStateChange(Mockito.any(Application.class));
	}


	private String applicationRequestBody() {
		return "{\"applicationId\":1, \"affiliateDetails\":{\"address\":{\"country\":{\"isoCode2\":\"SE\"}}, \"billingAddress\":{}}}";
	}

	@Test
	public void submitApplicationShouldTransitionApprovalForNonCommercialSubmission() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		application.getCommercialUsage().setState(UsageReportState.NOT_SUBMITTED);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isOk());

		Mockito.verify(commercialUsageService).transitionCommercialUsageApproval(Mockito.any(CommercialUsage.class),
				Mockito.eq(UsageReportTransition.SUBMIT));
	}

	@Test
	public void submitApplicationShouldNotTransitionApprovalForAlreadySubmittedSubmission() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L, affiliate);
		application.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		application.getCommercialUsage().setState(UsageReportState.SUBMITTED);

		postSubmitApplication(1L, applicationRequestBody())
			.andExpect(status().isOk());

		Mockito.verify(commercialUsageService, Mockito.never()).transitionCommercialUsageApproval(Mockito.any(CommercialUsage.class),
				Mockito.any(UsageReportTransition.class));
	}

	private ResultActions postSubmitApplication(long primaryApplicationId, String updatedApprovalState) throws Exception {
		return mockMvc.perform(
			MockMvcRequestBuilders
				.post(Routes.APPLICATION_REGISTRATION, primaryApplicationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedApprovalState)
				.accept(MediaType.APPLICATION_JSON));
	}

	private PrimaryApplication withExistingSwedishPrimaryApplication(long primaryApplicationId, Affiliate affiliate) {
		PrimaryApplication primaryApplication = new PrimaryApplication(primaryApplicationId);
		primaryApplication.setAffiliateDetails(new AffiliateDetails());
		primaryApplication.setMember(swedenMember);
		primaryApplication.setAffiliate(affiliate);
		CommercialUsage commercialUsage = new CommercialUsage(10L, affiliate);
		commercialUsage.setType(affiliate.getType());
		primaryApplication.setCommercialUsage(commercialUsage);
		Mockito.when(applicationRepository.findOne(primaryApplicationId)).thenReturn(primaryApplication);
		return primaryApplication;
	}

	private Affiliate withAffiliate(StandingState existingStandingState, AffiliateType affiliateType) {
		Affiliate affiliate = new Affiliate(2L);
		affiliate.setStandingState(existingStandingState);
		affiliate.setAffiliateDetails(new AffiliateDetails());
		affiliate.setType(affiliateType);
		Mockito.when(affiliateRepository.findByCreatorIgnoreCase(Mockito.anyString())).thenReturn(Arrays.asList(affiliate));
		return affiliate;
	}

}
