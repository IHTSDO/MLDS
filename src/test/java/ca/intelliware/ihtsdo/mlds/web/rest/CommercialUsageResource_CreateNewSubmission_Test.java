package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.joda.time.LocalDate;
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
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsagePeriod;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageResource_CreateNewSubmission_Test {
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

	private Affiliate affiliate;

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

		affiliate = new Affiliate(1L);
		affiliate.setType(AffiliateType.COMMERCIAL);
		Mockito.when(affiliateRepository.findOne(affiliate.getAffiliateId())).thenReturn(affiliate);
    }

	@Test
	public void createNewSubmissionShouldCreateNewBlankUsageWhenNoExistingUsages() throws Exception {
		CommercialUsagePeriod usagePeriod = withUsagePeriod("2014-01-01", "2014-06-30");
		
		Mockito.when(commercialUsageRepository.findActiveBySamePeriod(Mockito.any(Affiliate.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(Arrays.<CommercialUsage>asList());
		
		Mockito.when(commercialUsageRepository.findActiveByMostRecentPeriod(Mockito.any(Affiliate.class))).thenReturn(Arrays.<CommercialUsage>asList());
		
		final long newCommercialUsageId = 123L;
		saveCommercialUsageAndAssignIdForNewEntities(newCommercialUsageId);
		
		postCreateNewSubmission(affiliate.getAffiliateId(), usagePeriod)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.commercialUsageId").value(123))
					.andExpect(jsonPath("$.startDate").value(jsonDateMatcher("2014-01-01")))
					.andExpect(jsonPath("$.endDate").value(jsonDateMatcher("2014-06-30")))
					.andExpect(jsonPath("$.type").value("COMMERCIAL"));
	}

	private void saveCommercialUsageAndAssignIdForNewEntities(final long newCommercialUsageId) {
		Mockito.when(commercialUsageRepository.save(Mockito.any(CommercialUsage.class))).then(new Answer<CommercialUsage>() {

			@Override
			public CommercialUsage answer(InvocationOnMock invocation) throws Throwable {
				CommercialUsage commercialUsage = (CommercialUsage) invocation.getArguments()[0];
				if (commercialUsage.getCommercialUsageId() == null) {
					commercialUsage.setCommercialUsageId(newCommercialUsageId);
				}
				return commercialUsage;
			}
			
		});
	}

	@Test
	public void createNewSubmissionShouldReturnExistingUsageWithMatchingPeriod() throws Exception {
		CommercialUsagePeriod usagePeriod = withUsagePeriod("2014-01-01", "2014-06-30");
		CommercialUsage commercialUsage = new CommercialUsage(2L, affiliate);
		commercialUsage.setApprovalState(ApprovalState.SUBMITTED);
		
		Mockito.when(commercialUsageRepository.findActiveBySamePeriod(Mockito.any(Affiliate.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(Arrays.<CommercialUsage>asList(commercialUsage));
		
		postCreateNewSubmission(affiliate.getAffiliateId(), usagePeriod)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.commercialUsageId").value(2));
	}
	
	@Test
	public void createNewSubmissionShouldCreateDuplicateUsageWhenNoMatchingPeriodButMostRecentPeriodPresent() throws Exception {
		CommercialUsagePeriod usagePeriod = withUsagePeriod("2014-01-01", "2014-06-30");
		
		CommercialUsage otherCommercialUsage = new CommercialUsage(2L, affiliate);
		otherCommercialUsage.setStartDate(LocalDate.parse("2014-07-01"));
		otherCommercialUsage.setEndDate(LocalDate.parse("2014-12-31"));
		otherCommercialUsage.setType(AffiliateType.COMMERCIAL);
		otherCommercialUsage.setApprovalState(ApprovalState.SUBMITTED);

		Mockito.when(commercialUsageRepository.findActiveBySamePeriod(Mockito.any(Affiliate.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(Arrays.<CommercialUsage>asList());
		
		Mockito.when(commercialUsageRepository.findActiveByMostRecentPeriod(Mockito.any(Affiliate.class))).thenReturn(Arrays.<CommercialUsage>asList(otherCommercialUsage));
		
		final long newCommercialUsageId = 123L;
		saveCommercialUsageAndAssignIdForNewEntities(newCommercialUsageId);
		
		postCreateNewSubmission(affiliate.getAffiliateId(), usagePeriod)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.commercialUsageId").value(123))
					.andExpect(jsonPath("$.startDate").value(jsonDateMatcher("2014-01-01")))
					.andExpect(jsonPath("$.endDate").value(jsonDateMatcher("2014-06-30")))
					.andExpect(jsonPath("$.type").value("COMMERCIAL"));
	}

	@Test
	public void createNewSubmissionShouldLogWhenNewUsageIsCreated() throws Exception {
		CommercialUsagePeriod usagePeriod = withUsagePeriod("2014-01-01", "2014-06-30");
		
		Mockito.when(commercialUsageRepository.findActiveBySamePeriod(Mockito.any(Affiliate.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(Arrays.<CommercialUsage>asList());
		
		Mockito.when(commercialUsageRepository.findActiveByMostRecentPeriod(Mockito.any(Affiliate.class))).thenReturn(Arrays.<CommercialUsage>asList());
		
		final long newCommercialUsageId = 123L;
		saveCommercialUsageAndAssignIdForNewEntities(newCommercialUsageId);
		
		postCreateNewSubmission(affiliate.getAffiliateId(), usagePeriod)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.commercialUsageId").value(123));
		
		Mockito.verify(commercialUsageAuditEvents).logCreationOf(Mockito.any(CommercialUsage.class));
	}

	private Matcher<Iterable<? extends Integer>> jsonDateMatcher(String date) {
		//TODO JodaTime serializer are not setup properly for mock tests so map to basic output
		LocalDate localDate = LocalDate.parse(date);
		return org.hamcrest.Matchers.contains(localDate.getYear(),localDate.getMonthOfYear(),localDate.getDayOfMonth());
	}
	
	private CommercialUsagePeriod withUsagePeriod(String startDate, String endDate) {
		CommercialUsagePeriod usagePeriod = new CommercialUsagePeriod();
		usagePeriod.setStartDate(LocalDate.parse(startDate));
		usagePeriod.setEndDate(LocalDate.parse(endDate));
		return usagePeriod;
	}


	private void withCommercialUsageResetter() {
		Mockito.doAnswer(new Answer<CommercialUsage>() {
			@Override
			public CommercialUsage answer(InvocationOnMock invocation) throws Throwable {
				//Act as reset and save...
				CommercialUsage usage = (CommercialUsage) invocation.getArguments()[0];
				usage.setCommercialUsageId(null);
				usage.setStartDate((LocalDate) invocation.getArguments()[1]);
				usage.setEndDate((LocalDate) invocation.getArguments()[2]);
				usage.setApprovalState(ApprovalState.NOT_SUBMITTED);
				usage.setEffectiveTo(null);
				return null;
			}
		}).when(commercialUsageResetter).detachAndReset(Mockito.any(CommercialUsage.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
	}

	private ResultActions postCreateNewSubmission(long affiliateId, CommercialUsagePeriod period) throws Exception {
		return restCommercialUsageResource.perform(
				MockMvcRequestBuilders
					.post(Routes.USAGE_REPORTS, affiliateId)
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"startDate\":\""+period.getStartDate()+"\",\"endDate\":\""+period.getEndDate()+"\"}")
					.accept(MediaType.APPLICATION_JSON));
	}
}
