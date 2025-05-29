package ca.intelliware.ihtsdo.mlds.service.mail;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReleasePackageUpdatedEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

    AffiliateRepository affiliateRepository;
    UserRepository userRepository;

    public ReleasePackageUpdatedEmailSender(UserRepository userRepository, AffiliateRepository affiliateRepository) {
        this.userRepository = userRepository;
        this.affiliateRepository = affiliateRepository;
    }

    public void sendRelasePackageUpdatedEmail(User user, ReleasePackage releasePackage, ReleaseVersion releaseVersion) {
        final Locale locale = Locale.forLanguageTag(user.getLangKey());
        Map<String, Object> variables = Maps.newHashMap();

        // Add existing variables
        variables.put(EmailVariables.RELEASE_PACKAGE, releasePackage);
        variables.put(EmailVariables.RELEASE_VERSION, releaseVersion);
        variables.put(EmailVariables.USER, user);
        variables.put(EmailVariables.MEMBERKEY, releasePackage.getMember().getKey());
        variables.put(EmailVariables.VIEW_RELEASE_PACKAGE_URL, clientLinkBuilder.buildViewReleasePackageLink(releasePackage.getReleasePackageId()));
        variables.put(EmailVariables.VIEW_PACKAGES_URL, clientLinkBuilder.buildViewReleasesLink());

        // Generate unsubscribe URL with affiliateId and unsubscribeKey
        String unsubscribeUrl = generateUnsubscribeData(user);
        variables.put(EmailVariables.UNSUBSCRIBE_URL, unsubscribeUrl);  // Add unsubscribe URL to the variables

        // Generate the content using the template
        String content = templateEvaluator.evaluateTemplate("releasePackageUpdatedEmail", locale, variables);

        // Get the subject for the email
        String subject = templateEvaluator.getTitleFor("releasePackageUpdated", locale);

        // Send the email
        mailService.sendEmail(user.getEmail(), subject, content, false, true);
    }


    // New method to generate unsubscribe key and fetch affiliateId
    public String generateUnsubscribeData(User user) {
        // Fetch affiliate using creator from Affiliate table (returns Affiliate, not Optional)
        List<Affiliate> affiliateList = affiliateRepository.findByCreatorIgnoreCase(user.getLogin());

        // Check if affiliate exists and is not empty
        if (affiliateList != null && !affiliateList.isEmpty()) {
            Affiliate affiliate = affiliateList.get(0);  // Get the first affiliate

            long affiliateId = affiliate.getAffiliateId();  // Get affiliateId

            // Generate unsubscribe key if it does not exist already
            if (user.getUnsubscribeKey() == null || user.getUnsubscribeKey().isEmpty()) {
                user.setUnsubscribeKey(UUID.randomUUID().toString());  // Generate new unsubscribe key
                userRepository.save(user);  // Save the user with the new unsubscribe key
            }

            // Return the unsubscribe URL with affiliateId and unsubscribeKey
            return clientLinkBuilder.buildUnsubscribeLink(affiliateId, user.getUnsubscribeKey());
        } else {
            // Handle case where affiliate is not found
            throw new IllegalArgumentException("Affiliate not found for the user.");
        }
    }


}
