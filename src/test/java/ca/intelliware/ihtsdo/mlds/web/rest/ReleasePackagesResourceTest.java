package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

public class ReleasePackagesResourceTest {

    private MockMvc restReleasePackagesResource;

	@Mock
	ReleasePackageRepository releasePackageRepository;

	@Mock
	ReleaseVersionRepository releaseVersionRepository;

	@Mock
	ReleaseFileRepository releaseFileRepository;

	@Mock
	AuthorizationChecker authorizationChecker;
	
	@Mock
	CurrentSecurityContext currentSecurityContext;

	@Mock
	AuditEventService auditEventService;

	ReleasePackagesResource releasePackagesResource;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        releasePackagesResource = new ReleasePackagesResource();
        
        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.auditEventService = auditEventService;

        this.restReleasePackagesResource = MockMvcBuilders.standaloneSetup(releasePackagesResource).build();
    }

	@Test
	public void testReleasePackageCreateSavesRecord() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageLogsAuditEvent() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("RELEASE_PACKAGE_CREATED"),Mockito.anyMap());
	}

}
