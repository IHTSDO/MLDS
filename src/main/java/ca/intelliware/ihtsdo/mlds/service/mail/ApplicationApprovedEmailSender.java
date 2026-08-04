package ca.intelliware.ihtsdo.mlds.service.mail;

import ca.intelliware.ihtsdo.mlds.domain.User;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class ApplicationApprovedEmailSender {

    @Resource
    private MailService mailService;

    @Resource
    private TemplateEvaluator templateEvaluator;

    @Resource
    private ClientLinkBuilder clientLinkBuilder;

    public void sendApplicationApprovalEmail(User user,
                                             String memberKey,
                                             long affiliateId,
                                             boolean includeInvoiceParagraph) {

        final Locale locale = Locale.forLanguageTag(user.getLangKey());

        Map<String, Object> variables = Maps.newHashMap();
        variables.put(EmailVariables.AFFILIATE_ID, Long.toString(affiliateId));
        variables.put(EmailVariables.USER, user);
        variables.put(EmailVariables.MEMBERKEY, memberKey);
        variables.put(EmailVariables.LOGIN_URL, clientLinkBuilder.buildLoginLink());
        variables.put(EmailVariables.VIEW_PACKAGES_URL, clientLinkBuilder.buildViewReleasesLink());

        variables.put("includeInvoicingInfo", includeInvoiceParagraph);
        String content = templateEvaluator.evaluateTemplate(
            "applicationApprovedEmail",
            locale,
            variables);

        String subject = templateEvaluator.getTitleFor("applicationApproved", locale);

        mailService.sendEmail(user.getEmail(), subject, content, false, true);
    }
}
