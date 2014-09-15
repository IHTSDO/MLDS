package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

public class AuthorityConverterTest {
	AuthorityConverter authorityConverter = new AuthorityConverter();
	
	CentralAuthUserPermission remoteAdminPermission = new CentralAuthUserPermission("MLDS", AuthorityConverter.REMOTE_ROLE_ADMIN, AuthorityConverter.REMOTE_MEMBER_IHTSDO);
	CentralAuthUserPermission remoteStaffPermission = new CentralAuthUserPermission("MLDS", AuthorityConverter.REMOTE_ROLE_STAFF, AuthorityConverter.REMOTE_MEMBER_IHTSDO);
	CentralAuthUserPermission remoteSwedenStaffPermission = new CentralAuthUserPermission("MLDS", AuthorityConverter.REMOTE_ROLE_STAFF, "SE");
	
	GrantedAuthority adminAuthority = new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN);
	GrantedAuthority staffAuthority = new SimpleGrantedAuthority(AuthoritiesConstants.STAFF);
	GrantedAuthority staffIHTSDOAuthority = new SimpleGrantedAuthority(AuthoritiesConstants.staffRoleForMember("IHTSDO"));
	GrantedAuthority staffSwedenAuthority = new SimpleGrantedAuthority(AuthoritiesConstants.staffRoleForMember("SE"));

	@Test
	public void convertAdminToAdmin() throws Exception {
		
		List<GrantedAuthority> authoritiesList = authorityConverter.buildAuthoritiesList(Arrays.asList(remoteAdminPermission));
		
		assertThat(authoritiesList, contains(adminAuthority));
	}
	
	@Test
	public void convertStaffToStaffAndStaffForIHTSDOMember() throws Exception {
		
		List<GrantedAuthority> authoritiesList = authorityConverter.buildAuthoritiesList(Arrays.asList(remoteStaffPermission));
		
		assertThat(authoritiesList, contains(staffAuthority,staffIHTSDOAuthority));
	}

	@Test
	public void convertSwedenStaffToStaffAndStaffForSwedenMember() throws Exception {
		
		List<GrantedAuthority> authoritiesList = authorityConverter.buildAuthoritiesList(Arrays.asList(remoteSwedenStaffPermission));
		
		assertThat(authoritiesList, contains(staffAuthority,staffSwedenAuthority));
	}
}
