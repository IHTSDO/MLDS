package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;

@Service
public class AnnouncementAuthorizationChecker extends AuthorizationChecker {

	public void checkCanPostAnnouncement(AnnouncementDTO announcement) {
		if (currentSecurityContext.isAdmin() 
				|| currentSecurityContext.isStaffFor(announcement.getMember())) {
			return;
		}
		failCheck("User not authorized to post announcement.");
	}
}
