package ca.intelliware.ihtsdo.mlds.design;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

public class LiqubaseVerification {
	@Test
	public void verifyThatWeAreNotUsingTheDefaultJHGeneratedMigration() throws Exception {
		boolean jhipsterGeneratedChangelogExists = new File("src/main/resources/config/liquibase/changelog/db-changelog-002.xml").exists();
		assertFalse("we don't use the auto-generated migrations, except as a starting point for editing",jhipsterGeneratedChangelogExists);
	}
}
