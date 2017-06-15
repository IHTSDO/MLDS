package ca.intelliware.ihtsdo.mlds.config.metrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
public class JHipsterHealthIndicatorConfiguration {

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private DataSource dataSource;

    @Bean
    public JavaMailHealthCheckIndicator javaMailHealthCheckIndicator() {
        return new JavaMailHealthCheckIndicator(javaMailSender);
    }

    @Bean
    public DatabaseHealthCheckIndicator databaseHealthCheckIndicator() {
        return new DatabaseHealthCheckIndicator(new JdbcTemplate(dataSource));
    }

}
