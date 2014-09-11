package ca.intelliware.ihtsdo.mlds.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationResource.CreateApplicationDTO;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationAuthorizationCheckerTest {
	
	ApplicationAuthorizationChecker authorizationChecker;
	
	@Mock UserStandingCalculator userStandingCalculator;
	
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	
	Member ihtsdo;
	Member sweden;
	
	Application ihtsdoApplication;
	Application swedenApplication;
	
	CreateApplicationDTO ihtsdoCreateExtensionApplication;
	CreateApplicationDTO swedenCreateExtensionApplication;

	@Before
	public void setUp() {
		authorizationChecker = new ApplicationAuthorizationChecker();
		authorizationChecker.userStandingCalculator = userStandingCalculator;
		
		authorizationChecker.currentSecurityContext = new CurrentSecurityContext();
		sweden = new Member("SE", 1);
		ihtsdo = new Member("IHTSDO", 2);
		
		ihtsdoApplication = new PrimaryApplication();
		ihtsdoApplication.setMember(ihtsdo);
		swedenApplication = new PrimaryApplication();
		swedenApplication.setMember(sweden);
		
		ihtsdoCreateExtensionApplication = new CreateApplicationDTO();
		ihtsdoCreateExtensionApplication.setApplicationType(ApplicationType.EXTENSION);
		ihtsdoCreateExtensionApplication.setMemberKey(ihtsdo.getKey());
		swedenCreateExtensionApplication = new CreateApplicationDTO();
		swedenCreateExtensionApplication.setApplicationType(ApplicationType.EXTENSION);
		swedenCreateExtensionApplication.setMemberKey(sweden.getKey());
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
	
	
	@Test
	public void adminCanCreateApplication() {
		securityContextSetup.asAdmin();
		
		authorizationChecker.checkCanCreateApplication(ihtsdoCreateExtensionApplication);
		authorizationChecker.checkCanCreateApplication(swedenCreateExtensionApplication);
	}

	@Test
	public void staffCanCreateOwnMemberApplication() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanCreateApplication(ihtsdoCreateExtensionApplication);
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotCreateOtherMemberApplication() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanCreateApplication(swedenCreateExtensionApplication);
	}

	@Test
	public void userCanCreateOwnMemberApplication() {
		securityContextSetup.asAffiliateUser();

		Mockito.when(userStandingCalculator.isLoggedInUserAffiliateDeactivated()).thenReturn(false);
		
		authorizationChecker.checkCanCreateApplication(swedenCreateExtensionApplication);
	}

	@Test(expected=IllegalStateException.class)
	public void deactivatedUserCanNotCreateApplication() {
		securityContextSetup.asAffiliateUser();
		
		Mockito.when(userStandingCalculator.isLoggedInUserAffiliateDeactivated()).thenReturn(true);
		
		authorizationChecker.checkCanCreateApplication(swedenCreateExtensionApplication);
	}
}
