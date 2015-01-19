package ca.intelliware.ihtsdo.mlds.service;

import org.hamcrest.Matcher;
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
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
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
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.PENDING_INVOICE);
		
		try {
			commercialUsageService.transitionCommercialUsageApproval(commercialUsage, UsageReportTransition.SUBMIT);
			Assert.fail();
		} catch (IllegalStateException e) {
			Assert.assertThat(e.getMessage(), matchUnsupportedApprovalTransactionException());
		}
		
		Assert.assertEquals("commercial usage unchanged", UsageReportState.PENDING_INVOICE, commercialUsage.getState());
	}


	private Matcher<String> matchUnsupportedApprovalTransactionException() {
		return Matchers.containsString("Unsupported usage report");
	}
	
	@Test
	public void transitionCommercialUsageApprovalShouldTransitionNotSubmittedToSubmittedOnSubmit() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.NOT_SUBMITTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, UsageReportTransition.SUBMIT);
		
		Assert.assertThat(result.getState(), Matchers.equalTo(UsageReportState.SUBMITTED));
		Assert.assertThat(result.getCommercialUsageId(), Matchers.equalTo(2L));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionChangeRequestedToResubmittedOnSubmit() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.CHANGE_REQUESTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, UsageReportTransition.SUBMIT);
		
		Assert.assertThat(result.getState(), Matchers.equalTo(UsageReportState.RESUBMITTED));
		Assert.assertThat(result.getCommercialUsageId(), Matchers.equalTo(2L));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldFailWhenRetractingPreviouslyRetractedUsage() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.PENDING_INVOICE);
		commercialUsage.setEffectiveTo(Instant.now());
		
		try {
			commercialUsageService.transitionCommercialUsageApproval(commercialUsage, UsageReportTransition.RETRACT);
			Assert.fail();
		} catch (IllegalStateException e) {
			Assert.assertThat(e.getMessage(), matchUnsupportedApprovalTransactionException());
		}
	}

	@Test
	public void transitionCommercialUsageApprovalShouldCreateNewUsageOnRetractInChangeRequestedState() throws Exception {
		final CommercialUsage originalCommercialUsage = withCommercialUsage(2L, UsageReportState.PENDING_INVOICE);
		final long postResetterId = 123;

		// Note that this is working with the mocked commercialUsageResetter
		Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		Mockito.when(commercialUsageRepository.saveAndFlush(Mockito.any(CommercialUsage.class))).thenAnswer(duplicateCommercialUsage(postResetterId));
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(originalCommercialUsage,
				UsageReportTransition.RETRACT);
		
		// New commercial usage
		Assert.assertThat(result.getState(), Matchers.equalTo(UsageReportState.CHANGE_REQUESTED));
		Assert.assertThat(result.getCommercialUsageId(), Matchers.equalTo(123L));
		
		// Original commercial usage
		Assert.assertEquals(UsageReportState.PENDING_INVOICE, originalCommercialUsage.getState());
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
				
				savedDuplicate.setState(commercialUsage.getState());
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
				usage.setState(UsageReportState.NOT_SUBMITTED);
				return null;
			}
		}).when(commercialUsageResetter).detachAndReset(Mockito.any(CommercialUsage.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionSubmittedToApprovedOnReviewed() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.SUBMITTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage,
				UsageReportTransition.PENDING_INVOICE);
		
		Assert.assertThat(result.getState(), Matchers.equalTo(UsageReportState.PENDING_INVOICE));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldFailWhenReviewingInactiveUsage() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.SUBMITTED);
		commercialUsage.setEffectiveTo(Instant.now());
		
		try {
			commercialUsageService.transitionCommercialUsageApproval(commercialUsage, UsageReportTransition.PENDING_INVOICE);
			Assert.fail();
		} catch (IllegalStateException e) {
			Assert.assertThat(e.getMessage(), matchUnsupportedApprovalTransactionException());
		}
	}

	@Test
	public void transitionCommercialUsageApprovalShouldTransitionResubmittedToApprovedOnReviewed() throws Exception {
		CommercialUsage commercialUsage = withCommercialUsage(2L, UsageReportState.RESUBMITTED);
		
		CommercialUsage result = commercialUsageService.transitionCommercialUsageApproval(commercialUsage,
				UsageReportTransition.PENDING_INVOICE);
		
		Assert.assertThat(result.getState(), Matchers.equalTo(UsageReportState.PENDING_INVOICE));
	}

	
	private CommercialUsage withCommercialUsage(long commercialUsageId, UsageReportState approvalState) {
		Affiliate affiliate = new Affiliate(1L);
		CommercialUsage commercialUsage = new CommercialUsage(commercialUsageId, affiliate);
		commercialUsage.setState(approvalState);
		Mockito.when(commercialUsageRepository.findOne(commercialUsageId)).thenReturn(commercialUsage);
		return commercialUsage;
	}
}
