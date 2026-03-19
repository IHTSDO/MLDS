package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.ReleasePackageUpdatedEmailSender;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.List;


@Service
public class UserNotifier {
    @Resource
    ReleasePackageUpdatedEmailSender releasePackageUpdatedEmailSender;
    @Resource
    UserMembershipCalculator userMembershipCalculator;
    @Resource
    public UserRepository userRepository;
    @PersistenceContext
    EntityManager entityManager;
    private final Logger log = LoggerFactory.getLogger(UserResource.class);


    public void notifyReleasePackageUpdated(ReleaseVersion releaseVersion) {

        ReleasePackage releasePackage = releaseVersion.getReleasePackage();
        Member member = releasePackage.getMember();

        List<User> users = (List<User>) userMembershipCalculator
            .approvedReleaseUsersWithAnyMembership(member);
        List<User> eligibleUsers = users.stream()
            .filter(user -> user.getAcceptNotifications()
                && !(Member.KEY_IHTSDO.equals(member.getKey())
                && user.getCountryNotificationsOnly()))
            .toList();

        List<String> logins = eligibleUsers.stream()
            .map(User::getLogin)
            .toList();

        if (logins.isEmpty()) {
            return;
        }

        userRepository.updateUnsubscribeKeysForUsers(logins);
        entityManager.clear();

        List<User> updatedUsers = userRepository.findByLoginIgnoreCaseIn(logins);


        for (User user : updatedUsers) {
            try {
                releasePackageUpdatedEmailSender.sendRelasePackageUpdatedEmail(
                    user, releasePackage, releaseVersion
                );

            } catch (MailException e) {
                throw new IllegalStateException(
                    "Mail error for user: " + user.getLogin(), e);

            } catch (TemplateProcessingException e) {
                throw new IllegalStateException(
                    "Template error for user: " + user.getLogin(), e);

            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                    "Invalid data for user: " + user.getLogin(), e);
            }
        }

    }
}
