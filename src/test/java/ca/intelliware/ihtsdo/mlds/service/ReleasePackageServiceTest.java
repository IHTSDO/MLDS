package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReleasePackageServiceTest {

    @Mock
    private ReleasePackageConfigRepository releasePackageConfigRepository;

    @Mock
    private ReleasePackageRepository releasePackageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReleasePackageAccessRepository releasePackageAccessRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentSecurityContext currentSecurityContext;

    @InjectMocks
    private ReleasePackageService releasePackageService;

    @InjectMocks
    private ReleasePackageAccessService releasePackageAccessService;

    private User testUser;
    private ReleasePackage testPackage;
    private ReleasePackageConfig config;
    private ReleasePackageConfig masterConfig;


    @Before
    public void setup() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setLogin("user");

        testPackage = new ReleasePackage();
        testPackage.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);

        config = new ReleasePackageConfig();
        config.setReleasePermissionType("EVERYONE");

        masterConfig = new ReleasePackageConfig();
        masterConfig.setReleasePermissionType("ADMIN_ONLY");
    }

    @Test
    public void shouldUpdateReleaseMasterConfigSuccessfully() throws JsonProcessingException {

        Map<String, Object> request = new HashMap<>();
        request.put("releaseType", "ONLINE");
        request.put("releasePermissionType", "ADMIN_STAFF_SELECTED_USERS");
        List<String> users = List.of("101", "102");
        request.put("users", users);

        String userListJson = "[\"101\",\"102\"]";

        ReleasePackageConfig existingConfig = new ReleasePackageConfig();
        existingConfig.setReleaseType("ONLINE");

        when(releasePackageConfigRepository.findByReleaseType("ONLINE")).thenReturn(existingConfig);
        when(objectMapper.writeValueAsString(users)).thenReturn(userListJson);

        releasePackageService.updateReleaseMasterConfig(request);

        assertEquals("ADMIN_STAFF_SELECTED_USERS", existingConfig.getReleasePermissionType());
        assertEquals(userListJson, existingConfig.getUserList());
        assertTrue(existingConfig.getActive());

        verify(releasePackageConfigRepository).save(existingConfig);
        verify(releasePackageRepository).findAll();
    }

    @Test
    public void shouldUpdatePermissionTypeForEachReleasePackage() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("releasePackageType", "ADMIN_ONLY");
        List<Integer> releases = List.of(1);
        requestBody.put("releases", releases);

        ReleasePackage rp1 = new ReleasePackage();
        rp1.setPermissionType(ReleasePermissionType.ADMIN_ONLY);

        when(releasePackageRepository.findAllByReleasePackageIdIn(List.of(1L))).thenReturn(List.of(rp1));

        releasePackageService.updateReleasePackagesPermission(requestBody);

        assertEquals(ReleasePermissionType.ADMIN_ONLY, rp1.getPermissionType());
    }

    @Test
    public void shouldReturnMasterPermissionVisibilityDetails_WhenMasterConfigIsActive() {
        ReleaseVersion releaseVersion = new ReleaseVersion();
        releaseVersion.setReleaseType("online");
        releaseVersion.setArchive(false);

        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);
        releasePackage.setReleaseVersions(Set.of(releaseVersion));

        ReleasePackageConfig config = new ReleasePackageConfig();
        config.setReleaseType("online");
        config.setReleasePermissionType("ADMIN_STAFF");
        config.setActive(true);

        when(releasePackageConfigRepository.findByReleaseTypeIgnoreCase("online")).thenReturn(config);

        PermissionVisibilityResponse response = releasePackageService.getVisibilityDetails(releasePackage);

        assertTrue(response.isMasterPermission());
        assertEquals("ADMIN_STAFF", response.getPermissionType());
        assertEquals("online", response.getReleaseType());
    }

    @Test
    public void shouldReturnPackageLevelPermission_WhenMasterConfigIsInactive() {
        ReleaseVersion releaseVersion = new ReleaseVersion();
        releaseVersion.setReleaseType("offline");
        releaseVersion.setArchive(false);

        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setPermissionType(ReleasePermissionType.NOT_SELECTED);
        releasePackage.setReleaseVersions(Set.of(releaseVersion));

        ReleasePackageConfig config = new ReleasePackageConfig();
        config.setReleaseType("offline");
        config.setReleasePermissionType("ADMIN_STAFF");
        config.setActive(false);

        when(releasePackageConfigRepository.findByReleaseTypeIgnoreCase("offline")).thenReturn(config);

        PermissionVisibilityResponse response = releasePackageService.getVisibilityDetails(releasePackage);

        assertFalse(response.isMasterPermission());
        assertEquals("NOT_SELECTED", response.getPermissionType());
        assertEquals("offline", response.getReleaseType());
    }

    @Test
    public void shouldReturnPackagesForStaffWithProperPermissions() {
        // Given
        when(currentSecurityContext.isStaff()).thenReturn(true);

        ReleasePackage p1 = new ReleasePackage();
        p1.setMember(new Member("SE", 1));
        p1.setPermissionType(ReleasePermissionType.EVERYONE);

        ReleasePackage p2 = new ReleasePackage();
        p2.setMember(new Member("IN", 2));
        p2.setPermissionType(ReleasePermissionType.EVERYONE);

        List<ReleasePackage> all = List.of(p1, p2);
        List<ReleasePackage> online = List.of(p1);

        ReleasePackageConfig grantedConfig = new ReleasePackageConfig();
        grantedConfig.setReleaseType("ONLINE");
        grantedConfig.setReleasePackageAccess("SE");
        grantedConfig.setReleasePermissionType("EVERYONE");
        grantedConfig.setUserList("[\"user1\"]");
        grantedConfig.setActive(true);

        ReleasePackageConfig notGrantedConfig = new ReleasePackageConfig();
        notGrantedConfig.setReleaseType("ONLINE");
        notGrantedConfig.setReleasePackageAccess("IN");
        notGrantedConfig.setReleasePermissionType("NOT_SELECTED");
        notGrantedConfig.setUserList("[]");
        notGrantedConfig.setActive(true);

        List<ReleasePackageConfig> granted = List.of(grantedConfig);
        List<ReleasePackageConfig> notGranted = List.of(notGrantedConfig);

        when(releasePackageConfigRepository.findByReleasePermissionTypeNot("NOT_SELECTED")).thenReturn(granted);
        when(releasePackageConfigRepository.findByReleasePermissionType("NOT_SELECTED")).thenReturn(notGranted);

        var result = releasePackageAccessService.getAccessiblePackagesForStaff(all, online, List.of(), List.of(), "SE");

        assertEquals(1, result.size());
        assertTrue(result.contains(p1));
    }

    @Test
    public void shouldReturnTrueWhenPermissionTypeIsAdminOnly() {
        testPackage.setPermissionType(ReleasePermissionType.ADMIN_ONLY);
        assertTrue(releasePackageAccessService.isAdminOnly(testPackage, config, masterConfig));
    }


    @Test
    public void shouldAllowAccessToSelectedUser() {
        testPackage.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);

        when(currentSecurityContext.getCurrentUserName()).thenReturn("user");
        when(userRepository.findByLoginIgnoreCase("user")).thenReturn(testUser);
        when(releasePackageAccessRepository.existsByReleasePackageIdAndUserId(100L, 1L)).thenReturn(true);

        var result = releasePackageAccessService.handleUserAccess(testPackage, config, masterConfig, 100L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldDenyAccessToNonSelectedUser() {
        testPackage.setPermissionType(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS);

        when(currentSecurityContext.getCurrentUserName()).thenReturn("user");
        when(userRepository.findByLoginIgnoreCase("user")).thenReturn(testUser);
        when(releasePackageAccessRepository.existsByReleasePackageIdAndUserId(100L, 1L)).thenReturn(false);

        var result = releasePackageAccessService.handleUserAccess(testPackage, config, masterConfig, 100L);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

}
