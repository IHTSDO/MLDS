package ca.intelliware.ihtsdo.mlds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		addLiquibaseScanPackages();
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
