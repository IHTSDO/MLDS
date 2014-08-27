package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

public class ReleasePackagesResource_ReleaseFiles_Test {

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
	UriDownloader uriDownloader;

	ReleasePackagesResource releasePackagesResource;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        releasePackagesResource = new ReleasePackagesResource();
        
        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.releaseVersionRepository = releaseVersionRepository;
        releasePackagesResource.releaseFileRepository = releaseFileRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.releasePackageAuditEvents = releasePackageAuditEvents;
        releasePackagesResource.uriDownloader = uriDownloader;

        this.restReleasePackagesResource = MockMvcBuilders.standaloneSetup(releasePackagesResource).build();
    }

	@Test
	public void downloadReleaseFileShouldTriggerUriDownloader() throws Exception {
		ReleaseFile releaseFile = withReleaseFileWithIdOf(3L);
		releaseFile.setDownloadUrl("http://test.com/file");

		restReleasePackagesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_FILE_DOWNLOAD, 1L, 2L, 3L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(uriDownloader).download(Mockito.eq("http://test.com/file"), Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
	}

	private ReleaseFile withReleaseFileWithIdOf(long id) {
		ReleaseFile releaseFile = new ReleaseFile();
		Mockito.when(releaseFileRepository.findOne(id)).thenReturn(releaseFile);
		return releaseFile;
	}
	
	@Test
	public void downloadReleaseFileShouldLogToAudit() throws Exception {
		withReleaseFileWithIdOf(3L);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_FILE_DOWNLOAD, 1L, 2L, 3L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logDownload(Mockito.any(ReleaseFile.class), Mockito.anyInt());
	}
}
