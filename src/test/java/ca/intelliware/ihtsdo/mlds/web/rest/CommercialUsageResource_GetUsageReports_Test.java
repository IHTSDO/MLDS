package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageAuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
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
        when(affiliateRepository.findById(999L)).thenReturn(Optional.empty());

		restCommercialUsageResource.perform(MockMvcRequestBuilders.get(Routes.USAGE_REPORTS, 999L)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
	}

	@Test
	public void getUsageReportsShouldReturnUsageReportsForAffiliate() throws Exception {
		Affiliate affiliate = new Affiliate();
		CommercialUsage commercialUsage = new CommercialUsage(2L, affiliate);
		commercialUsage.setNote("Test Note");
		CommercialUsageCountry commercialUsageCountry = new CommercialUsageCountry(3L, commercialUsage);
		commercialUsageCountry.setSnomedPractices(3);
		CommercialUsageEntry commercialUsageEntry = new CommercialUsageEntry(4L, commercialUsage);
		commercialUsageEntry.setName("Test Name");
		affiliate.addCommercialUsage(commercialUsage);
		when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));

		restCommercialUsageResource.perform(MockMvcRequestBuilders.get(Routes.USAGE_REPORTS, 1L)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].note").value("Test Note"))
                .andExpect(jsonPath("$[0].countries[0].snomedPractices").value(3))
                .andExpect(jsonPath("$[0].entries[0].name").value("Test Name"))
                ;

    }
    @Test
    public void testReviewUsageReportCsv() {
        Object[] sampleRow1 = {
            1, "MemberKey123", "US", "UK", "2024-01-01", "2024-12-31", "Active",
            "2024-06-01", "AgreementTypeA", "John Doe", "Type-Subtype", "OrganizationName1",
            "OrganizationType1", 5, 10, "Research", "Completed", "None", 2, 1, 3, 4, 20
        };
        Object[] sampleRow2 = {
            2, "MemberKey456", "CA", "FR", "2024-02-01", "2024-11-30", "Inactive",
            "2024-06-10", "AgreementTypeB", "Jane Smith", "Type-Subtype", "OrganizationName2",
            "OrganizationType2", 15, 25, "Education", "In Progress", "Training", 4, 3, 2, 1, 50
        };
        List<Object[]> sampleData = Arrays.asList(sampleRow1, sampleRow2);
        when(commercialUsageRepository.findUsageReport()).thenReturn(sampleData);
        Collection<Object[]> response = commercialUsageResource.reviewUsageReportCsv();
        assertEquals(sampleData.size(), response.size());
        assertTrue(response.containsAll(sampleData), "The response content should match the sample data");
    }

}
