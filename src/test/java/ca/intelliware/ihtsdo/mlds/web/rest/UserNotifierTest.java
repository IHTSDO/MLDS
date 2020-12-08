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
		
		ReleasePackage releasePackage = new ReleasePackage(1L);
		releasePackage.setMember(member);
		ReleaseVersion releaseVersion = new ReleaseVersion(2L);
		releasePackage.addReleaseVersion(releaseVersion);
		
		User user1 = withUser(1L);
		User user2 = withUser(2L);
		
		Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(member)).thenReturn(Arrays.asList(user1, user2));
		
		userNotifier.notifyReleasePackageUpdated(releaseVersion);
		
		Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);
		Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1)).sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
	}
	
	private User withUser(long id) {
		User user = new User();
		user.setUserId(id);
		user.setAcceptNotifications(true);
		return user;
	}
}
