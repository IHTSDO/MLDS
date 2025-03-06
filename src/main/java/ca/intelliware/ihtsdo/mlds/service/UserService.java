package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.security.SecurityUtils;
import ca.intelliware.ihtsdo.mlds.service.util.RandomUtil;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateDetailsResponseDTO;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ca.intelliware.ihtsdo.mlds.domain.ApprovalState.CHANGE_REQUESTED;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Resource
    AutologinService autologinService;
    @Autowired
    private AffiliateDetailsRepository affiliateDetailsRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    public User activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        User user = userRepository.getUserByActivationKey(key);

        // activate given user for the registration key.
        if (user != null) {
            user.setActivated(true);
            // MLDS-234 Rory requested that activation keys not expire
            // user.setActivationKey(null);
            autologinService.loginUser(user);

            log.debug("Activated user: {}", user);
        }

        return user;
    }


    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey, Boolean activated) {
        User newUser = new User();
        Optional<Authority> authorityOptional = authorityRepository.findById("ROLE_USER");
        Authority authority = authorityOptional.get();
        Set<Authority> authorities = new HashSet<Authority>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(activated);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String firstName, String lastName, String email) {
        User currentUser = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        log.debug("Changed Information for User: {}", currentUser);
    }

    public void changePassword(String password) {
        User currentUser = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        changePassword(currentUser, password);
    }

    protected void changePassword(User user, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        log.debug("Changed password for User: {}", user);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User currentUser = userRepository.findByLoginIgnoreCase(SecurityUtils.getCurrentLogin());
        if (currentUser != null) {
            currentUser.getAuthorities().size(); // eagerly load the association
        }
        return currentUser;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(LocalDate.now().minusMonths(1));
        for (PersistentToken token : tokens) {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        }
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findNotActivatedUsersByCreationDateBefore(LocalDate.now().minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    public AffiliateDetailsResponseDTO getAffiliateDetails(String email, Long affiliateDetailsId) {
        // Fetch user details
        User user = userRepository.findByLoginIgnoreCase(email);

        // Fetch affiliates by creator email
        List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(email);

        // Return an empty DTO if no affiliates are found
        if (affiliates.isEmpty()) {
            return new AffiliateDetailsResponseDTO(user, null, Collections.emptyList());
        }

        // Get first affiliate's details safely
        Affiliate firstAffiliate = affiliates.get(0);

        return new AffiliateDetailsResponseDTO(user, firstAffiliate.getAffiliateDetails(), affiliates);
    }


    @Transactional
    public void updatePrimaryEmail(String login, String updatedEmail) {
        User user = userRepository.findByLoginIgnoreCase(login);
        if (user == null) {
            throw new NoSuchElementException("User not found for login: " + login);
        }

        // Check if the updated email already exists in the system
        User existingUser = userRepository.findByLoginIgnoreCase(updatedEmail);
        if (existingUser != null) {
            handleExistingUserConflict(existingUser, updatedEmail);
        }

        // Update the current user's email
        updateUserEmail(user, updatedEmail);

        // Update associated records
        updateRelatedEntities(login, updatedEmail);
    }

    /**
     * Handles the scenario where the updated email already exists in the system.
     */
    private void handleExistingUserConflict(User existingUser, String updatedEmail) {
        String modifiedEmail = addOldToEmail(updatedEmail);

        updateAffiliates(updatedEmail, modifiedEmail, true); // Deactivate affiliates
        updateAffiliateDetails(updatedEmail, modifiedEmail);
        updateApplications(updatedEmail, modifiedEmail);

        // Deactivate and rename existing user
        existingUser.setLogin(modifiedEmail);
        existingUser.setEmail(modifiedEmail);
        existingUser.setActivated(false);
        userRepository.save(existingUser);
    }

    /**
     * Updates the email of the given user.
     */
    private void updateUserEmail(User user, String updatedEmail) {
        user.setLogin(updatedEmail);
        user.setEmail(updatedEmail);
        userRepository.save(user);
    }

    /**
     * Updates all related entities (Affiliates, Applications, and AffiliateDetails).
     */
    private void updateRelatedEntities(String oldEmail, String newEmail) {
        updateApplications(oldEmail, newEmail);
        updateAffiliates(oldEmail, newEmail, false);
        updateAffiliateDetails(oldEmail, newEmail);
    }

    /**
     * Updates the email field in the Application table.
     */
    private void updateApplications(String oldEmail, String newEmail) {
        List<Application> applications = applicationRepository.findByUsernameIgnoreCase(oldEmail);
        if (!applications.isEmpty()) {
            applications.forEach(app -> app.setUsername(newEmail));
            applicationRepository.saveAll(applications);
        }
    }

    /**
     * Updates the creator field in the Affiliate table.
     */
    private void updateAffiliates(String oldEmail, String newEmail, boolean deactivate) {
        List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(oldEmail);
        if (!affiliates.isEmpty()) {
            affiliates.forEach(affiliate -> {
                affiliate.setCreator(newEmail);
                if (deactivate) {
                    affiliate.setDeactivated(true);
                }
            });
            affiliateRepository.saveAll(affiliates);
        }
    }

    /**
     * Updates the email field in the AffiliateDetails table.
     */
    private void updateAffiliateDetails(String oldEmail, String newEmail) {
        List<AffiliateDetails> affiliateDetailsList = affiliateDetailsRepository.getAllAffiliateDetailsByEmail(oldEmail);
        if (!affiliateDetailsList.isEmpty()) {
            affiliateDetailsList.forEach(details -> details.setEmail(newEmail));
            affiliateDetailsRepository.saveAll(affiliateDetailsList);
        }
    }

    /**
     * Utility method to modify an email (adds "old" before "@").
     */
    private String addOldToEmail(String email) {
        String[] parts = email.split("@");
        if (parts.length == 2) {
            return parts[0] + "old@" + parts[1];
        }
        throw new IllegalArgumentException("Invalid email format: " + email);
    }


    @Scheduled(cron = "0 0 17 * * ?", zone = "Asia/Kolkata")
    public void removePendingApplication() {
        Logger logger = LoggerFactory.getLogger(getClass());
        Instant currentDate = Instant.now();

        // Fetch all applications at once (Avoids multiple DB calls)
        List<Application> applications = applicationRepository.findAll();
        logger.info("Total applications retrieved: {}", applications.size());

        List<Long> filteredAffiliateIds = new ArrayList<>();

        for (Application application : applications) {
            Long memberId = application.getMember().getMemberId();
            Member member = getMemberById(memberId);

            if (member == null) {
                logger.warn("Member not found for application ID: {}", application.getApplicationId());
                continue; // Skip if member not found
            }

            int pendingApplication = member.getPendingApplication();
            if (pendingApplication == 0) {
                logger.info("Skipping application ID {}: PendingApplication is 0", application.getApplicationId());
                continue; // Skip processing if no pending applications
            }

            Instant cutoffDate = getCutoffDate(pendingApplication);
            Instant completedAt = application.getCompletedAt();
            Instant submittedAt = application.getSubmittedAt();
            ApprovalState approvalState = application.getApprovalState();

            // Check if the application meets the criteria
            if ((approvalState == ApprovalState.CHANGE_REQUESTED ||
                approvalState == ApprovalState.NOT_SUBMITTED ||
                approvalState == ApprovalState.REJECTED) &&
                (completedAt == null ? completedAt.isBefore(cutoffDate) : submittedAt.isBefore(cutoffDate))) {

                filteredAffiliateIds.add(application.getAffiliate().getAffiliateId());
            }
        }

        logger.info("Total applications meeting the criteria: {}", filteredAffiliateIds.size());

        // Deactivate affiliates using reusable method
        deactivateAffiliates(filteredAffiliateIds);
    }


    /**
     * Scheduled process to deactivate affiliates whose invoices are in a pending state
     * beyond the defined period for their respective member country.
     *
     * - Fetches all affiliates with pending invoices.
     * - Retrieves the defined invoice pending period for the member.
     * - Identifies affiliates whose invoice pending period has exceeded the cutoff date.
     * - Performs a bulk deactivation for the identified affiliates.
     *
     * This ensures that affiliates who have not cleared their invoices within the allowed timeframe
     * are deactivated automatically, maintaining compliance with membership policies.
     */
    @Scheduled(cron = "0 0 16 * * ?", zone = "Asia/Kolkata")
    public void removeInvoicesPending() {
        Logger logger = LoggerFactory.getLogger(getClass());

        Member member = getMemberById(1L);
        if (member.getInvoicesPending() == 0) {
            logger.info("Skipping processing: PendingApplication is 0 for Member ID 1");
            return;
        }

        Instant cutoffDate = getCutoffDate(member.getInvoicesPending());
        List<Long> affiliateIds = getFilteredAffiliateIds(cutoffDate);

        deactivateAffiliates(affiliateIds);
    }

    /*Get filtered affiliate IDs for Pending Incoices State For IHTSDO members*/
    private List<Long> getFilteredAffiliateIds(Instant cutoffDate) {
        List<Affiliate> affiliates = affiliateRepository.getIHTSDOPendingInvoices();

        return affiliates.stream()
            .filter(affiliate -> affiliate.getStandingState() == StandingState.PENDING_INVOICE
                && affiliate.getCreated().isBefore(cutoffDate))
            .map(Affiliate::getAffiliateId)
            .collect(Collectors.toList());
    }

    /*Fetch member details using the Member Id*/
    private Member getMemberById(Long memberId) {
        return memberRepository.findMemberById(memberId);
    }

    /*Reusable Method to Compute cutoff date based on Standing State , Approval State, Usage Reports*/
    private Instant getCutoffDate(int invoicesPending) {
        return Instant.now().minus(invoicesPending, ChronoUnit.DAYS);
    }

    /*Reusable method for Bulk Deactivate the Affiliates if applicable*/
    private void deactivateAffiliates(List<Long> affiliateIds) {
        Logger logger = LoggerFactory.getLogger(getClass());

        if (!affiliateIds.isEmpty()) {
            int updatedCount = affiliateRepository.bulkDeactivateAffiliates(affiliateIds);
            logger.info("Total affiliates deactivated: {}", updatedCount);
        } else {
            logger.info("No affiliates met the criteria for deactivation.");
        }
    }

    @Scheduled(cron = "0 0 15 * * ?", zone = "Asia/Kolkata")
    public void removeUsageReports() {
        Logger logger = LoggerFactory.getLogger(getClass());

        List<Affiliate> affiliates = affiliateRepository.findAll();
        List<Long> affiliateIdsForDeactivation = new ArrayList<>();

        for (Affiliate affiliate : affiliates) {
            Member member = getMemberById(affiliate.getHomeMember().getMemberId());
            // Step 1: Skip processing if Usage Reports for Member is 0
            if (member.getUsageReports() == 0) {
                logger.info("Skipping processing: Usage Reports is 0 for Member ID {}", affiliate.getHomeMember().getMemberId());
                continue;
            }
            // Step 2: Get commercial usage details
            Set<CommercialUsage> commercialUsages = affiliate.getCommercialUsages();
            if (commercialUsages == null || commercialUsages.isEmpty()) {
                logger.info("Skipping Affiliate ID {}: No CommercialUsage records found.", affiliate.getAffiliateId());
                continue;
            }
            // Step 3: Compute cutoff date
            Instant cutoffDate = getCutoffDate(member.getUsageReports());
            // Step 4: Check conditions on CommercialUsage
            for (CommercialUsage usage : commercialUsages) {
                if (usage.getState().equals(ApprovalState.NOT_SUBMITTED) && usage.getSubmitted() == null) {
                    Instant createdDate = usage.getCreated();
                    if (createdDate.isBefore(cutoffDate)) {
                        affiliateIdsForDeactivation.add(affiliate.getAffiliateId());
                        break; // No need to check further for this affiliate
                    }
                }
            }
        }
        // Step 5: Bulk update affiliates for deactivation
        deactivateAffiliates(affiliateIdsForDeactivation);
    }

}







