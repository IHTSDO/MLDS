package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.Application;
import ca.intelliware.ihtsdo.mlds.web.rest.UriDownloader.S3Location;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class UriDownloaderTest {

	@Inject
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
