package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageResource_GetUsageReports_Test {
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
		commercialUsageCountry.setAnalysisPractices(3);
		commercialUsageCountry.setCreationPractices(2);
		CommercialUsageEntry commercialUsageEntry = new CommercialUsageEntry(4L, commercialUsage);
		commercialUsageEntry.setName("Test Name");
		affiliate.addCommercialUsage(commercialUsage);
		Mockito.when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
		
		restCommercialUsageResource.perform(MockMvcRequestBuilders.get(Routes.USAGE_REPORTS, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].note").value("Test Note"))
                .andExpect(jsonPath("$[0].countries[0].practices").value(5))
                .andExpect(jsonPath("$[0].countries[0].analysisPractices").value(3))
                .andExpect(jsonPath("$[0].countries[0].creationPractices").value(2))
                .andExpect(jsonPath("$[0].entries[0].name").value("Test Name"))
                ;

	}
}
