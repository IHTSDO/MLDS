package ca.intelliware.ihtsdo.mlds.config.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JHipsterHealthIndicatorConfiguration implements InitializingBean {

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private DataSource dataSource;

    private JavaMailHealthCheckIndicator javaMailHealthCheckIndicator = new JavaMailHealthCheckIndicator();
    private DatabaseHealthCheckIndicator databaseHealthCheckIndicator = new DatabaseHealthCheckIndicator();

    @Bean
    public HealthIndicator<Map<String, HealthCheckIndicator.Result>> healthIndicator() {
        return new HealthIndicator<Map<String, HealthCheckIndicator.Result>>() {
            @Override
            public Map<String, HealthCheckIndicator.Result> health() {
                Map<String, HealthCheckIndicator.Result> healths = new LinkedHashMap<>();

                healths.putAll(javaMailHealthCheckIndicator.health());
                healths.putAll(databaseHealthCheckIndicator.health());

                return healths;
            }
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        javaMailHealthCheckIndicator.setJavaMailSender(javaMailSender);
        databaseHealthCheckIndicator.setDataSource(dataSource);
    }
}
