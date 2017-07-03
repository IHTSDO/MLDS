package ca.intelliware.ihtsdo.mlds.config;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amazonaws.services.kinesis.model.InvalidArgumentException;
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
    public SpringLiquibase liquibase() {
        log.debug("Configuring Liquibase");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts("development, production");
        log.debug("Completed Liquibase configuration");
        return liquibase;
    }
	
	//See http://blog.netgloo.com/2014/10/06/spring-boot-data-access-with-jpa-hibernate-and-mysql/
	
	/**
	 * Declare the JPA entity manager factory.
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		if (entityManagerFactory != null) {
			log.debug("Skipping entityManagerFactory configuration - already configured.");
			return entityManagerFactory;
		}
		log.debug("Configuring entityManagerFactory");
		entityManagerFactory =  new LocalContainerEntityManagerFactoryBean();
		
		entityManagerFactory.setDataSource(dataSource());
		
		// Classpath scanning of @Component, @Service, etc annotated class
		/*entityManagerFactory.setPackagesToScan(
			env.getProperty("entitymanager.packagesToScan")); */
		
		// Vendor adapter
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
		
		// Hibernate properties -- add explicit check for new property
		if (env.getProperty("hibernate.cache.region.factory_class") == null) {
			throw new InvalidArgumentException("Configuration file is missing required parameter -hibernate.cache.region.factory_class" );
		}
		Properties additionalProperties = new Properties();
		additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
		additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		additionalProperties.put("hibernate.cache.region.factory_class", env.getProperty("hibernate.cache.region.factory_class"));
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

}

