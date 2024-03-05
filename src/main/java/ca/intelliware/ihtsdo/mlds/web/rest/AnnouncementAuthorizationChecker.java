package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementAuthorizationChecker extends AuthorizationChecker {

	public void checkCanPostAnnouncement(AnnouncementDTO announcement) {
		if (currentSecurityContext.isAdmin()
				|| (currentSecurityContext.isStaffFor(announcement.getMember()) && !announcement.isAllAffiliates())) {
			return;
		}
		failCheck("User not authorized to post announcement.");
	}
}
