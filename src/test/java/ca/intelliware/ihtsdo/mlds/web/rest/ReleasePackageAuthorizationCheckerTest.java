package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

public class ReleasePackageAuthorizationCheckerTest {
	ReleasePackageAuthorizationChecker authorizationChecker;
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	private ReleasePackage ihtsdoReleasePackage;
	private ReleasePackage swedenReleasePackage;
	private ReleaseVersion offlineReleaseVersion;
	private ReleaseVersion onlineReleaseVersion;
	
	@Before
	public void setUp() {
		authorizationChecker = new ReleasePackageAuthorizationChecker();
		authorizationChecker.currentSecurityContext = new CurrentSecurityContext();
		ihtsdoReleasePackage = new ReleasePackage();
		swedenReleasePackage = new ReleasePackage();
		offlineReleaseVersion = new ReleaseVersion();
		offlineReleaseVersion.setOnline(false);
		onlineReleaseVersion = new ReleaseVersion();
		onlineReleaseVersion.setOnline(true);
	}

	@Test
	public void onlyStaffAndAdminCanSeeOfflinePackages() {
		securityContextSetup.asAdmin();
		assertTrue("Admin should see offline packages", authorizationChecker.shouldSeeOfflinePackages());
		
		securityContextSetup.asIHTSDOStaff();
		assertTrue("Staff should see offline packages", authorizationChecker.shouldSeeOfflinePackages());
		
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
	
	// FIXME MLDS-372 ReleasePackage needs member to test this
	//@Test(expected=IllegalStateException.class)
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

}
