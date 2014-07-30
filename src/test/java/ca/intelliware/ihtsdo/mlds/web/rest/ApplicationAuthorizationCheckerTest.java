package ca.intelliware.ihtsdo.mlds.web.rest;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

public class ApplicationAuthorizationCheckerTest {
	ApplicationAuthorizationChecker authorizationChecker;
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	Member ihtsdo;
	Member sweden;
	
	Application ihtsdoApplication;
	Application swedenApplication;

	@Before
	public void setUp() {
		authorizationChecker = new ApplicationAuthorizationChecker();
		authorizationChecker.currentSecurityContext = new CurrentSecurityContext();
		sweden = new Member("SE");
		ihtsdo = new Member("IHTSDO");
		
		ihtsdoApplication = new Application();
		ihtsdoApplication.setMember(ihtsdo);
		swedenApplication = new Application();
		swedenApplication.setMember(sweden);
	}
	
	@Test
	public void adminCanAccessApplication() {
		securityContextSetup.asAdmin();
		
		authorizationChecker.checkCanAccessApplication(ihtsdoApplication);
		authorizationChecker.checkCanAccessApplication(swedenApplication);
	}

	@Test
	public void staffCanAccessApplication() {
		securityContextSetup.asAdmin();
		
		authorizationChecker.checkCanAccessApplication(ihtsdoApplication);
		authorizationChecker.checkCanAccessApplication(swedenApplication);
	}

	@Test
	public void userCanAccessOwnApplication() {
		securityContextSetup.asAffiliateUser();

		ihtsdoApplication.setUsername(SecurityContextSetup.USERNAME);
		
		authorizationChecker.checkCanAccessApplication(ihtsdoApplication);
	}

	@Test(expected=IllegalStateException.class)
	public void userCanNotAccessOtherUsersApplication() {
		securityContextSetup.asAffiliateUser();

		ihtsdoApplication.setUsername("OTHER_USER");
		
		authorizationChecker.checkCanAccessApplication(ihtsdoApplication);
	}

	@Test(expected=IllegalStateException.class)
	public void anonymousCanNotAccessApplication() {
		securityContextSetup.asAnonymous();
		
		authorizationChecker.checkCanAccessApplication(ihtsdoApplication);
	}
	
	
	@Test
	public void adminCanApproveApplication() {
		securityContextSetup.asAdmin();
		
		authorizationChecker.checkCanApproveApplication(ihtsdoApplication);
		authorizationChecker.checkCanApproveApplication(swedenApplication);
	}

	@Test
	public void staffCanApproveOwnMemberApplication() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanApproveApplication(ihtsdoApplication);
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotApproveOtherMemberApplication() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanApproveApplication(swedenApplication);
	}
	
	@Test(expected=IllegalStateException.class)
	public void usersCanNotApproveApplication() {
		securityContextSetup.asAffiliateUser();
		
		authorizationChecker.checkCanApproveApplication(ihtsdoApplication);
	}
	
	@Test(expected=IllegalStateException.class)
	public void anonymousCanNotApproveApplication() {
		securityContextSetup.asAnonymous();
		
		authorizationChecker.checkCanApproveApplication(ihtsdoApplication);
	}
}
