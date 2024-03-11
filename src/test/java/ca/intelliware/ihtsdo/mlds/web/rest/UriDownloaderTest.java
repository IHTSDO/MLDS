package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import ca.intelliware.ihtsdo.mlds.config.PostgresTestContainerTest;
import jakarta.transaction.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.intelliware.ihtsdo.mlds.web.rest.UriDownloader.S3Location;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.application.properties")
@ActiveProfiles("dev")
@Transactional
public class UriDownloaderTest extends MySqlTestContainerTest {

	@Autowired
	UriDownloader downloader;

	@Test
	public void testDetermineS3Bucket() {
		String url = "s3://ire.published.release.ihtsdo/international/SnomedCT_Release_INT_20140131.zip";
		final String expectedBucket = "ire.published.release.ihtsdo";
		final String expectedFilePath = "international/SnomedCT_Release_INT_20140131.zip";

		S3Location s3Location = downloader.determineS3Location(url);
		Assert.assertEquals(expectedBucket, s3Location.getBucket());
		Assert.assertEquals(expectedFilePath, s3Location.getFilePath());

		url = "https://ire.published.release.ihtsdo.s3.amazonaws.com/international/SnomedCT_Release_INT_20140131.zip";
		s3Location = downloader.determineS3Location(url);
		Assert.assertEquals(expectedBucket, s3Location.getBucket());
		Assert.assertEquals(expectedFilePath, s3Location.getFilePath());
	}

}
