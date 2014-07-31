package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class ReleasePackagesResourceTest {

    private MockMvc restReleasePackagesResource;

	@Mock
	ReleasePackageRepository releasePackageRepository;

	@Mock
	ReleaseVersionRepository releaseVersionRepository;

	@Mock
	ReleaseFileRepository releaseFileRepository;

	@Mock
	ReleasePackageAuthorizationChecker authorizationChecker;
	
	@Mock
	CurrentSecurityContext currentSecurityContext;

	@Mock
	ReleasePackageAuditEvents releasePackageAuditEvents;

	ReleasePackagesResource releasePackagesResource;

	@Before
    public void setup() {
        releasePackagesResource = new ReleasePackagesResource();
        
        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.releasePackageAuditEvents = releasePackageAuditEvents;
        
        Mockito.stub(authorizationChecker.getMemberAssociatedWithUser()).toReturn(new Member("IHTSDO"));

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
		
		Mockito.verify(releasePackageAuditEvents).logCreationOf(Mockito.any(ReleasePackage.class));
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
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
		
		Mockito.verify(releasePackageRepository, Mockito.never()).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteShouldSucceedForInactiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
		inactiveVersion.setOnline(false);
		releasePackage.addReleaseVersion(inactiveVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteLogsAuditEvent() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logDeletionOf(Mockito.any(ReleasePackage.class));
	}

}
