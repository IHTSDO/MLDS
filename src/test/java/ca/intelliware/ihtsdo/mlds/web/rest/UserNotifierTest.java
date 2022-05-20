package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.ReleasePackageUpdatedEmailSender;

@RunWith(MockitoJUnitRunner.class)
public class UserNotifierTest {

	@Mock ReleasePackageUpdatedEmailSender releasePackageUpdatedEmailSender;
	@Mock UserMembershipCalculator userMembershipCalculator;

	private UserNotifier userNotifier;

	@Before
	public void setup() {
		userNotifier = new UserNotifier();

		userNotifier.releasePackageUpdatedEmailSender = releasePackageUpdatedEmailSender;
		userNotifier.userMembershipCalculator = userMembershipCalculator;
	}

	@Test
	public void notifyReleasePackageUpdatedShouldEmailEachMatchingUser() {
		Member member = new Member("se", 10L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

		User user1 = withUser(1L, true, false);
		User user2 = withUser(2L, true, false);

		Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(member)).thenReturn(Arrays.asList(user1, user2));

		userNotifier.notifyReleasePackageUpdated(releaseVersion);

		Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);
		Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
	}

    @Test
    public void notifyReleasePackageUpdatedShouldNotEmailUserOptingOut() {
        Member member = new Member("se", 10L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

        User user1 = withUser(1L, true, false);
        User user2 = withUser(2L, false, false);

        Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(member)).thenReturn(Arrays.asList(user1, user2));

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);
        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(0)).sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
    }

    @Test
    public void notifyReleasePackageUpdatedShouldNotEmailUserWithCountryOnlyChoice() {
        Member member = new Member("IHTSDO", 1L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

        User user1 = withUser(1L, true, true);
        User user2 = withUser(2L, true, false);

        Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(member)).thenReturn(Arrays.asList(user1, user2));

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(0)).sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);
        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
    }

    private User withUser(long id, boolean accepNotifications, boolean countryNotificationsOnly) {
		User user = new User();
		user.setUserId(id);
		user.setAcceptNotifications(accepNotifications);
        user.setCountryNotificationsOnly(countryNotificationsOnly);
        return user;
	}

    private ReleasePackage withReleasePackage(Member member) {
        ReleasePackage releasePackage = new ReleasePackage(1L);
        releasePackage.setMember(member);
        ReleaseVersion releaseVersion = new ReleaseVersion(2L);
        releasePackage.addReleaseVersion(releaseVersion);
        releasePackage.getReleaseVersions().stream().findFirst();
        return releasePackage;
    }
}
