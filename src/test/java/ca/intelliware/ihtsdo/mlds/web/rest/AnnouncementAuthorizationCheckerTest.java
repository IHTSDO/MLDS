package ca.intelliware.ihtsdo.mlds.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;


import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnnouncementAuthorizationCheckerTest {

	AnnouncementAuthorizationChecker authorizationChecker;

	@Mock UserStandingCalculator userStandingCalculator;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

	Member ihtsdo;
	Member sweden;

	@Before
	public void setUp() {
		authorizationChecker = new AnnouncementAuthorizationChecker();

		authorizationChecker.setCurrentSecurityContext(new CurrentSecurityContext());
		sweden = new Member("SE", 1);
		ihtsdo = new Member("IHTSDO", 2);
	}

	@Test
	public void adminCanPostAnnouncementForAnyMember() {
		securityContextSetup.asAdmin();

		authorizationChecker.checkCanPostAnnouncement(announcement(ihtsdo));
		authorizationChecker.checkCanPostAnnouncement(announcement(sweden));
	}

	@Test
	public void adminCanPostAnnouncementForAllAffiliates() {
		securityContextSetup.asAdmin();

		authorizationChecker.checkCanPostAnnouncement(announcementForAllAffiliates(ihtsdo));
	}

	private AnnouncementDTO announcementForAllAffiliates(Member member) {
		AnnouncementDTO announcement = announcement(member);
		announcement.setAllAffiliates(true);
		return announcement;
	}

	@Test
	public void staffCanPostAnnouncementForOwnMember() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanPostAnnouncement(announcement(ihtsdo));
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotPostAnnouncementForOtherMember() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanPostAnnouncement(announcement(sweden));
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotPostAnnouncementForAllAffiliates() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanPostAnnouncement(announcementForAllAffiliates(sweden));
	}

	@Test(expected=IllegalStateException.class)
	public void usersCanNotPostAnnouncements() {
		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanPostAnnouncement(announcement(ihtsdo));
	}

	@Test(expected=NullPointerException.class)
	public void canNotPostAnnouncementsForMissingMember() {
		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanPostAnnouncement(announcement(null));
	}

	private AnnouncementDTO announcement(Member member) {
		AnnouncementDTO announcement = new AnnouncementDTO();
		announcement.setMember(member);
		return announcement;
	}
}
