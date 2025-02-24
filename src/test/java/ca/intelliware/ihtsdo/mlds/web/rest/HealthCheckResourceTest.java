package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.config.metrics.DatabaseHealthCheckIndicator;
import ca.intelliware.ihtsdo.mlds.config.metrics.JavaMailHealthCheckIndicator;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckResourceTest {
    @InjectMocks
    private HealthCheckResource healthCheckResource;

    @Mock
    private JavaMailHealthCheckIndicator javaMailHealthCheckIndicator;

    @Mock
    private DatabaseHealthCheckIndicator databaseHealthCheckIndicator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCheckHealth_BothServicesHealthy() {
        // Arrange
        when(javaMailHealthCheckIndicator.health()).thenReturn(Health.up().build());
        when(databaseHealthCheckIndicator.health()).thenReturn(Health.up().build());

        // Act
        Health health = healthCheckResource.checkHealth();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertTrue(health.getDetails().containsKey("mail"));
        assertTrue(health.getDetails().containsKey("database"));

        // Check that the details are also healthy
        Health mailHealthDetail = (Health) health.getDetails().get("mail");
        Health databaseHealthDetail = (Health) health.getDetails().get("database");

        assertEquals(Status.UP, mailHealthDetail.getStatus());
        assertEquals(Status.UP, databaseHealthDetail.getStatus());
    }


    @Test
    public void testCheckHealth_MailServiceUnhealthy() {
        // Arrange
        when(javaMailHealthCheckIndicator.health()).thenReturn(Health.down().build());
        when(databaseHealthCheckIndicator.health()).thenReturn(Health.up().build());

        // Act
        Health health = healthCheckResource.checkHealth();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("mail"));
        assertTrue(health.getDetails().containsKey("database"));
    }

    @Test
    public void testCheckHealth_DatabaseServiceUnhealthy() {
        // Arrange
        when(javaMailHealthCheckIndicator.health()).thenReturn(Health.up().build());
        when(databaseHealthCheckIndicator.health()).thenReturn(Health.down().build());

        // Act
        Health health = healthCheckResource.checkHealth();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("mail"));
        assertTrue(health.getDetails().containsKey("database"));
    }

    @Test
    public void testCheckHealth_BothServicesUnhealthy() {
        // Arrange
        when(javaMailHealthCheckIndicator.health()).thenReturn(Health.down().build());
        when(databaseHealthCheckIndicator.health()).thenReturn(Health.down().build());

        // Act
        Health health = healthCheckResource.checkHealth();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("mail"));
        assertTrue(health.getDetails().containsKey("database"));
    }

}
