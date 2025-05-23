package ca.intelliware.ihtsdo.mlds.web.rest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import java.util.Optional;

public class ReleaseVersionsResourceTest {

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

	@Mock
	UserNotifier userNotifier;

	ReleaseVersionsResource releaseVersionsResource;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		releaseVersionsResource = new ReleaseVersionsResource();

		releaseVersionsResource.releasePackageRepository = releasePackageRepository;
		releaseVersionsResource.releaseVersionRepository = releaseVersionRepository;
		releaseVersionsResource.authorizationChecker = authorizationChecker;
		releaseVersionsResource.currentSecurityContext = currentSecurityContext;
		releaseVersionsResource.releasePackageAuditEvents = releasePackageAuditEvents;
		releaseVersionsResource.userNotifier = userNotifier;

		this.restReleasePackagesResource = MockMvcBuilders.standaloneSetup(releaseVersionsResource).build();
	}

	@Test
	public void testCreateReleaseVersionSavesRecordAndAddsToParentReleasePackage() throws Exception {
		ReleasePackage releasePackage = withReleasePackageWithIdOf(1L);

		stubSaveReleaseVersion(2L);

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_VERSIONS, 1L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content("{ \"name\": \"name\", \"description\": \"description\", \"releaseType\": \"releaseType\"}")
						.accept(MediaType.APPLICATION_JSON_UTF8))
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

		Mockito.when(releasePackageRepository.findById(id)).thenReturn(Optional.of(releasePackage));
		Mockito.when(releasePackageRepository.getOne(id)).thenReturn(releasePackage);
		return releasePackage;
	}

	@Test
	public void testCreateReleaseVersionLogsAuditEvent() throws Exception {
		withReleasePackageWithIdOf(1L);
		stubSaveReleaseVersion(2L);

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_VERSIONS, 1L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content("{ \"name\": \"name\", \"description\": \"description\" }")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logCreationOf(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionUpdateFailsForUnknownId() throws Exception {
		withReleasePackageWithIdOf(1L);
		Mockito.when(releaseVersionRepository.findById(999L)).thenReturn(Optional.empty());

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_VERSION, 1L, 999L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content("{ \"releaseVersionId\": 999, \"name\": \"name\", \"description\": \"description\" }")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());

		Mockito.verify(releaseVersionRepository, Mockito.never()).save(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionUpdateShouldOnlyCopySubsetOfFields() throws Exception {
		ReleaseVersion releaseVersion = new ReleaseVersion();
		releaseVersion.setName("originalName");
		releaseVersion.setDescription("originalDescription");
		releaseVersion.setCreatedBy("originalCreatedBy");
		releaseVersion.setReleaseType("originalReleaseType");

		Mockito.when(releaseVersionRepository.findById(2L)).thenReturn(Optional.of(releaseVersion));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_VERSION, 1L, 2L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content("{ \"releaseVersionId\": 1, \"name\": \"newName\", \"description\": \"newDescription\", \"createdBy\": \"newCreatedBy\", \"createdBy\": \"newCreatedBy\", \"releaseType\": \"newReleaseType\" }")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());


		Assert.assertEquals("newName", releaseVersion.getName());
		Assert.assertEquals("newDescription", releaseVersion.getDescription());

		Assert.assertEquals("originalCreatedBy", releaseVersion.getCreatedBy());
		Assert.assertEquals("newReleaseType", releaseVersion.getReleaseType());
	}

	@Test
	public void testReleaseVersionNotification() throws Exception {
		ReleaseVersion releaseVersion = new ReleaseVersion();
		releaseVersion.setOnline(false);

		Mockito.when(releaseVersionRepository.findById(2L)).thenReturn(Optional.of(releaseVersion));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_VERSION_NOTIFICATIONS, 1L, 2L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		Mockito.verify(userNotifier, Mockito.times(1)).notifyReleasePackageUpdated(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionDeleteShouldFailForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion activeVersion = new ReleaseVersion(2L);
//     activeVersion.setOnline(true);
		activeVersion.setReleaseType("online");
		releasePackage.addReleaseVersion(activeVersion);

		Mockito.when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));
		Mockito.when(releaseVersionRepository.findById(2L)).thenReturn(Optional.of(activeVersion));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 1L, 2L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isConflict());

		Mockito.verify(releaseVersionRepository, Mockito.never()).delete(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionDeleteShouldSucceedForInactiveVersion() throws Exception {
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
//     inactiveVersion.setOnline(false);
		inactiveVersion.setReleaseType("offline");

		Mockito.when(releaseVersionRepository.findById(2L)).thenReturn(Optional.of(inactiveVersion));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 1L, 2L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		Mockito.verify(releaseVersionRepository).delete(Mockito.any(ReleaseVersion.class));
	}

	@Test
	public void testReleaseVersionDeleteLogsAuditEvent() throws Exception {
		ReleaseVersion releaseVersion = new ReleaseVersion();
		releaseVersion.setReleaseType("offline");

		Mockito.when(releaseVersionRepository.findById(1L)).thenReturn(Optional.of(releaseVersion));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_VERSION, 55L, 1L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logDeletionOf(Mockito.any(ReleaseVersion.class));
	}


    @Test
    public void testUpdateArchiveSuccess() {
        ReleasePackage releasePackage = new ReleasePackage();

        ReleaseVersion releaseVersion = new ReleaseVersion(2L);
        releasePackage.addReleaseVersion(releaseVersion);
        releasePackage.addReleaseVersion(releaseVersion);
        when(releaseVersionRepository.findById(1L)).thenReturn(Optional.of(releaseVersion));
        doNothing().when(authorizationChecker).checkCanEditReleasePackage(releasePackage);
        ResponseEntity<ReleaseVersion> response = releaseVersionsResource.updateArchive(1L, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isArchive());
        verify(releaseVersionRepository).findById(1L);
        verify(authorizationChecker).checkCanEditReleasePackage(releasePackage);
    }


    @Test
    public void testUpdateArchiveReleaseVersionNotFound() {
        ReleasePackage releasePackage = new ReleasePackage();

        ReleaseVersion releaseVersion = new ReleaseVersion(2L);
        releasePackage.addReleaseVersion(releaseVersion);
        releasePackage.addReleaseVersion(releaseVersion);
        when(releaseVersionRepository.findById(1L)).thenReturn(Optional.of(releaseVersion));
        doNothing().when(authorizationChecker).checkCanEditReleasePackage(releasePackage);
        when(releaseVersionRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<ReleaseVersion> response = releaseVersionsResource.updateArchive(1L, true);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(releaseVersionRepository).findById(1L);
        verify(authorizationChecker, never()).checkCanEditReleasePackage(any());
    }
}
