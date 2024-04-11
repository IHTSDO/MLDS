package ca.intelliware.ihtsdo.mlds.service.mail;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    public static final String TEMPLATE_SUFFIX = "Email";
    public static final String EMAIL_ACTIVATION_PREFIX = "activation";

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired private Environment env;

    @Autowired private JavaMailSenderImpl javaMailSender;

    @Autowired private MessageSource messageSource;

	@Resource TemplateEvaluator templateEvaluator;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
        log.debug("Confgured From address: {}", from);
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'!", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(final String email, String content, Locale locale) {
        log.debug("Sending activation e-mail to '{}'", email);
        final String subject = messageSource.getMessage(EMAIL_ACTIVATION_PREFIX + ".title", null, locale);
        sendEmail(email, subject, content, false, true);
    }


}
