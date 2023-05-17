package ca.intelliware.ihtsdo.mlds.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.MySQLContainer;

public enum MySqlTestContainerConfig {
    INSTANCE();

    private MySQLContainer<?> mySQLContainer;
    private MysqlDataSource dataSource;

    MySqlTestContainerConfig() {
        this.mySQLContainer = new MySQLContainer<>("mysql:8.0.27")
            .withDatabaseName("mldsInDockerTestContainer")
            .withUsername("abe")
            .withPassword("sapien")
            .withExposedPorts(3306);

        this.mySQLContainer.start();
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUrl(mySQLContainer.getJdbcUrl());
        this.dataSource.setUser(mySQLContainer.getUsername());
        this.dataSource.setPassword(mySQLContainer.getPassword());
    }

    public static MySqlTestContainerConfig getInstance() {
        return INSTANCE;
    }

    public void setupDataSource() {
        DatabaseConfiguration.setDataSource(dataSource);
    }
}
