package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.AnnouncementEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AnnouncementResource {

	private static final String EVENT_ANNOUNCEMENT_POSTED = "ANNOUNCEMENT_POSTED";

	@Autowired
	AnnouncementAuthorizationChecker announcementAuthorizationChecker;
	@Autowired
    AnnouncementEmailSender announcementEmailSender;
	@Autowired
    UserMembershipCalculator userMembershipCalculator;
	@Autowired AuditEventService auditEventService;

	@RequestMapping(value = Routes.ANNOUNCEMENTS,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<AnnouncementDTO> postAnnouncement(@RequestBody AnnouncementDTO announcement) {

		announcementAuthorizationChecker.checkCanPostAnnouncement(announcement);

		sendAnnouncementEmails(announcement);

    	logAnnouncement(announcement);

    	return new ResponseEntity<AnnouncementDTO>(announcement, HttpStatus.OK);
    }

	private void logAnnouncement(AnnouncementDTO announcement) {
		Map<String, String> auditData = new HashMap<String,String>();
    	auditData.put("announcement.member", announcement.getMember().getKey());
    	auditData.put("announcement.title", announcement.getSubject());
    	if (announcement.isAllAffiliates()) {
    		auditData.put("announcement.allAffiliates", "All");
    	}

		auditEventService.logAuditableEvent(EVENT_ANNOUNCEMENT_POSTED, auditData);
	}

	private void sendAnnouncementEmails(AnnouncementDTO announcement) {
		sendEmailsForAffiliates(announcement);
		sendEmailsForAdditionalEmailAddresses(announcement);
	}

	private void sendEmailsForAdditionalEmailAddresses(AnnouncementDTO announcement) {
		if (announcement.getAdditionalEmails() != null) {
			for (String email : announcement.getAdditionalEmails()) {
				if (StringUtils.isNotBlank(email)) {
					announcementEmailSender.sendAnnouncementEmail(email, announcement.getMember(), announcement.getSubject(), announcement.getBody());
				}
			}
		}
	}

	private void sendEmailsForAffiliates(AnnouncementDTO announcement) {
		for (User user : matchingAffiliateUsers(announcement)) {
			announcementEmailSender.sendAnnouncementEmail(user.getEmail(), announcement.getMember(), announcement.getSubject(), announcement.getBody());
		}
	}

	private Iterable<User> matchingAffiliateUsers(AnnouncementDTO announcement) {
		if (announcement.isAllAffiliates()) {
			return userMembershipCalculator.approvedActiveUsers();
		} else {
			return userMembershipCalculator.approvedActiveUsersWithHomeMembership(announcement.getMember());
		}
	}

}
