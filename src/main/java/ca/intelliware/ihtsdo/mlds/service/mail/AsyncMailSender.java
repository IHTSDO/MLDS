package ca.intelliware.ihtsdo.mlds.service.mail;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Handles asynchronous e-mail sending.
 */
@Service
public class AsyncMailSender {

    private final Logger log = LoggerFactory.getLogger(AsyncMailSender.class);

    private final JavaMailSenderImpl javaMailSender;

    public AsyncMailSender(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(
        String to,
        String subject,
        String content,
        boolean isMultipart,
        boolean isHtml,
        String from) {

        log.debug(
            "Send e-mail[multipart '{}' and html '{}'] to '{}' "
                + "with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content);

        final MimeMessage mimeMessage =
            javaMailSender.createMimeMessage();

        try {
            final MimeMessageHelper message = new MimeMessageHelper(
                mimeMessage,
                isMultipart,
                StandardCharsets.UTF_8.name());

            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);

            log.debug("Sent e-mail to User '{}'!", to);
        } catch (Exception e) {
            log.warn(
                "E-mail could not be sent to user '{}', exception is: {}",
                to,
                e.getMessage());
        }
    }
}
