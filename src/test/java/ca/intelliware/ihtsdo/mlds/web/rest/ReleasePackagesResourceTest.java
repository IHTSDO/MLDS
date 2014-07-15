package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
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

	@Test
	public void testReleasePackageUpdateFailsForUnknownId() throws Exception {
		Mockito.when(releasePackageRepository.findOne(999L)).thenReturn(null);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 999, \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
		
		Mockito.verify(releasePackageRepository, Mockito.never()).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldSave() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldOnlyCopySubsetOfFields() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		releasePackage.setName("originalName");
		releasePackage.setDescription("originalDescription");
		releasePackage.setCreatedBy("originalCreatedBy");
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\", \"createdBy\": \"newCreatedBy\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		ArgumentCaptor<ReleasePackage> savedReleasePackage = ArgumentCaptor.forClass(ReleasePackage.class);
		Mockito.verify(releasePackageRepository).save(savedReleasePackage.capture());
		
		Assert.assertEquals("newName", savedReleasePackage.getValue().getName());
		Assert.assertEquals("newDescription", savedReleasePackage.getValue().getDescription());
		
		Assert.assertEquals("originalCreatedBy", savedReleasePackage.getValue().getCreatedBy());
	}

	@Test
	public void testReleasePackageDeleteShouldFailForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion activeVersion = new ReleaseVersion(2L);
		activeVersion.setOnline(true);
		releasePackage.addReleaseVersion(activeVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
		
		Mockito.verify(releasePackageRepository, Mockito.never()).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteShouldSucceedForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
		inactiveVersion.setOnline(false);
		releasePackage.addReleaseVersion(inactiveVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).delete(Mockito.any(ReleasePackage.class));
	}
}
