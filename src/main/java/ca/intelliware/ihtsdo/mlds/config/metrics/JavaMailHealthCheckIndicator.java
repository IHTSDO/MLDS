package ca.intelliware.ihtsdo.mlds.config.metrics;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * SpringBoot Actuator HealthIndicator check for JavaMail.
 */
public class JavaMailHealthCheckIndicator extends AbstractHealthIndicator {

    private final Logger log = LoggerFactory.getLogger(JavaMailHealthCheckIndicator.class);

    private JavaMailSenderImpl javaMailSender;

    public JavaMailHealthCheckIndicator(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        log.debug("Initializing JavaMail health indicator");

        try {
            javaMailSender.getSession().getTransport().connect(javaMailSender.getHost(),
                    javaMailSender.getUsername(),
                    javaMailSender.getPassword());

            builder.up();

        } catch (MessagingException e) {
            log.debug("Cannot connect to e-mail server.", e);
            builder.down(e);
        }
    }
}
