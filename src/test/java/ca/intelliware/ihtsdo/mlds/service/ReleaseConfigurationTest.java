package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.web.rest.ReleasePackageAuthorizationChecker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseConfigurationTest {

    @Mock
    private ReleaseVersionAccessRepository releaseVersionAccessRepository;

    @Mock
    private ReleasePackageConfigRepository releasePackageConfigRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentSecurityContext currentSecurityContext;

    @InjectMocks
    private ReleasePackageAuthorizationChecker authorizationChecker;

    private ReleaseVersion version;
    private ReleasePackage releasePackage;
    private Member member;
    private User user;

    @Before
    public void setup() throws Exception {

        member = new Member("SE", 1);

        releasePackage = new ReleasePackage();
        releasePackage.setMember(member);

        version = new ReleaseVersion();
        version.setReleaseVersionId(100L);

        linkVersionToPackage(version, releasePackage);

        user = new User();
        user.setUserId(1L);
        user.setLogin("user");
    }

    private void linkVersionToPackage(ReleaseVersion version, ReleasePackage releasePackage) throws Exception {

        Set<ReleaseVersion> versions = new HashSet<>();
        versions.add(version);
        releasePackage.setReleaseVersions(versions);

        Field field = ReleaseVersion.class.getDeclaredField("releasePackage");
        field.setAccessible(true);
        field.set(version, releasePackage);
    }

    @Test
    public void shouldAllowAdminAccess() {

        when(currentSecurityContext.isAdmin()).thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }

    @Test
    public void shouldAllowOwnerAccess() {

        when(currentSecurityContext.isStaff()).thenReturn(true);
        when(currentSecurityContext.isStaffFor(member)).thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }


    @Test
    public void shouldAllowEveryonePermission() {

        version.setPermissionType(ReleasePermissionType.EVERYONE);

        when(currentSecurityContext.isUser()).thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }


    @Test
    public void shouldAllowStaffAccess() {

        version.setPermissionType(ReleasePermissionType.ADMIN_AND_STAFF);

        when(currentSecurityContext.isStaff()).thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }

    @Test
    public void shouldAllowAffiliateUser() {

        version.setPermissionType(ReleasePermissionType.ADMIN_STAFF_AFFILIATES);

        when(currentSecurityContext.isUser()).thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }


    @Test
    public void shouldAllowSelectedUser() {

        version.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);

        when(currentSecurityContext.isUser()).thenReturn(true);

        when(currentSecurityContext.getCurrentUserName()).thenReturn("user");
        when(userRepository.findByLoginIgnoreCase("user")).thenReturn(user);

        when(releaseVersionAccessRepository
            .existsByReleaseVersionIdAndUserId(100L, 1L))
            .thenReturn(true);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }

    @Test
    public void shouldDenyNonSelectedUser() {

        version.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);

        when(currentSecurityContext.isUser()).thenReturn(true);
        when(currentSecurityContext.isAdmin()).thenReturn(false);
        when(currentSecurityContext.isStaff()).thenReturn(false);

        when(currentSecurityContext.getCurrentUserName()).thenReturn("user");
        when(userRepository.findByLoginIgnoreCase("user")).thenReturn(user);

        when(releaseVersionAccessRepository
            .existsByReleaseVersionIdAndUserId(100L, 1L))
            .thenReturn(false);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertFalse(result);
    }

    @Test
    public void shouldAllowAnonymousWhenEveryone() {

        version.setPermissionType(ReleasePermissionType.EVERYONE);

        when(currentSecurityContext.isUser()).thenReturn(false);
        when(currentSecurityContext.isAdmin()).thenReturn(false);
        when(currentSecurityContext.isStaff()).thenReturn(false);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertTrue(result);
    }

    @Test
    public void shouldDenyAnonymousWhenNotEveryone() {

        version.setPermissionType(ReleasePermissionType.ADMIN_ONLY);

        when(currentSecurityContext.isUser()).thenReturn(false);
        when(currentSecurityContext.isAdmin()).thenReturn(false);
        when(currentSecurityContext.isStaff()).thenReturn(false);

        boolean result = authorizationChecker.canAccessReleaseVersion(version);

        assertFalse(result);
    }
}
