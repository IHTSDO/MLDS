package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.AssertThrows;
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

public class ReleasePackagesResource_ReleaseVersions_Test {

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
	ReleasePackageAuditEvents releasePackageAuditEvents;

	ReleasePackagesResource releasePackagesResource;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        releasePackagesResource = new ReleasePackagesResource();
        
        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.releaseVersionRepository = releaseVersionRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.releasePackageAuditEvents = releasePackageAuditEvents;

        this.restReleasePackagesResource = MockMvcBuilders.standaloneSetup(releasePackagesResource).build();
    }

	@Test
	public void testCreateReleaseVersionSavesRecordAndAddsToParentReleasePackage() throws Exception {
		ReleasePackage releasePackage = withReleasePackageWithIdOf(1L);

		stubSaveReleaseVersion(2L);

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_VERSIONS, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releaseVersionRepository).save(Mockito.any(ReleaseVersion.class));
		Assert.assertThat(releasePackage.getReleaseVersions(), Matchers.hasItem(new ReleaseVersion(2L)));
	}

	private void stubSaveReleaseVersion(final long newReleaseVersionId) {
		Mockito.when(releaseVersionRepository.save(Mockito.any(ReleaseVersion.class))).then(new Answer<ReleaseVersion>() {
			@Override
			public ReleaseVersion answer(InvocationOnMock invocation) throws Throwable {
				ReleaseVersion releaseVersion = (ReleaseVersion) invocation.getArguments()[0];
				releaseVersion.setReleaseVersionId(newReleaseVersionId);
				return releaseVersion;
			}
		});
	}

	private ReleasePackage withReleasePackageWithIdOf(long id) {
		ReleasePackage releasePackage = new ReleasePackage();
		
		Mockito.when(releasePackageRepository.findOne(id)).thenReturn(releasePackage);
		Mockito.when(releasePackageRepository.getOne(id)).thenReturn(releasePackage);
		return releasePackage;
	}

	@Test
	public void testCreateReleaseVersionLogsAuditEvent() throws Exception {
		withReleasePackageWithIdOf(1L);
		stubSaveReleaseVersion(2L);

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_VERSIONS, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageAuditEvents).logCreationOf(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionUpdateFailsForUnknownId() throws Exception {
		withReleasePackageWithIdOf(1L);
		Mockito.when(releaseVersionRepository.findOne(999L)).thenReturn(null);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_VERSION, 1L, 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releaseVersionId\": 999, \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
		
		Mockito.verify(releaseVersionRepository, Mockito.never()).save(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionUpdateShouldOnlyCopySubsetOfFields() throws Exception {
		ReleaseVersion releaseVersion = new ReleaseVersion();
		releaseVersion.setName("originalName");
		releaseVersion.setDescription("originalDescription");
		releaseVersion.setCreatedBy("originalCreatedBy");
		
		Mockito.when(releaseVersionRepository.findOne(1L)).thenReturn(releaseVersion);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_VERSION, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releaseVersionId\": 1, \"name\": \"newName\", \"description\": \"newDescription\", \"createdBy\": \"newCreatedBy\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		ArgumentCaptor<ReleaseVersion> savedReleaseVersion = ArgumentCaptor.forClass(ReleaseVersion.class);
		Mockito.verify(releaseVersionRepository).save(savedReleaseVersion.capture());
		
		Assert.assertEquals("newName", savedReleaseVersion.getValue().getName());
		Assert.assertEquals("newDescription", savedReleaseVersion.getValue().getDescription());
		
		Assert.assertEquals("originalCreatedBy", savedReleaseVersion.getValue().getCreatedBy());
	}

	@Test
	public void testReleaseVersionDeleteShouldFailForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion activeVersion = new ReleaseVersion(2L);
		activeVersion.setOnline(true);
		releasePackage.addReleaseVersion(activeVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		Mockito.when(releaseVersionRepository.findOne(2L)).thenReturn(activeVersion);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 1L, 2L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
		
		Mockito.verify(releaseVersionRepository, Mockito.never()).delete(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionDeleteShouldSucceedForInactiveVersion() throws Exception {
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
		inactiveVersion.setOnline(false);
		
		Mockito.when(releaseVersionRepository.findOne(2L)).thenReturn(inactiveVersion);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 1L, 2L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releaseVersionRepository).delete(Mockito.any(ReleaseVersion.class));
	}
	
	@Test
	public void testReleaseVersionDeleteLogsAuditEvent() throws Exception {
		ReleaseVersion releaseVersion = new ReleaseVersion();
		
		Mockito.when(releaseVersionRepository.findOne(1L)).thenReturn(releaseVersion);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 55L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logReleaseVersionDeleted(Mockito.any(ReleaseVersion.class));
	}
}
