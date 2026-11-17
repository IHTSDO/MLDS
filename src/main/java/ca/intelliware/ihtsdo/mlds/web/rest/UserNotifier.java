package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.ReleasePackageUpdatedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.NotificationResponseDTO;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.ArrayList;
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

    private final Logger log =
        LoggerFactory.getLogger(UserNotifier.class);

    public NotificationResponseDTO notifyReleasePackageUpdated(
        ReleaseVersion releaseVersion
    ) {

        ReleasePackage releasePackage =
            releaseVersion.getReleasePackage();

        Member member = releasePackage.getMember();

        List<User> users =
            (List<User>) userMembershipCalculator
                .approvedReleaseUsersWithAnyMembership(member);

        List<User> eligibleUsers = users.stream()
            .filter(user -> user.getAcceptNotifications()
                && !(Member.KEY_IHTSDO.equals(member.getKey())
                && user.getCountryNotificationsOnly()))
            .toList();

        List<String> logins = eligibleUsers.stream()
            .map(User::getLogin)
            .toList();

        int totalRecipients = eligibleUsers.size();
        int sentCount = 0;
        int failedCount = 0;

        List<String> errors = new ArrayList<>();

        // No eligible recipients
        if (logins.isEmpty()) {

            log.info(
                "No eligible recipients found for release version: {}",
                releaseVersion.getReleaseVersionId()
            );

            return new NotificationResponseDTO(
                0,
                0,
                0,
                "NO_RECIPIENTS",
                errors
            );
        }

        // Update unsubscribe keys
        userRepository.updateUnsubscribeKeysForUsers(logins);

        entityManager.clear();

        List<User> updatedUsers =
            userRepository.findByLoginIgnoreCaseIn(logins);

        // Send emails individually
        for (User user : updatedUsers) {

            try {

                releasePackageUpdatedEmailSender
                    .sendRelasePackageUpdatedEmail(
                        user,
                        releasePackage,
                        releaseVersion
                    );

                sentCount++;

                log.info(
                    "Release notification email sent successfully to user: {}",
                    user.getLogin()
                );

            } catch (MailException
                     | TemplateProcessingException
                     | IllegalArgumentException e) {

                failedCount++;

                String errorMessage =
                    "Failed to send email to " + user.getLogin();

                errors.add(errorMessage);

                log.error(
                    "Release notification email failed for user: {}",
                    user.getLogin(),
                    e
                );

            } catch (Exception e) {

                failedCount++;

                String errorMessage =
                    "Unexpected error while sending email to "
                        + user.getLogin();

                errors.add(errorMessage);

                log.error(
                    "Unexpected notification error for user: {}",
                    user.getLogin(),
                    e
                );
            }
        }

        // Account for users that were not returned by the second query.
        int unprocessedRecipients =
            totalRecipients - updatedUsers.size();

        if (unprocessedRecipients > 0) {

            failedCount += unprocessedRecipients;

            for (int i = 0; i < unprocessedRecipients; i++) {
                errors.add(
                    "Unable to retrieve an eligible recipient for notification"
                );
            }
        }

        String status;

        if (failedCount == 0 && sentCount == totalRecipients) {
            status = "SUCCESS";
        } else if (sentCount > 0) {
            status = "PARTIAL_SUCCESS";
        } else {
            status = "FAILED";
        }

        log.info(
            "Release notification completed. Total: {}, Sent: {}, Failed: {}",
            totalRecipients,
            sentCount,
            failedCount
        );

        return new NotificationResponseDTO(
            totalRecipients,
            sentCount,
            failedCount,
            status,
            errors
        );
    }
}
