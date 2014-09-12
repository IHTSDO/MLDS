package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

public class AuthorizationCheckerTest {
	AuthorizationChecker authorizationChecker;
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	Member ihtsdo;
	Member sweden;
	
	Affiliate ihtsdoAffiliate;
	Affiliate swedenAffiliate;

	@Before
	public void setUp() {
		authorizationChecker = new AuthorizationChecker();
		authorizationChecker.currentSecurityContext = new CurrentSecurityContext();
		sweden = new Member("SE", 1);
		ihtsdo = new Member("IHTSDO", 2);
		
		ihtsdoAffiliate = new Affiliate(1L);
		ihtsdoAffiliate.setHomeMember(ihtsdo);
		swedenAffiliate = new Affiliate(2L);
		swedenAffiliate.setHomeMember(sweden);
	}

	@Test
	public void adminCanManageAffiliate() {
		securityContextSetup.asAdmin();
		
		authorizationChecker.checkCanManageAffiliate(ihtsdoAffiliate);
		authorizationChecker.checkCanManageAffiliate(swedenAffiliate);
	}

	@Test
	public void staffCanManageOwnMemberAffiliate() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanManageAffiliate(ihtsdoAffiliate);
	}

	@Test(expected=IllegalStateException.class)
	public void staffCanNotManageOtherMemberAffiliate() {
		securityContextSetup.asIHTSDOStaff();
		
		authorizationChecker.checkCanManageAffiliate(swedenAffiliate);
	}
	
	@Test(expected=IllegalStateException.class)
	public void usersCanNotManageAffiliate() {
		securityContextSetup.asAffiliateUser();
		
		authorizationChecker.checkCanManageAffiliate(ihtsdoAffiliate);
	}
	
	@Test(expected=IllegalStateException.class)
	public void anonymousCanNotManageAffiliate() {
		securityContextSetup.asAnonymous();
		
		authorizationChecker.checkCanManageAffiliate(ihtsdoAffiliate);
	}

}
