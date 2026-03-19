package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Arrays;
import java.util.List;

import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.ReleasePackageUpdatedEmailSender;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserNotifierTest {

    @Mock ReleasePackageUpdatedEmailSender releasePackageUpdatedEmailSender;
    @Mock UserMembershipCalculator userMembershipCalculator;
    @Mock
    UserRepository userRepository;

    @Mock
    EntityManager entityManager;

    private UserNotifier userNotifier;

    @Before
    public void setup() {
        userNotifier = new UserNotifier();

        userNotifier.releasePackageUpdatedEmailSender = releasePackageUpdatedEmailSender;
        userNotifier.userMembershipCalculator = userMembershipCalculator;
        userNotifier.userRepository = userRepository;
        userNotifier.entityManager = entityManager;
    }

    @Test
    public void notifyReleasePackageUpdatedShouldEmailEachMatchingUser() {
        Member member = new Member("se", 10L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

        User user1 = withUser(1L, true, false);
        User user2 = withUser(2L, true, false);

        Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(Mockito.any()))
            .thenReturn(Arrays.asList(user1, user2));

        Mockito.when(userRepository.findByLoginIgnoreCaseIn(Mockito.anyList()))
            .thenAnswer(invocation -> {
                List<String> requestedLogins = invocation.getArgument(0);

                return Arrays.asList(user1, user2).stream()
                    .filter(u -> requestedLogins.contains(u.getLogin()))
                    .toList();
            });

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1))
            .sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1))
            .sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
    }

    @Test
    public void notifyReleasePackageUpdatedShouldNotEmailUserOptingOut() {

        Member member = new Member("se", 10L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

        User user1 = withUser(1L, true, false);
        User user2 = withUser(2L, false, false);

        Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(Mockito.any()))
            .thenReturn(Arrays.asList(user1, user2));

        Mockito.when(userRepository.findByLoginIgnoreCaseIn(Mockito.anyList()))
            .thenAnswer(invocation -> {
                List<String> requestedLogins = invocation.getArgument(0);

                return Arrays.asList(user1, user2).stream()
                    .filter(u -> requestedLogins.contains(u.getLogin()))
                    .toList();
            });

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1))
            .sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.never())
            .sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
    }

    @Test
    public void notifyReleasePackageUpdatedShouldNotEmailUserWithCountryOnlyChoice() {
        Member member = new Member("IHTSDO", 1L);
        ReleasePackage releasePackage = withReleasePackage(member);
        ReleaseVersion releaseVersion = releasePackage.getReleaseVersions().stream().findFirst().get();

        User user1 = withUser(1L, true, true);
        User user2 = withUser(2L, true, false);

        Mockito.when(userMembershipCalculator.approvedReleaseUsersWithAnyMembership(Mockito.any()))
            .thenReturn(Arrays.asList(user1, user2));

        Mockito.when(userRepository.findByLoginIgnoreCaseIn(Mockito.anyList()))
            .thenAnswer(invocation -> {
                List<String> requestedLogins = invocation.getArgument(0);

                return Arrays.asList(user1, user2).stream()
                    .filter(u -> requestedLogins.contains(u.getLogin()))
                    .toList();
            });

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.never())
            .sendRelasePackageUpdatedEmail(user1, releasePackage, releaseVersion);

        Mockito.verify(releasePackageUpdatedEmailSender, Mockito.times(1))
            .sendRelasePackageUpdatedEmail(user2, releasePackage, releaseVersion);
    }

    private User withUser(long id, boolean accepNotifications, boolean countryNotificationsOnly) {
        User user = new User();
        user.setUserId(id);
        user.setLogin("user" + id);
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
