package ca.intelliware.ihtsdo.mlds.service.mail;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class ReleasePackageUpdatedEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

	public void sendRelasePackageUpdatedEmail(User user, ReleasePackage releasePackage, ReleaseVersion releaseVersion) {
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.RELEASE_PACKAGE, releasePackage);
		variables.put(EmailVariables.RELEASE_VERSION, releaseVersion);
		variables.put(EmailVariables.USER, user);
		variables.put(EmailVariables.MEMBERKEY, releasePackage.getMember().getKey());
		variables.put(EmailVariables.VIEW_RELEASE_PACKAGE_URL, clientLinkBuilder.buildViewReleasePackageLink(releasePackage.getReleasePackageId()));
		variables.put(EmailVariables.VIEW_PACKAGES_URL, clientLinkBuilder.buildViewReleasesLink());
		String content = templateEvaluator.evaluateTemplate("releasePackageUpdatedEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("releasePackageUpdated", locale);

		mailService.sendEmail(user.getEmail(), subject, content, false, true);
	}

}
