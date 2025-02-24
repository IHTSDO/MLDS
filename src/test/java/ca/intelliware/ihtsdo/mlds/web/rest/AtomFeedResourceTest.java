package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.service.AtomFeedService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AtomFeedResourceTest {
    @Mock
    private ReleaseVersionRepository releaseVersionRepository;

    @InjectMocks
    private AtomFeedService atomFeedService;

    @Value("${atom.feed.baseUrl}")
    private String feedBaseUrl;

    @Value("${atom.feed.title}")
    private String feedTitle;

    @Value("${atom.feed.link}")
    private String feedLink;

    @Value("${atom.feed.UUID}")
    private String feedUUID;

    @Value("${atom.feed.generator}")
    private String feedGenerator;

    @Value("${atom.feed.profile}")
    private String feedProfile;

    public SimpleDateFormat dateFormat;

    @BeforeEach
    public void setup() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    @Test
    public void testGenerateAtomFeed() {
        List<Object[]> mockAtomFeedData = createMockAtomFeedData();
        when(releaseVersionRepository.listAtomFeed()).thenReturn(mockAtomFeedData);

        String atomFeedXml = atomFeedService.generateAtomFeed();

        assertAtomFeedContent(atomFeedXml);
    }

    private List<Object[]> createMockAtomFeedData() {
        List<Object[]> mockAtomFeedData = new ArrayList<>();
        mockAtomFeedData.add(new Object[]{
            "Title", "DownloadURL", "MemberOrgName", "MemberOrgURL",
            "ContactEmail", "Id", "Copyrights", "2023-08-01 12:00:00.0",
            "2023-07-31 12:00:00.0", "Summary", "ReleasePackageURI",
            "VersionURI", "VersionDependentURI", "VersionDependentDerivativeURI",
            "PackageId", "VersionId", "FileId", true, "Md5Hash", "FileSize",
            "PackageType"
        });
        return mockAtomFeedData;
    }

    private void assertAtomFeedContent(String atomFeedXml) {
        assertTrue(atomFeedXml.contains("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
        assertTrue(atomFeedXml.contains("<feed xmlns=\"http://www.w3.org/2005/Atom\""));
        assertTrue(atomFeedXml.contains("<title>" + feedTitle + "</title>"));
        assertTrue(atomFeedXml.contains("<id>urn:uuid:" + feedUUID + "</id>"));
        assertTrue(atomFeedXml.contains("<generator>" + feedGenerator + "</generator>"));
        assertTrue(atomFeedXml.contains("<title>Title</title>"));
        assertTrue(atomFeedXml.contains("<summary>Summary</summary>"));
        assertTrue(atomFeedXml.contains("<ncts:atomSyndicationFormatProfile>"));
    }

}
