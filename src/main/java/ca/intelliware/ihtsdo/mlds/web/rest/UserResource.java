package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.UserService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateDetailsResponseDTO;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    UserRepository userRepository;
    @Autowired AffiliateRepository affiliateRepository;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody Iterable<User> getUsers() {
        log.debug("Rest request to get all Users");
        return userRepository.findAll();
    }

    /**
     * GET  /rest/users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/users/{login}",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    public User getUser(@PathVariable String login, HttpServletResponse response) {
        log.debug("REST request to get User : {}", login);
        User user = userRepository.findByLoginIgnoreCase(login);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return user;
    }

    @RequestMapping(value = "/getUserDetails", method = RequestMethod.POST)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<AffiliateDetailsResponseDTO> getUserDetails(@RequestParam String login, @RequestParam Long affiliateDetailsId) {
        log.debug("REST request to get User : {}", login);

        AffiliateDetailsResponseDTO responseDTO = userService.getAffiliateDetails(login, affiliateDetailsId);

        // If no affiliate details are found, return an empty DTO with HTTP 200
        if (responseDTO.getAffiliateDetails() == null && responseDTO.getAffiliate().isEmpty()) {
            return ResponseEntity.ok(responseDTO);
        }

        return ResponseEntity.ok(responseDTO);
    }


    @RequestMapping(value = "/updatePrimaryEmail", method = RequestMethod.POST)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> updatePrimaryEmail(@RequestParam String login, @RequestParam String updatedEmail) {
        try {

            userService.updatePrimaryEmail(login, updatedEmail);
            return ResponseEntity.ok("Primary email updated successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or related data not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the email");
        }
    }
    @RequestMapping(value = "/testRun", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<String> testRun() {
        try {

            userService.removeUsageReports();
            return ResponseEntity.ok("Pendind Applications Removed successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or related data not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the Pendind Applications");
        }
    }

    @PostMapping("/unsubscribenotification/{affiliateId}/{key}")
    public ResponseEntity<String> unsubscribeOnce(
        @PathVariable Long affiliateId,
        @PathVariable String key) {

        // Step 1: Get the Affiliate using the affiliateId
        Optional<Affiliate> affiliateOpt = affiliateRepository.findById(affiliateId);

        if (affiliateOpt.isPresent()) {
            Affiliate affiliate = affiliateOpt.get();

            // Step 2: Get the creator from the Affiliate
            String creatorLogin = affiliate.getCreator();

            // Step 3: Fetch the User by matching the creator's login (no need for Optional here)
            User user = userRepository.findByLoginIgnoreCase(creatorLogin);

            if (user != null) {
                // Step 4: Check if the key matches
                if (!key.equals(user.getUnsubscribeKey())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired unsubscribe link.");
                }

                // Step 5: If already unsubscribed, show a message
                if (!user.getAcceptNotifications()) {
                    return ResponseEntity.status(HttpStatus.GONE).body("This unsubscribe link has already been used.");
                }

                // Step 6: Unsubscribe the user by setting acceptNotifications to false
                user.setAcceptNotifications(false);

                // Step 7: Invalidate the unsubscribe key by generating a new one
                user.setUnsubscribeKey(UUID.randomUUID().toString());
                userRepository.save(user);

                return ResponseEntity.ok("You have successfully unsubscribed.");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Affiliate not found.");
    }
    @GetMapping("/usersList")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<List<Map<String, Object>>> getUsersWithAuthorities() {

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> userLogins = users.stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("login", user.getLogin());
                return map;
            })
            .toList();

        return ResponseEntity.ok(userLogins);
    }
}


