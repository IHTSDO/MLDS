package ca.intelliware.ihtsdo.mlds.config;

import org.junit.BeforeClass;

public abstract class MySqlTestContainerTest {
    @BeforeClass
    public static void dataSourceConfiguration() {
        MySqlTestContainerConfig.INSTANCE.getInstance().setupDataSource();
    }

}
