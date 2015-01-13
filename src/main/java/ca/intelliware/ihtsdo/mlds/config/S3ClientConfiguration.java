package ca.intelliware.ihtsdo.mlds.config;

import java.io.IOException;

import org.ihtsdo.otf.dao.s3.OfflineS3ClientImpl;
import org.ihtsdo.otf.dao.s3.S3Client;
import org.ihtsdo.otf.dao.s3.S3ClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.amazonaws.auth.BasicAWSCredentials;

@Configuration
public class S3ClientConfiguration implements EnvironmentAware {

	private static final String ENV_S3_API = "s3.api.";
	private static final String PROP_OFFLINE_MODE = "offlineMode";
	private static final String PROP_AWS_KEY = "default.aws.key";
	private static final String PROP_AWS_PRIVATEKEY = "default.aws.privateKey";

	private final Logger log = LoggerFactory.getLogger(S3ClientConfiguration.class);

	private RelaxedPropertyResolver propertyResolver;

	public S3ClientConfiguration() {
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_S3_API);
	}

	@Bean
	public S3Client s3Client() throws IOException {
		log.debug("Configuring S3 client");
		S3Client s3Client = null;
		Boolean s3Offline = propertyResolver.getProperty(PROP_OFFLINE_MODE, Boolean.class, true);
		log.info("Configuring " + (s3Offline ? "offline" : "online") + " s3 client.");
		if (s3Offline) {
			s3Client = new OfflineS3ClientImpl();
		} else {
			String awsKey = propertyResolver.getProperty(PROP_AWS_KEY);
			String awsPrivateKey = propertyResolver.getProperty(PROP_AWS_PRIVATEKEY);
			BasicAWSCredentials credentials = new BasicAWSCredentials(awsKey, awsPrivateKey);
			s3Client = new S3ClientImpl(credentials);
		}

		log.debug("Done:Configuring S3 Client");

		return s3Client;
	}

}
