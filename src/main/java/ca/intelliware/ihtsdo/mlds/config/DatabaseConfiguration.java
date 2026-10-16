package ca.intelliware.ihtsdo.mlds.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.intelliware.ihtsdo.mlds.web.filter.MldsOpenEntityManagerInViewFilter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
    basePackages = {
        "ca.intelliware.ihtsdo.mlds.repository",
        DatabaseConfiguration.MLDS_REGISTRATION_PACKAGE
    },
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfiguration implements EnvironmentAware {

    private static final Logger log =
        LoggerFactory.getLogger(DatabaseConfiguration.class);

    private static final String MLDS_DOMAIN_PACKAGE =
        "ca.intelliware.ihtsdo.mlds.domain";
    private static final String COMMONS_EVENT_MODEL_PACKAGE =
        "ca.intelliware.ihtsdo.commons.event.model";
    private static final String COMMONS_EVENT_USER_PACKAGE =
        "ca.intelliware.ihtsdo.commons.event.model.user";
    private static final String COMMONS_EVENT_SYSTEM_PACKAGE =
        "ca.intelliware.ihtsdo.commons.event.model.system";
    static final String MLDS_REGISTRATION_PACKAGE =
        "ca.intelliware.ihtsdo.mlds.registration";

    private Environment env;

    private static DataSource dataSource;

    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean
    public synchronized DataSource dataSource() {

        if (dataSource != null) {
            log.debug("Skipping Datasource configuration - already configured.");
            return dataSource;
        }

        log.debug("Configuring Datasource");

        Binder binder = Binder.get(env);

        String url = binder
            .bind("spring.datasource.url", String.class)
            .orElse(null);

        String databaseName = binder
            .bind("spring.datasource.database-name", String.class)
            .orElse(null);

        validateDatasourceProperties(url, databaseName);

        HikariConfig config = buildHikariConfig(binder, url, databaseName);

        dataSource = new HikariDataSource(config);

        log.debug("Datasource configuration complete");

        return dataSource;
    }

    private void validateDatasourceProperties(String url, String databaseName) {
        if (StringUtils.isBlank(url) && StringUtils.isBlank(databaseName)) {
            if (log.isErrorEnabled()) {
                log.error(
                    "Your database connection pool configuration is incorrect! " +
                        "The application cannot start. Please check your Spring profile. " +
                        "Current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles())
                );
            }

            throw new ApplicationContextException(
                "Database connection pool is not configured correctly"
            );
        }
    }

    private HikariConfig buildHikariConfig(
        Binder binder,
        String url,
        String databaseName
    ) {
        HikariConfig config = new HikariConfig();

        Integer configuredPoolSize = binder
            .bind(
                "spring.datasource.hikari.maximum-pool-size",
                Integer.class
            )
            .orElse(50);
        int maxPoolSize = configuredPoolSize != null ? configuredPoolSize : 50;
        config.setMaximumPoolSize(maxPoolSize);

        String username = binder
            .bind("spring.datasource.username", String.class)
            .orElse(null);

        String password = binder
            .bind("spring.datasource.password", String.class)
            .orElse(null);

        if (StringUtils.isNotBlank(url)) {
            configureWithUrl(config, url, username, password, databaseName);
        } else {
            configureWithDataSourceClass(config, binder, databaseName);
        }

        applyCredentials(config, username, password);

        return config;
    }

    private void configureWithUrl(
        HikariConfig config,
        String url,
        String username,
        String password,
        String databaseName
    ) {
        config.setJdbcUrl(url);

        if (StringUtils.isNotBlank(username)) {
            config.setUsername(username);
        }

        if (StringUtils.isNotBlank(password)) {
            config.setPassword(password);
        }

        if (StringUtils.isNotBlank(databaseName)) {
            config.addDataSourceProperty("databaseName", databaseName);
        }
    }

    private void configureWithDataSourceClass(
        HikariConfig config,
        Binder binder,
        String databaseName
    ) {
        String dataSourceClassName = binder
            .bind(
                "spring.datasource.data-source-class-name",
                String.class
            )
            .orElse(null);

        if (StringUtils.isNotBlank(dataSourceClassName)) {
            config.setDataSourceClassName(dataSourceClassName);
        }

        if (StringUtils.isNotBlank(databaseName)) {
            config.addDataSourceProperty(
                "databaseName",
                databaseName
            );
        }

        String serverName = binder
            .bind(
                "spring.datasource.serverName",
                String.class
            )
            .orElse(null);

        if (StringUtils.isNotBlank(serverName)) {
            config.addDataSourceProperty(
                "serverName",
                serverName
            );
        }
    }

    private void applyCredentials(
        HikariConfig config,
        String username,
        String password
    ) {
        if (StringUtils.isNotBlank(username)) {
            config.addDataSourceProperty("user", username);
        }

        if (StringUtils.isNotBlank(password)) {
            config.addDataSourceProperty("password", password);
        }
    }

    /**
     * Base packages used by the application.
     */
    @Bean(name = "autoConfigurationUtilsBasePackages")
    public List<String> getBasePackages() {

        return Arrays.asList(
            MLDS_DOMAIN_PACKAGE,
            COMMONS_EVENT_MODEL_PACKAGE,
            COMMONS_EVENT_USER_PACKAGE,
            COMMONS_EVENT_SYSTEM_PACKAGE,
            MLDS_REGISTRATION_PACKAGE
        );
    }

    /**
     * Liquibase configuration.
     */
    @Bean
    public SpringLiquibase liquibase() throws SQLException {

        log.debug("Configuring Liquibase");

        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog(
            "classpath:config/liquibase/master.xml"
        );
        liquibase.setContexts(
            "development, production"
        );

        releaseChangelogLock(
            liquibase.getDataSource()
        );

        log.debug("Completed Liquibase configuration");

        return liquibase;
    }

    private void releaseChangelogLock(
        DataSource dataSource
    ) throws SQLException {

        try (Connection connection =
                 dataSource.getConnection()) {

            boolean databaseChangeLogLockExists =
                databaseExists(
                    connection,
                    "databasechangeloglock"
                );

            if (databaseChangeLogLockExists) {

                String releaseLock =
                    "UPDATE DATABASECHANGELOGLOCK " +
                        "SET LOCKED = FALSE, " +
                        "LOCKEDBY = NULL, " +
                        "LOCKGRANTED = NULL";

                try (PreparedStatement statement =
                         connection.prepareStatement(releaseLock)) {

                    statement.executeUpdate();
                }
            }
        }
    }

    private static boolean databaseExists(
        Connection connection,
        String tableName
    ) throws SQLException {

        try (ResultSet resultSet =
                 connection.getMetaData().getTables(
                     connection.getCatalog(),
                     null,
                     tableName,
                     new String[]{"TABLE"}
                 )) {

            return resultSet.next();
        }
    }

    /**
     * Explicit JPA EntityManagerFactory configuration.
     *
     * The application uses a custom LocalContainerEntityManagerFactoryBean,
     * therefore entity packages must be explicitly configured here.
     */
    @Bean(name = "entityManagerFactory")
    @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        if (entityManagerFactory != null) {
            log.debug(
                "Skipping entityManagerFactory configuration - already configured."
            );
            return entityManagerFactory;
        }

        log.debug("Configuring entityManagerFactory");

        entityManagerFactory =
            new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(
            dataSource()
        );

        /*
         * IMPORTANT:
         *
         * User.java is located under:
         *
         * ca.intelliware.ihtsdo.mlds.domain
         *
         * Since this application manually creates the
         * LocalContainerEntityManagerFactoryBean, Hibernate will not
         * automatically discover the entity unless the package is
         * explicitly supplied here.
         */
        entityManagerFactory.setPackagesToScan(
            MLDS_DOMAIN_PACKAGE,
            COMMONS_EVENT_MODEL_PACKAGE,
            COMMONS_EVENT_USER_PACKAGE,
            COMMONS_EVENT_SYSTEM_PACKAGE,
            MLDS_REGISTRATION_PACKAGE
        );

        HibernateJpaVendorAdapter vendorAdapter =
            new HibernateJpaVendorAdapter();

        entityManagerFactory.setJpaVendorAdapter(
            vendorAdapter
        );

        Properties additionalProperties =
            new Properties();

        String dialect =
            env.getProperty(
                "spring.jpa.hibernate.dialect"
            );

        if (dialect != null && !dialect.isBlank()) {
            additionalProperties.put(
                "hibernate.dialect",
                dialect
            );
        }

        String showSql =
            env.getProperty(
                "spring.jpa.show_sql"
            );

        if (showSql != null) {
            additionalProperties.put(
                "hibernate.show_sql",
                showSql
            );
        }

        String ddlAuto =
            env.getProperty(
                "spring.jpa.hibernate.ddl-auto"
            );

        if (ddlAuto != null && !ddlAuto.isBlank()) {
            additionalProperties.put(
                "hibernate.hbm2ddl.auto",
                ddlAuto
            );
        }

        String searchDirType =
            env.getProperty(
                "spring.jpa.properties.hibernate.search.backend.directory.type",
                "local-heap"
            );

        if (!searchDirType.isBlank()) {
            additionalProperties.put(
                "hibernate.search.backend.directory.type",
                searchDirType
            );
        }

        String batchSize =
            env.getProperty(
                "spring.jpa.properties.hibernate.default_batch_fetch_size"
            );

        if (batchSize != null && !batchSize.isBlank()) {
            additionalProperties.put(
                "hibernate.default_batch_fetch_size",
                batchSize
            );
        }

        entityManagerFactory.setJpaProperties(
            additionalProperties
        );

        if (log.isDebugEnabled()) {
            log.debug(
                "JPA entity packages configured: {}",
                Arrays.asList(
                    MLDS_DOMAIN_PACKAGE,
                    COMMONS_EVENT_MODEL_PACKAGE,
                    COMMONS_EVENT_USER_PACKAGE,
                    COMMONS_EVENT_SYSTEM_PACKAGE,
                    MLDS_REGISTRATION_PACKAGE
                )
            );
        }

        log.debug(
            "Completed entityManagerFactory configuration"
        );

        return entityManagerFactory;
    }

    /**
     * Transaction manager.
     */
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager() {

        log.debug("Configuring transactionManager");

        JpaTransactionManager transactionManager =
            new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(
            entityManagerFactory().getObject()
        );

        return transactionManager;
    }

    /**
     * Translate Hibernate/JPA exceptions into Spring
     * DataAccessException hierarchy.
     */
    @Bean
    public static PersistenceExceptionTranslationPostProcessor
    exceptionTranslation() {

        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Open EntityManager for the duration of the web request.
     */
    @Bean
    public OpenEntityManagerInViewFilter
    openEntityManagerInViewFilter() {

        return new MldsOpenEntityManagerInViewFilter();
    }
}

