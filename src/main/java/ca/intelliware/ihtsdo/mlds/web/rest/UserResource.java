package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
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

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    UserRepository userRepository;

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



}


