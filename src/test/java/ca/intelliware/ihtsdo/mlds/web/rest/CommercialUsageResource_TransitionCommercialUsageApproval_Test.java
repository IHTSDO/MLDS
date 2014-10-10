package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.ApprovalTransition;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageService;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageResource_TransitionCommercialUsageApproval_Test {
    @Mock AffiliateRepository affiliateRepository;
    @Mock CommercialUsageAuthorizationChecker authorizationChecker;
    @Mock CommercialUsageRepository commercialUsageRepository;
    @Mock CommercialUsageAuditEvents commercialUsageAuditEvents;
    @Mock CommercialUsageResetter commercialUsageResetter;
    @Mock CommercialUsageService commercialUsageService;
    
	CommercialUsageResource commercialUsageResource;
	
	private MockMvc restCommercialUsageResource;
	
	Affiliate affiliate;

	@Before
    public void setup() {
        commercialUsageResource = new CommercialUsageResource();
        
        commercialUsageResource.affiliateRepository = affiliateRepository;
        commercialUsageResource.authorizationChecker = authorizationChecker;
        commercialUsageResource.commercialUsageRepository = commercialUsageRepository;
        commercialUsageResource.commercialUsageAuditEvents = commercialUsageAuditEvents;
        commercialUsageResource.commercialUsageResetter = commercialUsageResetter;
        commercialUsageResource.commercialUsageService = commercialUsageService;
        
        this.restCommercialUsageResource = MockMvcBuilders
        		.standaloneSetup(commercialUsageResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
        
        affiliate = new Affiliate(1L);
    }

	@Test
	public void transitionCommercialUsageApprovalShouldFailForUnknownCommercialUsage() throws Exception {
		
		postTransitionCommercialUsageApproval(999L, ApprovalTransition.SUBMIT)
			.andExpect(status().isNotFound());
	}

	
	@Test
	public void transitionCommercialUsageApprovalShouldPerformTransition() throws Exception {
		withCommercialUsage(2L, ApprovalState.NOT_SUBMITTED);
		
		CommercialUsage transformedCommercialUsage = createCommercialUsage(2L, ApprovalState.SUBMITTED);
		Mockito.when(commercialUsageService.transitionCommercialUsageApproval(Mockito.any(CommercialUsage.class), Mockito.eq(ApprovalTransition.SUBMIT))).thenReturn(transformedCommercialUsage );
		
		postTransitionCommercialUsageApproval(2L, ApprovalTransition.SUBMIT)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.approvalState").value("SUBMITTED"));
	}

	@Test
	public void transitionCommercialUsageApprovalShouldFailForUnsupportedTransitions() throws Exception {
		withCommercialUsage(2L, ApprovalState.NOT_SUBMITTED);
		
		Mockito.when(commercialUsageService.transitionCommercialUsageApproval(Mockito.any(CommercialUsage.class), Mockito.eq(ApprovalTransition.SUBMIT))).thenThrow(new IllegalStateException("UNSUPPORTED TRANSITION"));
		
		try {
			postTransitionCommercialUsageApproval(2L, ApprovalTransition.SUBMIT)
				.andExpect(status().is5xxServerError());
			Assert.fail();
		} catch (NestedServletException e) {
			Assert.assertThat(e.getCause().getMessage(), Matchers.containsString("UNSUPPORTED TRANSITION"));
		}
	}

	private CommercialUsage withCommercialUsage(long commercialUsageId, ApprovalState approvalState) {
		CommercialUsage commercialUsage = createCommercialUsage(commercialUsageId, approvalState);
		Mockito.when(commercialUsageRepository.findOne(commercialUsageId)).thenReturn(commercialUsage);
		return commercialUsage;
	}


	private CommercialUsage createCommercialUsage(long commercialUsageId, ApprovalState approvalState) {
		CommercialUsage commercialUsage = new CommercialUsage(commercialUsageId, affiliate);
		commercialUsage.setApprovalState(approvalState);
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
