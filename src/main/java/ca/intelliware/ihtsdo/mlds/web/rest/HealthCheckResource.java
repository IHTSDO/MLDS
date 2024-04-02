package ca.intelliware.ihtsdo.mlds.web.rest;
import ca.intelliware.ihtsdo.mlds.config.metrics.DatabaseHealthCheckIndicator;
import ca.intelliware.ihtsdo.mlds.config.metrics.JavaMailHealthCheckIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckResource {

    @Autowired
    private JavaMailHealthCheckIndicator javaMailHealthCheckIndicator;

    @Autowired
    private DatabaseHealthCheckIndicator databaseHealthCheckIndicator;

    @GetMapping("/health")
    public Health checkHealth() {
        Health.Builder healthBuilder = new Health.Builder();
        boolean isMailHealthy = true;
        boolean isDatabaseHealthy = true;

        // Check mail service health
        try {
            Health mailHealth = checkMailServiceHealth();
            if (mailHealth.getStatus().equals(Status.UP)) {
                healthBuilder.withDetail("mail", mailHealth);
            } else {
                isMailHealthy = false;
                healthBuilder.down().withDetail("mail", mailHealth);
            }
        } catch (Exception e) {
            isMailHealthy = false;
            healthBuilder.down().withDetail("mail", "Mail service check failed: " + e.getMessage());
        }

        // Check database health
        try {
            Health databaseHealth = checkDatabaseHealth();
            if (databaseHealth.getStatus().equals(Status.UP)) {
                healthBuilder.withDetail("database", databaseHealth);
            } else {
                isDatabaseHealthy = false;
                healthBuilder.down().withDetail("database", databaseHealth);
            }
        } catch (Exception e) {
            isDatabaseHealthy = false;
            healthBuilder.down().withDetail("database", "Database check failed: " + e.getMessage());
        }

        // Set overall health status
        if (isMailHealthy && isDatabaseHealthy) {
            return Health.up().withDetails(healthBuilder.build().getDetails()).build();
        } else {
            return healthBuilder.build();
        }
    }

    private Health checkMailServiceHealth() {
        try {
            return javaMailHealthCheckIndicator.health();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    private Health checkDatabaseHealth() {
        try {
            return databaseHealthCheckIndicator.health();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

