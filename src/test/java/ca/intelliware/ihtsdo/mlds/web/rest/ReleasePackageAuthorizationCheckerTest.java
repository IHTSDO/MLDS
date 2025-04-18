package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReleasePackageAuthorizationCheckerTest {

	@Mock
	private UserMembershipAccessor userMembershipAccessor;

	@Mock
	private UserStandingCalculator userStandingCalculator;

	ReleasePackageAuthorizationChecker authorizationChecker;
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	ReleasePackage ihtsdoReleasePackage;
	ReleasePackage swedenReleasePackage;
	ReleaseVersion offlineReleaseVersion;
	ReleaseVersion onlineReleaseVersion;
	Member ihtsdo;
	Member sweden;

	@Before
	public void setUp() {
		authorizationChecker = new ReleasePackageAuthorizationChecker();
		authorizationChecker.setCurrentSecurityContext(new CurrentSecurityContext());
		authorizationChecker.userMembershipAccessor = userMembershipAccessor;
		authorizationChecker.userStandingCalculator = userStandingCalculator;

		sweden = new Member("SE", 1);
		ihtsdo = new Member("IHTSDO", 2);

		ihtsdoReleasePackage = new ReleasePackage();
		ihtsdoReleasePackage.setMember(ihtsdo);
		swedenReleasePackage = new ReleasePackage();
		swedenReleasePackage.setMember(sweden);
		offlineReleaseVersion = new ReleaseVersion();
//		offlineReleaseVersion.setOnline(false);
        offlineReleaseVersion.setReleaseType("offline");
		onlineReleaseVersion = new ReleaseVersion();
		onlineReleaseVersion.setOnline(true);
	}

	@Test
	public void onlyMemberAndStaffAndAdminCanSeeOfflinePackages() {
		securityContextSetup.asAdmin();
		assertTrue("Admin should see offline packages", authorizationChecker.shouldSeeOfflinePackages());

		securityContextSetup.asIHTSDOStaff();
		assertTrue("Staff should see offline packages", authorizationChecker.shouldSeeOfflinePackages());

		securityContextSetup.asIHTSDOMember();
		assertTrue("Member should see offline packages", authorizationChecker.shouldSeeOfflinePackages());

		securityContextSetup.asAffiliateUser();
		assertFalse("Users should not see offline packages", authorizationChecker.shouldSeeOfflinePackages());

		securityContextSetup.asAnonymous();
		assertFalse("Public should not see offline packages", authorizationChecker.shouldSeeOfflinePackages());
	}

	@Test
	public void adminCanCreatePackages() {
		securityContextSetup.asAdmin();

		authorizationChecker.checkCanCreateReleasePackages();
	}

	@Test
	public void staffCanCreatePackages() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanCreateReleasePackages();
	}

	@Test(expected=IllegalStateException.class)
	public void userCanNotCreatePackages() {
		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanCreateReleasePackages();
	}

	@Test(expected=IllegalStateException.class)
	public void anonymousCanNotCreatePackages() {
		securityContextSetup.asAnonymous();

		authorizationChecker.checkCanCreateReleasePackages();
	}

	@Test
	public void adminCanEditPackages() {
		securityContextSetup.asAdmin();

		authorizationChecker.checkCanEditReleasePackage(ihtsdoReleasePackage);
		authorizationChecker.checkCanEditReleasePackage(swedenReleasePackage);
	}

	@Test
	public void staffCanEditOwnMemberPackages() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanEditReleasePackage(ihtsdoReleasePackage);
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotEditOtherMemberPackages() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanEditReleasePackage(swedenReleasePackage);
	}

	@Test(expected=IllegalStateException.class)
	public void userCanNotEditPackages() {
		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanEditReleasePackage(ihtsdoReleasePackage);
	}


	@Test
	public void staffCanViewOfflinePackageVersion() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanAccessReleaseVersion(offlineReleaseVersion);
	}

	@Test
	public void AdminCanDownloadPackageVersion() {
		securityContextSetup.asAdmin();

		authorizationChecker.checkCanDownloadReleaseVersion(offlineReleaseVersion);
	}

	@Test
	public void staffCanDownloadPackageVersion() {
		securityContextSetup.asIHTSDOStaff();

		authorizationChecker.checkCanDownloadReleaseVersion(offlineReleaseVersion);
	}

	@Test
	public void memberCanDownloadIhtsdoPackageVersion() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

		securityContextSetup.asIHTSDOMember();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	@Test(expected=IllegalStateException.class)
	public void memberCannotDownloadOtherPackageVersion() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();
		onlineIhtsdoVersion.getReleasePackage().setMember(sweden);

		securityContextSetup.asIHTSDOMember();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	@Test
	public void userCanDownloadApprovedPackageVersion() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

		Mockito.when(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo)).thenReturn(true);

		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	private ReleaseVersion withOnlineIhtsdoReleasePackageVersion() {
		ReleasePackage releasePackage = new ReleasePackage(1L);
		releasePackage.setMember(ihtsdo);
		ReleaseVersion onlineIhtsdoVersion = new ReleaseVersion(2L);
		releasePackage.addReleaseVersion(onlineIhtsdoVersion);
		onlineIhtsdoVersion.setOnline(true);
        onlineIhtsdoVersion.setReleaseType("online");
		return onlineIhtsdoVersion;
	}

	@Test(expected=IllegalStateException.class)
	public void userCannotDownloadUnapprovedPackageVersion() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

		Mockito.when(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo)).thenReturn(false);

		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	@Test(expected=IllegalStateException.class)
	public void userCannotDownloadApprovedPackageVersionWhenAccountDeactivated() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

//		Mockito.when(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo)).thenReturn(true);

		Mockito.when(userStandingCalculator.isLoggedInUserAffiliateDeactivated()).thenReturn(true);

		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	@Test(expected=IllegalStateException.class)
	public void userCannotDownloadApprovedPackageVersionWhenAccountDeregistered() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

//		Mockito.when(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo)).thenReturn(true);

		Mockito.when(userStandingCalculator.isLoggedInUserAffiliateDeregistered()).thenReturn(true);

		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}

	@Test(expected=IllegalStateException.class)
	public void userCannotDownloadApprovedPackageVersionWhenAccountPendingInvoice() {
		ReleaseVersion onlineIhtsdoVersion = withOnlineIhtsdoReleasePackageVersion();

//		Mockito.when(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo)).thenReturn(true);

		Mockito.when(userStandingCalculator.isLoggedInUserAffiliatePendingInvoice()).thenReturn(true);

		securityContextSetup.asAffiliateUser();

		authorizationChecker.checkCanDownloadReleaseVersion(onlineIhtsdoVersion);
	}
}
