package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.AnnouncementEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;

import com.codahale.metrics.annotation.Timed;

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
    	
		auditEventService.createAuditEvent(EVENT_ANNOUNCEMENT_POSTED, auditData );
	}

	private void sendAnnouncementEmails(AnnouncementDTO announcement) {
		Member member = announcement.getMember();
		for (User user : userMembershipCalculator.acceptedUsers(member)) {
			announcementEmailSender.sendAnnouncementEmail(user, member, announcement.getSubject(), announcement.getBody());
		}
		if (announcement.getAdditionalEmails() != null) {
			for (String email : announcement.getAdditionalEmails()) {
				announcementEmailSender.sendAnnouncementEmail(email, member, announcement.getSubject(), announcement.getBody());
			}
		}
	}

}
