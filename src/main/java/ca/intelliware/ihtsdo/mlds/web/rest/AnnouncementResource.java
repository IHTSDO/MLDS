package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.AnnouncementEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;

@RestController
public class AnnouncementResource {

	private static final String EVENT_ANNOUNCEMENT_POSTED = "ANNOUNCEMENT_POSTED";
	
	@Resource AnnouncementAuthorizationChecker announcementAuthorizationChecker;
	@Resource AnnouncementEmailSender announcementEmailSender;
	@Resource UserMembershipCalculator userMembershipCalculator;
	@Resource AuditEventService auditEventService;
	
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
