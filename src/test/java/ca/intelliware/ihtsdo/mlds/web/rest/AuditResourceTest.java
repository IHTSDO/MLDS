package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AuditEventRequestDTO;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleaseFileCountDTO;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
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

    @Mock
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    private MockMvc restUserMockMvc;

    SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    @InjectMocks
    private AuditResource auditEventController;

    private LocalDate startDate;
    private LocalDate endDate;
    private Instant startInstant;
    private Instant endInstant;
    private Set<String> expectedUserSet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        AuditResource auditResource = new AuditResource();

        auditResource.auditEventService = auditEventService;

        securityContextSetup.asAdmin();
        startDate = LocalDate.of(2023, 10, 1);
        endDate = LocalDate.of(2023, 10, 31);
        startInstant = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        endInstant = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        expectedUserSet = new HashSet<>();
        expectedUserSet.add("user1");
        expectedUserSet.add("user2");

		this.restUserMockMvc = MockMvcBuilders
				.standaloneSetup(auditResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
    }


	@Test
    public void findByFilterShouldFailForUnknownFilter() throws Exception {
        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "unknownField eq 999")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

	@Test
    public void findByFilterShouldProvideAccessToAllAuditsWhenNoFilterProvided() throws Exception {
		when(auditEventService.findAll()).thenReturn(Arrays.asList(createAuditEvent("TypeA"), createAuditEvent("TypeB")));

        restUserMockMvc.perform(get(Routes.AUDITS)
                .accept(MediaType.APPLICATION_JSON_UTF8))
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
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
            .andDo(print())
                .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")))
                ;
    }

	@Test
    public void findByFilterShouldAllowFilteringByDateRange() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Instant fromDate = dateFormat.parse("2014-01-02").toInstant();
        Instant toDate = dateFormat.parse("2014-01-10").toInstant();
		when(auditEventService.findByDates(fromDate, toDate)).thenReturn(Arrays.asList(createAuditEvent("TypeA")));

        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "auditEventDate ge '2014-01-02' and auditEventDate le '2014-01-10'")
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

            .andDo(print())
            .andExpect(jsonPath("$[0].type", Matchers.is("TypeA")));

    }

	@Test
    public void findByFilterShouldFailForUnparseableDateRange() throws Exception {
		try {
	        restUserMockMvc.perform(get(Routes.AUDITS)
	        		.param("$filter", "auditEventDate ge '2014-01-02' and auditEventDate le 'NOT A DATE'")
	                .accept(MediaType.APPLICATION_JSON_UTF8))
	                .andExpect(status().is4xxClientError())
	                ;
		} catch (NestedServletException e) {
			assertEquals("Invalid format: \"NOT A DATE\"", e.getCause().getMessage());
		}

    }

	@Test
    public void findByFilterShouldAllowFilteringByAffiliate() throws Exception {
		when(auditEventService.findByAffiliateId(123L)).thenReturn(Arrays.asList(createAuditEvent("TypeA")));

        restUserMockMvc.perform(get(Routes.AUDITS)
        		.param("$filter", "affiliateId eq '123'")
                .accept(MediaType.APPLICATION_JSON_UTF8))
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

    @Test
    public void findReleaseFileDownloadAuditData_ShouldReturnCorrectCounts() {

        AuditEventRequestDTO request = createAuditEventRequestDTO();
        request.setExcludeAdminAndStaff(false);

        Instant[] dateRange = new Instant[]{startInstant, endInstant};
        when(auditEventService.getStartEndInstant(request)).thenReturn(dateRange);

        List<PersistentAuditEvent> mockEvents = Arrays.asList(
            createPersistentAuditEvent("user1", "FileA", "Version1", "PackageX"),
            createPersistentAuditEvent("user2", "FileA", "Version1", "PackageX"),
            createPersistentAuditEvent("user3", "FileB", "Version2", "PackageY")
        );

        when(auditEventService.getAuditEvents(false, startInstant, endInstant)).thenReturn(mockEvents);
        when(auditEventService.filterDownloadEvents(mockEvents)).thenReturn(mockEvents);

        List<ReleaseFileCountDTO> response = auditEventController.findReleaseFileDownloadAuditData(request);
        assertEquals(2, response.size());

        assertAuditResponse(response, "FileA", "Version1", 2);
        assertAuditResponse(response, "FileB", "Version2", 1);
    }


    @Test
    public void findReleaseFileDownloadDataForCsv_ShouldReturnFilteredEvents() {

        AuditEventRequestDTO request = createAuditEventRequestDTO();
        request.setExcludeAdminAndStaff(true);

        Instant[] dateRange = new Instant[]{startInstant, endInstant};
        when(auditEventService.getStartEndInstant(request)).thenReturn(dateRange);

        List<PersistentAuditEvent> mockEvents = Arrays.asList(
            createPersistentAuditEvent("user1", "FileA", "Version1", "PackageX"),
            createPersistentAuditEvent("user2", "FileB", "Version2", "PackageY")
        );

        when(auditEventService.getAuditEvents(true, startInstant, endInstant)).thenReturn(mockEvents);
        when(auditEventService.filterDownloadEvents(mockEvents)).thenReturn(mockEvents);

        List<PersistentAuditEvent> response = auditEventController.findReleaseFileDownloadDataForCsv(request);

        assertEquals(2, response.size());
        assertEquals("user1", response.get(0).getPrincipal());
        assertEquals("user2", response.get(1).getPrincipal());
    }



    private AuditEvent createAuditEvent(String type) {
        return new AuditEvent("testUser", type, "value1");
    }


    private AuditEventRequestDTO createAuditEventRequestDTO() {
        AuditEventRequestDTO request = new AuditEventRequestDTO();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return request;
    }

    private PersistentAuditEvent createPersistentAuditEvent(String principal, String fileLabel, String versionName, String packageName) {
        PersistentAuditEvent event = new PersistentAuditEvent();
        event.setPrincipal(principal);
        event.setAuditEventType("RELEASE_FILE_DOWNLOADED");
        event.setAuditEventDate(startInstant);

        Map<String, String> data = new HashMap<>();
        data.put("releaseFile.label", fileLabel);
        data.put("releaseVersion.name", versionName);
        data.put("releasePackage.name", packageName);
        event.setData(data);

        return event;
    }

    private void assertAuditResponse(List<ReleaseFileCountDTO> response, String expectedFileName, String expectedVersionName, int expectedCount) {
        assertTrue(response.stream().anyMatch(dto ->
            dto.getReleaseFileName().equals(expectedFileName) &&
                dto.getReleaseVersionName().equals(expectedVersionName) &&
                dto.getCount() == expectedCount));
    }


}
