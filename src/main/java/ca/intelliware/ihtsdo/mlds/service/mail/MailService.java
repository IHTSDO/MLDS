package ca.intelliware.ihtsdo.mlds.service.mail;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for sending e-mails.
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    public static final String TEMPLATE_SUFFIX = "Email";
    public static final String EMAIL_ACTIVATION_PREFIX = "activation";

    private final Logger log = LoggerFactory.getLogger(MailService.class);


    private final Environment env;

    private final MessageSource messageSource;
    private final AsyncMailSender asyncMailSender;

    @Resource
    TemplateEvaluator templateEvaluator;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    public MailService(
        Environment env, MessageSource messageSource,
        AsyncMailSender asyncMailSender) {
        this.env = env;
        this.messageSource = messageSource;
        this.asyncMailSender = asyncMailSender;
    }

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
        log.debug("Configured From address: {}", from);
    }

    public void sendEmail(
        String to,
        String subject,
        String content,
        boolean isMultipart,
        boolean isHtml) {

        asyncMailSender.sendEmail(
            to,
            subject,
            content,
            isMultipart,
            isHtml,
            from);
    }

    @Async
    public void sendActivationEmail(
        final String email,
        String content,
        Locale locale) {

        log.debug("Sending activation e-mail to '{}'", email);

        final String subject = messageSource.getMessage(
            EMAIL_ACTIVATION_PREFIX + ".title",
            null,
            locale);

        asyncMailSender.sendEmail(
            email,
            subject,
            content,
            false,
            true,
            from);
    }
}
