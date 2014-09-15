package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

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

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;

public class ApplicationResource_ApproveApplication_Test {
	private MockMvc mockMvc;
	
	@Mock
	ApplicationRepository applicationRepository;
	
	@Mock
	ApplicationAuditEvents applicationAuditEvents;
	
	@Mock
	ApplicationAuthorizationChecker applicationAuthorizationChecker;
	
	@Mock
	MemberRepository memberRepository;
	
	@Mock
	ApplicationService applicationService;
	
	@Mock
	AffiliateRepository affiliateRepository;
	
	@Mock
	AffiliateDetailsRepository affiliateDetailsRepository;
	
	@Mock
	AffiliateDetailsResetter affiliateDetailsResetter;
	
	@Mock
	AffiliateAuditEvents affiliateAuditEvents;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	ApplicationApprovedEmailSender applicationApprovedEmailSender;
	
	ApplicationResource applicationResource;
	
	Member sweden;
	
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
        
        applicationResource = new ApplicationResource();
        
        applicationResource.applicationRepository = applicationRepository;
        applicationResource.applicationAuditEvents = applicationAuditEvents;
        applicationResource.authorizationChecker = applicationAuthorizationChecker;
        applicationResource.applicationService = applicationService;
        applicationResource.memberRepository = memberRepository;
        applicationResource.affiliateRepository = affiliateRepository;
        applicationResource.affiliateDetailsRepository = affiliateDetailsRepository;
        applicationResource.affiliateDetailsResetter = affiliateDetailsResetter;
        applicationResource.affiliateAuditEvents = affiliateAuditEvents;
        applicationResource.userRepository = userRepository;
        applicationResource.applicationApprovedEmailSender = applicationApprovedEmailSender;
        
        applicationResource.routeLinkBuilder = new RouteLinkBuilder();

        this.mockMvc = MockMvcBuilders.standaloneSetup(applicationResource).build();

		sweden = new Member("SE", 1);
		Mockito.when(memberRepository.findOneByKey("SE")).thenReturn(sweden);

	}

	
	@Test
	public void approveApplicationShouldPromoteStandingToGoodForAcceptedPrimaryApplicationAndLogChange() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING);
		withExistingSwedishPrimaryApplication(1L);
		
		postApproveApplication(1L, "APPROVED")
				.andExpect(status().isOk());
		
		Assert.assertEquals(StandingState.IN_GOOD_STANDING, affiliate.getStandingState());
		Mockito.verify(affiliateAuditEvents).logStandingStateChange(Mockito.any(Affiliate.class));
	}

	@Test
	public void approveApplicationShouldPromoteStandingToRejctedForRejectedPrimaryApplicationAndLogChange() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING);
		withExistingSwedishPrimaryApplication(1L);
		
		postApproveApplication(1L, "REJECTED")
				.andExpect(status().isOk());
		
		Assert.assertEquals(StandingState.REJECTED, affiliate.getStandingState());
		Mockito.verify(affiliateAuditEvents).logStandingStateChange(Mockito.any(Affiliate.class));
	}

	private ResultActions postApproveApplication(long primaryApplicationId, String updatedApprovalState) throws Exception {
		return mockMvc.perform(
			MockMvcRequestBuilders
				.post(Routes.APPLICATION_APPROVE, primaryApplicationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedApprovalState)
				.accept(MediaType.APPLICATION_JSON));
	}

	private PrimaryApplication withExistingSwedishPrimaryApplication(long primaryApplicationId) {
		PrimaryApplication primaryApplication = new PrimaryApplication(primaryApplicationId);
		primaryApplication.setAffiliateDetails(new AffiliateDetails());
		primaryApplication.setMember(sweden);
		Mockito.when(applicationRepository.findOne(primaryApplicationId)).thenReturn(primaryApplication);
		return primaryApplication;
	}

	@Test
	public void approveApplicationShouldNotModifyStandingForNonCompleteApprovalState() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.APPLYING);
		withExistingSwedishPrimaryApplication(1L);
		
		postApproveApplication(1L, "REVIEW_REQUESTED")
				.andExpect(status().isOk());
		
		Assert.assertEquals(StandingState.APPLYING, affiliate.getStandingState());
		Mockito.verify(affiliateAuditEvents, Mockito.never()).logStandingStateChange(Mockito.any(Affiliate.class));
	}

	private Affiliate withAffiliate(StandingState existingStandingState) {
		Affiliate affiliate = new Affiliate(2L);
		affiliate.setStandingState(existingStandingState);
		Mockito.when(affiliateRepository.findByCreator(Mockito.anyString())).thenReturn(Arrays.asList(affiliate));
		return affiliate;
	}

	@Test
	public void approveApplicationShouldEmailUserForAcceptedApplication() throws Exception {
		withAffiliate(StandingState.APPLYING);
		withExistingSwedishPrimaryApplication(1L);
		
		postApproveApplication(1L, "APPROVED")
				.andExpect(status().isOk());
		
		Mockito.verify(applicationApprovedEmailSender).sendApplicationApprovalEmail(Mockito.any(User.class), Mockito.eq(sweden.getKey()), Mockito.anyLong());
	}

	@Test
	public void approveApplicationShouldUpdateApplicationApprovalState() throws Exception {
		withAffiliate(StandingState.APPLYING);
		PrimaryApplication application = withExistingSwedishPrimaryApplication(1L);
		
		postApproveApplication(1L, "APPROVED")
				.andExpect(status().isOk());
		
		Assert.assertEquals(ApprovalState.APPROVED, application.getApprovalState());
		Assert.assertNotNull(application.getCompletedAt());
		Mockito.verify(applicationRepository).save(application);
	}

}
