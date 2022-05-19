package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.ReleasePackageUpdatedEmailSender;

@Service
public class UserNotifier {
	@Resource ReleasePackageUpdatedEmailSender releasePackageUpdatedEmailSender;
	@Resource UserMembershipCalculator userMembershipCalculator;

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

	public void notifyReleasePackageUpdated(ReleaseVersion releaseVersion) {
		ReleasePackage releasePackage = releaseVersion.getReleasePackage();
		Member member = releasePackage.getMember();
		for (User user : userMembershipCalculator.approvedReleaseUsersWithAnyMembership(member)) {
			if (user.getAcceptNotifications() &&
                !(Member.KEY_IHTSDO.equals(member.getKey()) && user.getCountryNotificationsOnly()) ) {
				releasePackageUpdatedEmailSender.sendRelasePackageUpdatedEmail(user, releasePackage, releaseVersion);
			}
		}
	}
}
