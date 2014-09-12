package ca.intelliware.ihtsdo.mlds.service;

import org.hamcrest.Matchers;
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

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageServiceTest {
    @Mock
	CommercialUsageAuthorizationChecker authorizationChecker;
    
    @Mock
    CommercialUsageRepository commercialUsageRepository;
    
    @Mock
    CommercialUsageAuditEvents commercialUsageAuditEvents;
    
    @Mock
    CommercialUsageResetter commercialUsageResetter;
    
	CommercialUsageService commercialUsageService;
	
	@Before
    public void setup() {
        commercialUsageService = new CommercialUsageService();
        
        commercialUsageService.authorizationChecker = authorizationChecker;
        commercialUsageService.commercialUsageRepository = commercialUsageRepository;
        commercialUsageService.commercialUsageAuditEvents = commercialUsageAuditEvents;
        commercialUsageService.commercialUsageResetter = commercialUsageResetter;
        
        Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(commercialUsageRepository.saveAndFlush(Mockito.any(CommercialUsage.class))).then(AdditionalAnswers.returnsFirstArg());
        
		withCommercialUsageResetter();
    }

	
	@Test
	public void transitionCommercialUsageApprovalShouldFailForUnsupportedTransitions() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, ApprovalState.APPROVED);
		
		try {
			commercialUsageService.transitionCommercialUsageApproval(commercialUsage, ApprovalTransition.SUBMIT);
			Assert.fail();
		} catch (IllegalStateException e) {
			Assert.assertThat(e.getMessage(), Matchers.containsString("Unsupported approval transition"));
		}
		
		Assert.assertEquals("commercial usage unchanged", ApprovalState.APPROVED, commercialUsage.getApprovalState());
	}
	
	@Test
	public void transitionCommercialUsageApprovalShouldTransitionToSubmittedOnSubmit() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, ApprovalState.NOT_SUBMITTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, ApprovalTransition.SUBMIT);
		
		Assert.assertThat(result.getApprovalState(), Matchers.equalTo(ApprovalState.SUBMITTED));
		Assert.assertThat(result.getCommercialUsageId(), Matchers.equalTo(2L));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionToNotSubmittedOnRetract() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, ApprovalState.APPROVED);

		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, ApprovalTransition.RETRACT);
		
		Assert.assertThat(result.getApprovalState(), Matchers.equalTo(ApprovalState.NOT_SUBMITTED));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldCreateNewUsageOnRetract() throws Exception {
		final CommercialUsage originalCommercialUsage = withCommercialUsage(2L, ApprovalState.APPROVED);
		final long postResetterId = 123;

		// Note that this is working with the mocked commercialUsageResetter
		Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		Mockito.when(commercialUsageRepository.saveAndFlush(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(originalCommercialUsage, ApprovalTransition.RETRACT);
		
		Assert.assertThat(result.getApprovalState(), Matchers.equalTo(ApprovalState.NOT_SUBMITTED));
		Assert.assertThat(result.getCommercialUsageId(), Matchers.equalTo(123L));
		
		Assert.assertEquals(ApprovalState.APPROVED, originalCommercialUsage.getApprovalState());
		Assert.assertThat(originalCommercialUsage.getEffectiveTo(), org.hamcrest.Matchers.notNullValue(Instant.class));
	}

	private Answer<CommercialUsage> duplicateCommercialUsage( final long postResetterId) {
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
		CommercialUsage commercialUsage = withCommercialUsage(2L, ApprovalState.SUBMITTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, ApprovalTransition.REVIEWED);
		
		Assert.assertThat(result.getApprovalState(), Matchers.equalTo(ApprovalState.APPROVED));
	}

	private CommercialUsage withCommercialUsage(long commercialUsageId, ApprovalState approvalState) {
		Affiliate affiliate = new Affiliate(1L);
		CommercialUsage commercialUsage = new CommercialUsage(commercialUsageId, affiliate);
		commercialUsage.setApprovalState(approvalState);
		Mockito.when(commercialUsageRepository.findOne(commercialUsageId)).thenReturn(commercialUsage);
		return commercialUsage;
	}
}
