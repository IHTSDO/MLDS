package ca.intelliware.ihtsdo.mlds.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.MySQLContainer;

import java.sql.SQLException;

public enum MySqlTestContainerConfig {
    INSTANCE();

    private MySQLContainer<?> mySQLContainer;
    private MysqlDataSource dataSource;

    MySqlTestContainerConfig() {
        this.mySQLContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("mlds")
            .withUsername("root")
            .withPassword("test")
            .withExposedPorts(3306)
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_general_ci", "--lower-case-table-names=1");


        this.mySQLContainer.start();
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUrl(mySQLContainer.getJdbcUrl());
        this.dataSource.setUser(mySQLContainer.getUsername());
        this.dataSource.setPassword(mySQLContainer.getPassword());
        try {
            this.dataSource.setConnectionCollation("utf8mb4_general_ci");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static MySqlTestContainerConfig getInstance() {
        return INSTANCE;
    }

    public void setupDataSource() {
        DatabaseConfiguration.setDataSource(dataSource);
    }


}
