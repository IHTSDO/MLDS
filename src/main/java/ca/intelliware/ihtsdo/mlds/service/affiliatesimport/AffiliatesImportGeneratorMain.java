package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import io.github.jhipster.loaded.JHipsterReloaderAutoConfiguration;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import ca.intelliware.ihtsdo.mlds.config.Constants;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportGenerator.GeneratorContext;

@ComponentScan
@EnableAutoConfiguration(exclude = {
		MetricFilterAutoConfiguration.class, 
		MetricRepositoryAutoConfiguration.class, 
		JHipsterReloaderAutoConfiguration.class // @see ReplacementJHipsterReloaderAutoConfiguration
		})
public class AffiliatesImportGeneratorMain {
    private final Logger log = LoggerFactory.getLogger(AffiliatesImportGeneratorMain.class);

    @Resource
    AffiliatesImportGenerator affiliatesImportGenerator;
    /**
     * Main method, used to run the application.
     *
     * To run the application with hot reload enabled, add the following arguments to your JVM:
     * "-javaagent:spring_loaded/springloaded-jhipster.jar -noverify -Dspringloaded=plugins=io.github.jhipster.loaded.instrument.JHipsterLoadtimeInstrumentationPlugin"
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AffiliatesImportGeneratorMain.class);
        app.setShowBanner(false);

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        addDefaultProfile(app, source);
        
        // Fallback to set the list of liquibase package list
        addLiquibaseScanPackages();

        app.run(args);
    }

    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }

    @PostConstruct
    public void initApplication() throws IOException {
    	File file = new File("output.csv");
    	log.info("Starting to export to: "+file.getAbsolutePath());
    	GeneratorContext context = new AffiliatesImportGenerator.GeneratorContext();
    	context.setRows(6000);
    	context.setBaseKey("AFFILIATE-A-");
    	String content = affiliatesImportGenerator.generateFile(context);
		FileUtils.write(file, content);
		log.info("Export complete");
    }
    
    private static void addLiquibaseScanPackages() {
        System.setProperty("liquibase.scan.packages", "liquibase.change" + "," + "liquibase.database" + "," +
                "liquibase.parser" + "," + "liquibase.precondition" + "," + "liquibase.datatype" + "," +
                "liquibase.serializer" + "," + "liquibase.sqlgenerator" + "," + "liquibase.executor" + "," +
                "liquibase.snapshot" + "," + "liquibase.logging" + "," + "liquibase.diff" + "," +
                "liquibase.structure" + "," + "liquibase.structurecompare" + "," + "liquibase.lockservice" + "," +
                "liquibase.ext" + "," + "liquibase.changelog");
    }


}
