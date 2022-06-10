package ca.intelliware.ihtsdo.mlds.config;

import com.zaxxer.hikari.HikariDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Unit test specific configuration setup for Postgres through Docker testcontainers.
 */
public enum PostgresTestContainerConfig {

    INSTANCE();

    private PostgreSQLContainer<?> postgreSQLContainer;
    private HikariDataSource dataSource;

    PostgresTestContainerConfig() {
        this.postgreSQLContainer = new PostgreSQLContainer<>("postgres:9.6.18-alpine")
            .withDatabaseName("mldsInDockerTestContainer")
            .withUsername("abe")
            .withPassword("sapien")
            .withExposedPorts(5432);

        this.postgreSQLContainer.start();
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        this.dataSource.setDriverClassName(postgreSQLContainer.getDriverClassName());
        this.dataSource.setUsername(postgreSQLContainer.getUsername());
        this.dataSource.setPassword(postgreSQLContainer.getPassword());
    }

    public PostgresTestContainerConfig getInstance() {
        return INSTANCE;
    }

    public void setupDataSource() {
        DatabaseConfiguration.setDataSource(dataSource);
    }
}
