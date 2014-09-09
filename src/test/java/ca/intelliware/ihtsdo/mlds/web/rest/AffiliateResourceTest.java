package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliateImportAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesExporterService;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportGenerator;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportSpec;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class AffiliateResourceTest {

    @Mock
    private ApplicationAuthorizationChecker applicationAuthorizationChecker;

    @Mock
    private AffiliateRepository affiliateRepository;
    
    @Mock
    private AffiliateDetailsRepository affiliateDetailsRepository;
    
    @Mock
    private AffiliateAuditEvents affiliateAuditEvents;

    @Mock
    private AffiliateImportAuditEvents affiliateImportAuditEvents;

    @Mock
    private AffiliatesExporterService affiliatesExporterService;

    @Mock
    private AffiliatesImportGenerator affiliatesImportGenerator;

    @Mock
    private SessionService sessionService;
    
    @Mock
    private UserRepository userRepository;
    
    private MockMvc restUserMockMvc;
    
    SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        AffiliateResource affiliateResource = new AffiliateResource();
        
        affiliateResource.affiliateDetailsRepository = affiliateDetailsRepository;
        affiliateResource.affiliateRepository = affiliateRepository;
        affiliateResource.applicationAuthorizationChecker = applicationAuthorizationChecker;
        affiliateResource.affiliateAuditEvents = affiliateAuditEvents;
        affiliateResource.affiliatesExporterService = affiliatesExporterService;
        affiliateResource.affiliatesImportGenerator = affiliatesImportGenerator;
        affiliateResource.userRepository = userRepository;
        affiliateResource.sessionService = sessionService;
        affiliateResource.affiliateImportAuditEvents = affiliateImportAuditEvents;
        affiliateResource.currentSecurityContext = new CurrentSecurityContext();

        securityContextSetup.asAdmin();
		
		this.restUserMockMvc = MockMvcBuilders
				.standaloneSetup(affiliateResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
    }

    
	@Test
    public void updateAffiliateDetailShouldFailForUnknownAffiliate() throws Exception {
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
    public void updateAffiliateDetailShouldUpdateUserName() throws Exception {
    	User user = new User();
    	user.setLogin("Original Login");
    	user.setFirstName("Original FirstName");
    	user.setLastName("Original LastName");
    	when(userRepository.findOne("user@email.com")).thenReturn(user);
    	
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.getAffiliateDetails().setEmail("user@email.com");
		when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE_DETAIL, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"email\":\"ignore@email.com\", \"firstName\": \"Updated FirstName\", \"lastName\": \"Updated LastName\", \"address\": { \"street\":\"Updated Street\" }, \"billingAddress\": {} }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                ;
    	
		ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
    	Mockito.verify(userRepository).save(savedUser.capture());
    	
    	Assert.assertEquals("Updated FirstName", savedUser.getValue().getFirstName());
    	Assert.assertEquals("Updated LastName", savedUser.getValue().getLastName());
    	
    	Assert.assertEquals("Original Login", savedUser.getValue().getLogin());
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
    
    @Test
    public void getAffiliatesImportSpec() throws Exception {
    	AffiliatesImportSpec spec = new AffiliatesImportSpec();
    	spec.setExample("Example File Content");
		when(affiliatesExporterService.exportSpec()).thenReturn(spec);
    	
    	restUserMockMvc.perform(get(Routes.AFFILIATES_CSV_SPEC)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.example").value("Example File Content"))
                ;
    }

    @Test
    public void exportAffiliatesShouldExportAllAffiliatesInCSVFormat() throws Exception {
		when(affiliatesExporterService.exportToCSV()).thenReturn("affiliates file content");
    	
    	restUserMockMvc.perform(get(Routes.AFFILIATES_CSV)
                .accept("application/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/csv;charset=UTF-8"))
                .andExpect(content().string(Matchers.equalTo("affiliates file content")))
                ;
    }

    @Test
    public void exportAffiliatesShouldGenerateSpecifiedNumberOfAffiliatesInCSVFormat() throws Exception {
		when(affiliatesImportGenerator.generateFile(Mockito.eq(10))).thenReturn("generated affiliates file content");
    	
    	restUserMockMvc.perform(get(Routes.AFFILIATES_CSV)
    			.param("generate", "10")
                .accept("application/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/csv;charset=UTF-8"))
                .andExpect(content().string(Matchers.equalTo("generated affiliates file content")))
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
	
	@Test
	public void updateAffiliateShouldFailForUnknownAffiliate() throws Exception {
        when(affiliateRepository.findOne(999L)).thenReturn(null);

        restUserMockMvc.perform(put(Routes.AFFILIATE, 999L)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"notesInternal\": \"Updated notes\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
	}

	@Test
	public void updateAffiliateShouldFailIfCurrentUserCannotManage() throws Exception {
        when(affiliateRepository.findOne(999L)).thenReturn(createBlankAffiliate());
        Mockito.doThrow(new IllegalStateException("not allowed")).when(applicationAuthorizationChecker).checkCanManageAffiliate(Mockito.any(Affiliate.class));

        try {
        	restUserMockMvc.perform(put(Routes.AFFILIATE, 999L)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"notesInternal\": \"Updated notes\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        	Assert.fail();
        } catch (NestedServletException e) {
        	Assert.assertThat(e.getRootCause().getMessage(), Matchers.containsString("not allowed"));
        }
	}

	@Test
	public void updateAffiliateShouldUpdateSaveWithNotesField() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setNotesInternal("Original Notes");
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"notesInternal\": \"Updated Notes\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.notesInternal").value("Updated Notes"))
                ;
    	
    	Mockito.verify(affiliateRepository).save(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldUpdateSaveWithStandingStateField() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"standingState\": \"DEACTIVATED\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.standingState").value("DEACTIVATED"))
                ;
    	
    	Mockito.verify(affiliateRepository).save(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldIgnoreBlankStandingState() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"standingState\": null }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.standingState").value("IN_GOOD_STANDING"))
                ;
    	
    	Mockito.verify(affiliateRepository).save(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldFailWhenChangingStandingStateFromApplying() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setStandingState(StandingState.APPLYING);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"standingState\": \"IN_GOOD_STANDING\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                ;
	}

	@Test
	public void updateAffiliateShouldAuditLogWithAffiliateUpdate() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"notesInternal\": \"Updated Notes\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                ;
    	
    	Mockito.verify(affiliateAuditEvents).logUpdateOfAffiliate(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldAuditLogWithStandingStateChangeWhenChanged() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"standingState\": \"DEACTIVATED\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                ;
    	
    	Mockito.verify(affiliateAuditEvents).logStandingStateChange(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldSkipStandingStateAuditLogWhenUnchanged() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setStandingState(StandingState.APPLYING);
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"standingState\": \"APPLYING\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                ;
    	
    	Mockito.verify(affiliateAuditEvents, Mockito.never()).logStandingStateChange(Mockito.any(Affiliate.class));
	}

	@Test
	public void updateAffiliateShouldIgnoreNonSafeFields() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("original@email.com");
    	when(affiliateRepository.findOne(1L)).thenReturn(affiliate);
    	
    	restUserMockMvc.perform(put(Routes.AFFILIATE, 1L)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content("{ \"creator\": \"updated@email.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.creator").value("original@email.com"))
                ;
    	
    	Mockito.verify(affiliateRepository).save(Mockito.any(Affiliate.class));
	}
}
