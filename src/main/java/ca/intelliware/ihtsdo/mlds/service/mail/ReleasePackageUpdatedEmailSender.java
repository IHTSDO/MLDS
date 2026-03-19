package ca.intelliware.ihtsdo.mlds.service.mail;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReleasePackageUpdatedEmailSender {

    @Resource
    MailService mailService;
    @Resource
    TemplateEvaluator templateEvaluator;
    @Resource
    ClientLinkBuilder clientLinkBuilder;

    private final AffiliateRepository affiliateRepository;

    public ReleasePackageUpdatedEmailSender( AffiliateRepository affiliateRepository) {

        this.affiliateRepository = affiliateRepository;
    }

    public void sendRelasePackageUpdatedEmail(User user, ReleasePackage releasePackage, ReleaseVersion releaseVersion) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Map<String, Object> variables = Maps.newHashMap();
        variables.put(EmailVariables.RELEASE_PACKAGE, releasePackage);
        variables.put(EmailVariables.RELEASE_VERSION, releaseVersion);
        variables.put(EmailVariables.USER, user);
        variables.put(EmailVariables.MEMBERKEY, releasePackage.getMember().getKey());
        variables.put(EmailVariables.VIEW_RELEASE_PACKAGE_URL, clientLinkBuilder.buildViewReleasePackageLink(releasePackage.getReleasePackageId()));
        variables.put(EmailVariables.VIEW_PACKAGES_URL, clientLinkBuilder.buildViewReleasesLink());
        List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(user.getLogin());
        if (affiliates == null || affiliates.isEmpty()) {
            throw new IllegalArgumentException("Affiliate not found for user: " + user.getLogin());
        }
        long affiliateId = affiliates.get(0).getAffiliateId();
        String unsubscribeUrl = clientLinkBuilder.buildUnsubscribeLink(affiliateId, user.getUnsubscribeKey());
        variables.put(EmailVariables.UNSUBSCRIBE_URL, unsubscribeUrl);

        String content = templateEvaluator.evaluateTemplate("releasePackageUpdatedEmail", locale, variables);

        String subject = templateEvaluator.getTitleFor("releasePackageUpdated", locale);

        mailService.sendEmail(user.getEmail(), subject, content, false, true);

    }

}
