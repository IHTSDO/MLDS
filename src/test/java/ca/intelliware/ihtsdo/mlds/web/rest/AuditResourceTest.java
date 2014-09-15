package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

public class AuditResourceTest {

    @Mock
    private AuditEventService auditEventService;

    
    private MockMvc restUserMockMvc;
    
    SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        AuditResource auditResource = new AuditResource();
        
        auditResource.auditEventService = auditEventService;

        securityContextSetup.asAdmin();
		
		this.restUserMockMvc = MockMvcBuilders
				.standaloneSetup(auditResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
    }

    
	@Test
    public void findByFilterShouldFailForUnknownFilter() throws Exception {
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "unknownField eq 999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

	@Test
    public void findByFilterShouldProvideAccessToAllAuditsWhenNoFilterProvided() throws Exception {
		when(auditEventService.findAll()).thenReturn(Arrays.asList(createAuditEvent("TypeA"), createAuditEvent("TypeB")));
		
        restUserMockMvc.perform(get(Routes.AUDITS)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                .andExpect(jsonPath("$[1].type", Matchers.is("TypeB")))
                ;
    }

	@Test
    public void findByFilterShouldAllowFilteringByAuditType() throws Exception {
		when(auditEventService.findByAuditEventType("TypeA")).thenReturn(Arrays.asList(createAuditEvent("TypeA")));
		
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "auditEventType eq 'TypeA'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                ;
    }

	@Test
    public void findByFilterShouldAllowFilteringByDateRange() throws Exception {
		when(auditEventService.findByDates(Instant.parse("2014-01-02"), Instant.parse("2014-01-10"))).thenReturn(Arrays.asList(createAuditEvent("TypeA")));
		
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "auditEventDate ge '2014-01-02' and auditEventDate le '2014-01-10'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                ;
    }

	@Test
    public void findByFilterShouldFailForUnparseableDateRange() throws Exception {
		try {
	        restUserMockMvc.perform(get(Routes.AUDITS)
	        		.param("$filter", "auditEventDate ge '2014-01-02' and auditEventDate le 'NOT A DATE'")
	                .accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().is5xxServerError())
	                ;
	        Assert.fail();
		} catch (NestedServletException e) {
			Assert.assertEquals("Invalid format: \"NOT A DATE\"", e.getCause().getMessage());
		}
		
    }

	@Test
    public void findByFilterShouldAllowFilteringByAffiliate() throws Exception {
		when(auditEventService.findByAffiliateId(123L)).thenReturn(Arrays.asList(createAuditEvent("TypeA")));
		
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "affiliateId eq '123'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                ;
    }

	@Test
    public void findByFilterShouldAllowFilteringByApplication() throws Exception {
		when(auditEventService.findByApplicationId(123L)).thenReturn(Arrays.asList(createAuditEvent("TypeA")));
		
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "applicationId eq '123'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                ;
    }

	private AuditEvent createAuditEvent(String type) {
		return new AuditEvent("testUser", type, "value1");
	}
}
