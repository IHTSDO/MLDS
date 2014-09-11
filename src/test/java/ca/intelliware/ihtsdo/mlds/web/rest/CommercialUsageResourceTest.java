package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
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
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;
import ca.intelliware.ihtsdo.mlds.web.rest.CommercialUsageResource.ApprovalTransition;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageResourceTest {
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
	public void getUsageReportsShouldReturn404ForUnknownAffiliate() throws Exception {
		Mockito.when(affiliateRepository.findOne(999L)).thenReturn(null);
		
		restCommercialUsageResource.perform(MockMvcRequestBuilders.get(Routes.USAGE_REPORTS, 999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
	}

	@Test
	public void getUsageReportsShouldReturnUsageReportsForAffiliate() throws Exception {
		Affiliate affiliate = new Affiliate();
		CommercialUsage commercialUsage = new CommercialUsage(2L, affiliate);
		commercialUsage.setNote("Test Note");
		CommercialUsageCountry commercialUsageCountry = new CommercialUsageCountry(3L, commercialUsage);
		commercialUsageCountry.setPractices(13);
		CommercialUsageEntry commercialUsageEntry = new CommercialUsageEntry(4L, commercialUsage);
		commercialUsageEntry.setName("Test Name");
		affiliate.addCommercialUsage(commercialUsage);
		Mockito.when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
		
		restCommercialUsageResource.perform(MockMvcRequestBuilders.get(Routes.USAGE_REPORTS, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].note").value("Test Note"))
                .andExpect(jsonPath("$[0].countries[0].practices").value(13))
                .andExpect(jsonPath("$[0].entries[0].name").value("Test Name"))
                ;

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
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
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
