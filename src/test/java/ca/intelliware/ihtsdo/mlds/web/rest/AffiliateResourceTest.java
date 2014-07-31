package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class AffiliateResourceTest {

    @Mock
    private ApplicationAuthorizationChecker applicationAuthorizationChecker;

    @Mock
    private AffiliateRepository affiliateRepository;
    
    @Mock
    private AffiliateDetailsRepository affiliateDetailsRepository;
    
    @Mock
    private SessionService sessionService;
    
    private MockMvc restUserMockMvc;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        AffiliateResource affiliateResource = new AffiliateResource();
        
        affiliateResource.affiliateDetailsRepository = affiliateDetailsRepository;
        affiliateResource.affiliateRepository = affiliateRepository;
        affiliateResource.applicationAuthorizationChecker = applicationAuthorizationChecker;

        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(affiliateResource).build();
    }
    
    @Test
    public void updateAffiliateDetailShouldFailForUnknownApplication() throws Exception {
        when(affiliateRepository.findOne(999L)).thenReturn(null);

        restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 999L)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"firstName\": \"Updated FirstName\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateAffiliateDetailShouldFailWhenNoDetailsSetOnAffiliateYet() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setAffiliateDetails(null);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);

        restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"firstName\": \"Updated FirstName\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateAffiliateDetailShouldFailWhenApplicationNotApproved() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.getApplication().setApprovalState(ApprovalState.REJECTED);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);

        restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"firstName\": \"Updated FirstName\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateAffiliateDetailShouldUpdateSaveWithSafeFields() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.getAffiliateDetails().setFirstName("Original FirstName");
    	affiliate.getAffiliateDetails().getAddress().setStreet("Original Street");
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"firstName\": \"Updated FirstName\", \"address\": { \"street\":\"Updated Street\" }, \"billingAddress\": {} }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Updated FirstName"))
                .andExpect(jsonPath("$.address.street").value("Updated Street"))
                ;
    	
    	Mockito.verify(affiliateDetailsRepository).save(Mockito.any(AffiliateDetails.class));
    }

    @Test
    public void updateAffiliateDetailShouldIgnoreOrganizationFieldUpdates() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.getAffiliateDetails().setOrganizationName("Original OrganizationName");
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"organizationName\": \"Updated OrganizationName\", \"address\": {}, \"billingAddress\": {} }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.organizationName").value("Original OrganizationName"))
                ;
    }

    @Test
    public void updateAffiliateDetailShouldIgnoreAddressCountryFieldUpdates() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.getAffiliateDetails().getAddress().setCountry(createCountry("CA"));
    	affiliate.getAffiliateDetails().getBillingAddress().setCountry(createCountry("CA"));
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"address\": { \"country\": {\"isoCode2\":\"DK\"}}, \"billingAddress\": {\"country\": {\"isoCode2\":\"DK\"}} }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address.country.isoCode2").value("CA")) /* Original address country preserved */
                .andExpect(jsonPath("$.billingAddress.country.isoCode2").value("DK")) /* Updated billing country */
                ;
    }

    private Country createCountry(String code) {
    	return new Country(code, code, code);
	}

	private Affiliate createBlankAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	
    	AffiliateDetails affiliateDetails = new AffiliateDetails();
    	affiliateDetails.setAddress(new MailingAddress());
    	affiliateDetails.setBillingAddress(new MailingAddress());
    	affiliate.setAffiliateDetails(affiliateDetails);
    	
    	PrimaryApplication application = new PrimaryApplication(1L);
    	application.setApprovalState(ApprovalState.APPROVED);
    	affiliate.setApplication(application);
    	
    	return affiliate;
    }
}
