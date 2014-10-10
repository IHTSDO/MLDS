package ca.intelliware.ihtsdo.mlds.domain.json;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReleaseFileJsonTest {
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	ReleaseFile releaseFile;
	ObjectMapper objectMapper;

	@Before
	public void configureJackson() {
		objectMapper = new ObjectMapperTestBuilder(null).buildObjectMapper();
	}
	
	@Before
	public void buildObjectGraph() {
		releaseFile = new ReleaseFile(55L);
		ReleasePackage releasePackage = new ReleasePackage(22L);
		ReleaseVersion releaseVersion = new ReleaseVersion(33L);
		
		releasePackage.addReleaseVersion(releaseVersion);
		releaseVersion.addReleaseFile(releaseFile);
	}
	
	@Test
	public void adminSeesRawDownloadUrl() throws Exception {
		securityContextSetup.asAdmin();
		releaseFile.setDownloadUrl("http://example.com/file.zip");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(releaseFile));
		
		String downloadUrl = tree.get("downloadUrl").asText();
		assertEquals("http://example.com/file.zip", downloadUrl);
	}
	
	@Test
	public void affiliateDoesNotSeeRawDownloadUrl() throws Exception {
		securityContextSetup.asAffiliateUser();
		releaseFile.setDownloadUrl("http://example.com/file.zip");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(releaseFile));
		
		assertNull("downloadUrl missing from tree", tree.get("downloadUrl"));
	}
	
	@Test
	public void affiliateDoesSeeProxiedDownloadUrl() throws Exception {
		securityContextSetup.asAffiliateUser();
		releaseFile.setDownloadUrl("http://example.com/file.zip");
		
		JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(releaseFile));
		
		assertThat("clientDownloadUrl contains releaseFileId", tree.get("clientDownloadUrl").asText(), containsString("releaseFiles/"+releaseFile.getReleaseFileId()));
	}
}
