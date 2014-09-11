package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;
import ca.intelliware.ihtsdo.mlds.web.rest.CommercialUsageResource.ApprovalTransition;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageResource_TransitionCommercialUsageApproval_Test {
    @Mock
    AffiliateRepository affiliateRepository;
    
    @Mock
	CommercialUsageAuthorizationChecker authorizationChecker;
    
    @Mock
    CommercialUsageRepository commercialUsageRepository;
    
    @Mock
    CommercialUsageAuditEvents commercialUsageAuditEvents;
    
    @Mock
    CommercialUsageResetter commercialUsageResetter;
    
	CommercialUsageResource commercialUsageResource;
	
	private MockMvc restCommercialUsageResource;

	@Before
    public void setup() {
        commercialUsageResource = new CommercialUsageResource();
        
        commercialUsageResource.affiliateRepository = affiliateRepository;
        commercialUsageResource.authorizationChecker = authorizationChecker;
        commercialUsageResource.commercialUsageRepository = commercialUsageRepository;
        commercialUsageResource.commercialUsageAuditEvents = commercialUsageAuditEvents;
        commercialUsageResource.commercialUsageResetter = commercialUsageResetter;
        
        this.restCommercialUsageResource = MockMvcBuilders
        		.standaloneSetup(commercialUsageResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
        
        Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(commercialUsageRepository.saveAndFlush(Mockito.any(CommercialUsage.class))).then(AdditionalAnswers.returnsFirstArg());
        
		withCommercialUsageResetter();

    }

	
	@Test
	public void transitionCommercialUsageApprovalShouldFailForUnsupportedTransitions() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, ApprovalState.APPROVED);
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.SUBMIT)
			.andExpect(status().isConflict());
		
		Assert.assertEquals("commercial usage unchanged", ApprovalState.APPROVED, commercialUsage.getApprovalState());
	}
	
	@Test
	public void transitionCommercialUsageApprovalShouldTransitionToSubmittedOnSubmit() throws Exception {
		withCommercialUsage(2L, ApprovalState.NOT_SUBMITTED);
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.SUBMIT)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.approvalState").value("SUBMITTED"))
					.andExpect(jsonPath("$.commercialUsageId").value(2));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionToNotSubmittedOnRetract() throws Exception {
		withCommercialUsage(2L, ApprovalState.APPROVED);
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.RETRACT)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.approvalState").value("NOT_SUBMITTED"));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldCreateNewUsageOnRetract() throws Exception {
		final CommercialUsage originalCommercialUsage = withCommercialUsage(2L, ApprovalState.APPROVED);
		final long postResetterId = 123;

		// Note that this is working with the mocked commercialUsageResetter
		Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		Mockito.when(commercialUsageRepository.saveAndFlush(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.RETRACT)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.approvalState").value("NOT_SUBMITTED"))
					.andExpect(jsonPath("$.commercialUsageId").value(123));
		
		Assert.assertEquals(ApprovalState.APPROVED, originalCommercialUsage.getApprovalState());
		Assert.assertThat(originalCommercialUsage.getEffectiveTo(), org.hamcrest.Matchers.notNullValue(Instant.class));
	}

	private Answer<CommercialUsage> duplicateCommercialUsage(
			final long postResetterId) {
		return new Answer<CommercialUsage>() {

			@Override
			public CommercialUsage answer(InvocationOnMock invocation) throws Throwable {
				CommercialUsage commercialUsage = (CommercialUsage) invocation.getArguments()[0];
				CommercialUsage savedDuplicate = new CommercialUsage(
						commercialUsage.getCommercialUsageId() != null ? commercialUsage.getCommercialUsageId() : postResetterId, 
						commercialUsage.getAffiliate());
				
				savedDuplicate.setApprovalState(commercialUsage.getApprovalState());
				return savedDuplicate;
			}
		};
	}

	private void withCommercialUsageResetter() {
		Mockito.doAnswer(new Answer<CommercialUsage>() {
			@Override
			public CommercialUsage answer(InvocationOnMock invocation) throws Throwable {
				//Act as reset and save...
				CommercialUsage usage = (CommercialUsage) invocation.getArguments()[0];
				usage.setCommercialUsageId(null);
				usage.setApprovalState(ApprovalState.NOT_SUBMITTED);
				return null;
			}
		}).when(commercialUsageResetter).detachAndReset(Mockito.any(CommercialUsage.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionToApprovedOnReviewed() throws Exception {
		withCommercialUsage(2L, ApprovalState.SUBMITTED);
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.REVIEWED)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.approvalState").value("APPROVED"))
					.andExpect(jsonPath("$.commercialUsageId").value(2));
	}

	private CommercialUsage withCommercialUsage(long commercialUsageId, ApprovalState approvalState) {
		Affiliate affiliate = new Affiliate(1L);
		CommercialUsage commercialUsage = new CommercialUsage(commercialUsageId, affiliate);
		commercialUsage.setApprovalState(approvalState);
		Mockito.when(commercialUsageRepository.findOne(commercialUsageId)).thenReturn(commercialUsage);
		return commercialUsage;
	}

	private ResultActions postTransitionCommercialUsageApproval(long commercialUsageId, ApprovalTransition approvalTransition) throws Exception {
		return restCommercialUsageResource.perform(
				MockMvcRequestBuilders
					.post(Routes.USAGE_REPORT_APPROVAL, commercialUsageId)
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"transition\":\""+approvalTransition+"\"}")
					.accept(MediaType.APPLICATION_JSON));
	}
}
