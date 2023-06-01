package ca.intelliware.ihtsdo.mlds.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories({"ca.intelliware.ihtsdo.mlds.repository", "ca.intelliware.ihtsdo.mlds.registration", "ca.intelliware.ihtsdo.commons.event"})
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    private Environment env;

    private static DataSource dataSource;

    private static LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Bean
    public synchronized DataSource dataSource() {
    	if (dataSource != null) {
    		log.debug("Skipping Datasource configuration - already configured.");
    		return dataSource;
    	}
        log.debug("Configuring Datasource");
        if (propertyResolver.getProperty("url") == null && propertyResolver.getProperty("databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    "cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
        if (propertyResolver.getProperty("url") == null || "".equals(propertyResolver.getProperty("url"))) {
            config.addDataSourceProperty("databaseName", propertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", propertyResolver.getProperty("serverName"));
        } else {
            config.addDataSourceProperty("url", propertyResolver.getProperty("url"));
        }
        config.addDataSourceProperty("user", propertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", propertyResolver.getProperty("password"));
        dataSource = new HikariDataSource(config);
        log.debug("Datasource configuration complete");
        return dataSource;
    }

    @Bean(name = {"org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages"})
    public List<String> getBasePackages() {
    	return Arrays.asList(
    			"ca.intelliware.ihtsdo.mlds.domain",
    			"ca.intelliware.ihtsdo.commons.event.model",
    			"ca.intelliware.ihtsdo.commons.event.model.user",
    			"ca.intelliware.ihtsdo.commons.event.model.system",
    			"ca.intelliware.ihtsdo.mlds.registration"
    			);
    }

    @Bean
    public SpringLiquibase liquibase() throws SQLException{
        log.debug("Configuring Liquibase");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts("development, production");
        releaseChangelogLock(liquibase.getDataSource());
        log.debug("Completed Liquibase configuration");
        return liquibase;
    }



    private void releaseChangelogLock(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean dbExist = databaseExists(connection,"databasechangeloglock");
            if(dbExist) {
                String releaseLock = "UPDATE DATABASECHANGELOGLOCK SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL";
                try (PreparedStatement statement = connection.prepareStatement(releaseLock)) {
                    statement.executeUpdate();
                }
            }
        }
    }

    private static boolean databaseExists(Connection connection, String tableName) throws SQLException {

        String query = "SELECT 1 FROM " + tableName + " LIMIT 1";

        try (Statement statement = connection.createStatement()) {
            statement.executeQuery(query);
            return true;
        }

        catch (SQLException e) { return false; }

    }

	//See http://blog.netgloo.com/2014/10/06/spring-boot-data-access-with-jpa-hibernate-and-mysql/

	/**
	 * Declare the JPA entity manager factory.
	 */
	@Bean
	@DependsOn("liquibase")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        if (entityManagerFactory != null) {
            log.debug("Skipping entityManagerFactory configuration - already configured.");
            return entityManagerFactory;
        }
        log.debug("Configuring entityManagerFactory");
        entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource());

        // Classpath scanning of @Component, @Service, etc annotated class
		/*entityManagerFactory.setPackagesToScan(
			env.getProperty("entitymanager.packagesToScan")); */

        // Vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        // Hibernate properties -- add explicit check for new property
        if (this.env.getProperty("spring.jpa.properties.hibernate.cache.region.factory_class") == null) {
            throw new IllegalArgumentException("Configuration file is missing required parameter -hibernate.cache.region.factory_class");
        }
        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", this.env.getProperty("spring.jpa.hibernate.dialect"));
        additionalProperties.put("hibernate.show_sql", this.env.getProperty("spring.jpa.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto", this.env.getProperty("spring.jpa.hibernate.ddl-auto"));
        additionalProperties.put("hibernate.cache.region.factory_class", this.env.getProperty("spring.jpa.properties.hibernate.cache.region.factory_class"));
        entityManagerFactory.setJpaProperties(additionalProperties);
        log.debug("Completed entityManagerFactory configuration");
        return entityManagerFactory;
    }

	/**
	 * Declare the transaction manager.
	 */
	@Bean
	public JpaTransactionManager transactionManager() {
		log.debug("Configuring transactionManager");
		JpaTransactionManager transactionManager =
			new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(
			entityManagerFactory().getObject());
		return transactionManager;
	}

	/**
	 * PersistenceExceptionTranslationPostProcessor is a bean post processor
	 * which adds an advisor to any bean annotated with Repository so that any
	 * platform-specific exceptions are caught and then rethrown as one
	 * Spring's unchecked data access exceptions (i.e. a subclass of
	 * DataAccessException).
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

    static void setDataSource(DataSource dataSource) {
        DatabaseConfiguration.dataSource = dataSource;
    }
}

