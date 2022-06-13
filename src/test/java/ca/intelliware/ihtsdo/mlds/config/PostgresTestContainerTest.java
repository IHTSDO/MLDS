package ca.intelliware.ihtsdo.mlds.config;

import org.junit.BeforeClass;

/**
 * Abstract class for unit tests to extend if an MLDS Postgres database in Docker (testcontainer) is wanted.
 */
public abstract class PostgresTestContainerTest {

    @BeforeClass
    public static void dataSourceConfiguration() {
        PostgresTestContainerConfig.INSTANCE.getInstance().setupDataSource();
    }

}
