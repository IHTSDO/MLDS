package ca.intelliware.ihtsdo.mlds.config;

import java.io.IOException;

import org.ihtsdo.otf.dao.s3.OfflineS3ClientImpl;
import org.ihtsdo.otf.dao.s3.S3Client;
import org.ihtsdo.otf.dao.s3.S3ClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

@Configuration
public class S3ClientConfiguration implements EnvironmentAware {

    private static final String ENV_S3_API = "s3.api.";
    private static final String PROP_OFFLINE_MODE = "offline-mode";

    private final Logger log = LoggerFactory.getLogger(S3ClientConfiguration.class);


    private Environment environment;

    public S3ClientConfiguration() {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public S3Client s3ClientImplementation() throws IOException {
        log.debug("Configuring S3 client");
        S3Client s3Client = null;
        Binder binder = Binder.get(environment);
        Boolean s3Offline = binder.bind(ENV_S3_API + PROP_OFFLINE_MODE, Boolean.class).orElse(true);
        log.info("Configuring " + (s3Offline ? "offline" : "online") + " s3 client.");
        if (s3Offline) {
            s3Client = new OfflineS3ClientImpl();
        } else {
            s3Client = new S3ClientImpl(software.amazon.awssdk.services.s3.S3Client.builder()
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .build());
            log.debug("s3Client:", s3Client);
        }

        log.debug("Done:Configuring S3 Client");

        return s3Client;
    }
}
