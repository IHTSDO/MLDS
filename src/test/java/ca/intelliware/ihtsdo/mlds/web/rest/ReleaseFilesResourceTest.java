package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

import java.io.FileNotFoundException;
import java.util.Optional;

public class ReleaseFilesResourceTest {

    private MockMvc restReleaseFilesResource;

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
	UriDownloader uriDownloader;

	@Mock
	UserMembershipAccessor userMembershipAccessor;

	ReleaseFilesResource releaseFilesResource;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        releaseFilesResource = new ReleaseFilesResource();

        releaseFilesResource.releaseVersionRepository = releaseVersionRepository;
        releaseFilesResource.releaseFileRepository = releaseFileRepository;
        releaseFilesResource.authorizationChecker = authorizationChecker;
        releaseFilesResource.releasePackageAuditEvents = releasePackageAuditEvents;
        releaseFilesResource.uriDownloader = uriDownloader;
        releaseFilesResource.userMembershipAccessor = userMembershipAccessor;

        this.restReleaseFilesResource = MockMvcBuilders.standaloneSetup(releaseFilesResource).build();
    }

	@Test
	public void downloadReleaseFileShouldTriggerUriDownloader() throws Exception {
		ReleaseFile releaseFile = withReleaseFileWithIdOf(3L);
		releaseFile.setDownloadUrl("http://test.com/file");

		restReleaseFilesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_FILE_DOWNLOAD, 1L, 2L, 3L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		Mockito.verify(uriDownloader).download(Mockito.eq("http://test.com/file"), Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
	}

	private ReleaseFile withReleaseFileWithIdOf(long id) {
		ReleaseFile releaseFile = new ReleaseFile();
		Mockito.when(releaseFileRepository.findById(id)).thenReturn(Optional.of(releaseFile));
		return releaseFile;
	}

	@Test
	public void downloadReleaseFileShouldLogToAudit() throws Exception {
		withReleaseFileWithIdOf(3L);

		restReleaseFilesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_FILE_DOWNLOAD, 1L, 2L, 3L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logDownload(Mockito.any(ReleaseFile.class), Mockito.anyInt(), Mockito.any());
	}

	@Test
	public void downloadReleaseFileShouldFailIfCheckDenied() throws Exception {
		ReleaseFile releaseFile = withReleaseFileWithIdOf(3L);
		releaseFile.setDownloadUrl("http://test.com/file");

		Mockito.doThrow(new IllegalStateException("ACCOUNT DEACTIVATED")).when(authorizationChecker).checkCanDownloadReleaseVersion(Mockito.any(ReleaseVersion.class));

		try {
			restReleaseFilesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_FILE_DOWNLOAD, 1L, 2L, 3L)
					.contentType(MediaType.APPLICATION_JSON)
	                .accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk());
        } catch (Exception e) {
        	Assert.assertThat(e.getMessage(), Matchers.containsString("ACCOUNT DEACTIVATED"));
        }
	}

    @Test
    public void testCheckIhtsdoFile() throws FileNotFoundException {
        long releaseFileId = 1L;
        ReleaseFile mockReleaseFile = new ReleaseFile();
        mockReleaseFile.setDownloadUrl("https://ire.published.release.ihtsdo.s3.amazonaws.com/international/SnomedCT_Release_INT_20140131.zip");
        Mockito.when(releaseFileRepository.findById(releaseFileId))
            .thenReturn(Optional.of(mockReleaseFile));
        Boolean result = releaseFilesResource.checkIhtsdoFile(1L, 1L, releaseFileId, null, null);
        Assert.assertTrue("The download URL should contain 'ihtsdo'", result);
    }

}
