package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReleasePackageAuthorizationChecker extends AuthorizationChecker {

    @Resource
    UserMembershipAccessor userMembershipAccessor;
    @Resource
    UserStandingCalculator userStandingCalculator;

    @Resource
    ReleaseVersionAccessRepository releaseVersionAccessRepository;

    @Resource
    ReleasePackageConfigRepository releasePackageConfigRepository;

    @Resource
    UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void checkCanCreateReleasePackages() {
        if (isStaffOrAdmin()) {
            return;
        }
        failCheck("User not authorized to access release packages.");
    }

    public void checkCanEditReleasePackage(ReleasePackage releasePackage) {
        if (currentSecurityContext.isAdmin()
            || currentSecurityContext.isStaffFor(releasePackage.getMember())) {
            return;
        }
        failCheck("User not authorized to access release packages.");
    }

    boolean shouldSeeOfflinePackages() {
        return currentSecurityContext.isMemberOrStaffOrAdmin();
    }

    boolean shouldSeeAlphaBetaPackages() {
        return currentSecurityContext.isUser();
    }

    public void checkCanAccessReleaseVersion(ReleaseVersion releaseVersion) {
        if (releaseVersion.getReleaseType().equalsIgnoreCase("online") || shouldSeeOfflinePackages()) {
            return;
        }
        failCheck("User not authorized to access offline release version.");
    }

    public void checkCanDownloadReleaseVersion(ReleaseVersion releaseVersion) {

        if (isStaffOrAdmin()) {
            return;
        }

        boolean isIhtsdoMember =
            isMember() &&
                Objects.equals(
                    releaseVersion.getReleasePackage().getMember().getKey(),
                    Member.KEY_IHTSDO
                );

        boolean isAllowedAffiliate =
            !userStandingCalculator.isLoggedInUserAffiliateDeactivated()
                && !userStandingCalculator.isLoggedInUserAffiliateDeregistered()
                && !userStandingCalculator.isLoggedInUserAffiliatePendingInvoice()
                && userMembershipAccessor.isAffiliateMemberApplicationAccepted(
                releaseVersion.getReleasePackage().getMember()
            );

        if (isIhtsdoMember || isAllowedAffiliate) {
            return;
        }

        failDownloadCheck("User not authorized to download release version.");
    }


    private User getCurrentUser() {
        String username = currentSecurityContext.getCurrentUserName();
        return username != null
            ? userRepository.findByLoginIgnoreCase(username)
            : null;
    }

    private static class PermissionContext {
        ReleasePermissionType permission;
        ReleasePackageConfig config;

        public PermissionContext(
            ReleasePermissionType permission,
            ReleasePackageConfig config) {
            this.permission = permission;
            this.config = config;
        }
    }

    private boolean isUserInAllowedList(String jsonUserList, User user) {

        if (jsonUserList == null || jsonUserList.isBlank() || user == null) {
            return false;
        }

        try {
            List<Long> allowedUsers =
                objectMapper.readValue(jsonUserList, new TypeReference<List<Long>>() {});

            return allowedUsers.contains(user.getUserId());

        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateAccess(
        ReleaseVersion version,
        User user,
        boolean isStaffOrMember,
        ReleasePackageConfig moduleConfig,
        ReleasePackageConfig masterConfig
    ) {


        if (version.getPermissionType() != null &&
            version.getPermissionType() != ReleasePermissionType.NOT_SELECTED) {

            return hasAccess(
                new PermissionContext(version.getPermissionType(), null),
                version,
                user,
                isStaffOrMember
            );
        }


        if (moduleConfig != null && Boolean.TRUE.equals(moduleConfig.getActive())) {

            ReleasePermissionType modulePerm =
                safeEnum(moduleConfig.getReleasePermissionType());

            if (modulePerm != null) {
                return hasAccess(
                    new PermissionContext(modulePerm, moduleConfig),
                    version,
                    user,
                    isStaffOrMember
                );
            }
        }


        if (masterConfig != null && Boolean.TRUE.equals(masterConfig.getActive())) {

            ReleasePermissionType masterPerm =
                safeEnum(masterConfig.getReleasePermissionType());

            if (masterPerm != null) {
                return hasAccess(
                    new PermissionContext(masterPerm, masterConfig),
                    version,
                    user,
                    isStaffOrMember
                );
            }
        }


        return false;
    }

    private boolean hasAccess(
        PermissionContext ctx,
        ReleaseVersion version,
        User user,
        boolean isStaffOrMember
    ) {
        switch (ctx.permission) {

            case EVERYONE:
                return true;

            case ADMIN_ONLY, NOT_SELECTED:
                return false;

            case ADMIN_AND_STAFF:
                return isStaffOrMember;

            case ADMIN_STAFF_AFFILIATES:
                return currentSecurityContext.isUser() || isStaffOrMember;

            case ADMIN_STAFF_SELECTED_USERS:
                return checkSelectedUsersAccess(ctx, version, user, isStaffOrMember);

            default:
                return false;
        }
    }

    private boolean checkSelectedUsersAccess(
        PermissionContext ctx,
        ReleaseVersion version,
        User user,
        boolean isStaffOrMember
    ) {

        if (isStaffOrMember) {
            return true;
        }

        if (user == null) {
            return false;
        }

        // VERSION LEVEL
        if (ctx.config == null) {
            return releaseVersionAccessRepository
                .existsByReleaseVersionIdAndUserId(
                    version.getReleaseVersionId(),
                    user.getUserId());
        }

        // MODULE / MASTER
        return isUserInAllowedList(ctx.config.getUserList(), user);
    }

    private boolean isEveryone(ReleasePermissionType perm) {
        return perm == ReleasePermissionType.EVERYONE;
    }

    private boolean isEveryone(ReleasePackageConfig config) {
        return config != null &&
            "EVERYONE".equalsIgnoreCase(config.getReleasePermissionType());
    }

    private ReleasePermissionType safeEnum(String value) {
        try {
            return value != null ? ReleasePermissionType.valueOf(value) : null;
        } catch (Exception e) {
            return null;
        }
    }


    public static class AccessContext {
        User user;
        boolean isStaffOrMember;
        boolean isAnonymous;
        ReleasePackageConfig masterConfig;
        Map<String, ReleasePackageConfig> moduleConfigMap;

        public AccessContext(User user,
                             boolean isStaffOrMember,
                             boolean isAnonymous,
                             ReleasePackageConfig masterConfig,
                             Map<String, ReleasePackageConfig> moduleConfigMap) {
            this.user = user;
            this.isStaffOrMember = isStaffOrMember;
            this.isAnonymous = isAnonymous;
            this.masterConfig = masterConfig;
            this.moduleConfigMap = moduleConfigMap;
        }
    }

    public AccessContext buildAccessContext(Collection<ReleasePackage> releasePackages) {

        User user = getCurrentUser();

        boolean isStaffOrMember =
            currentSecurityContext.isStaff() || currentSecurityContext.isMember();

        boolean isAnonymous =
            !currentSecurityContext.isUser()
                && !currentSecurityContext.isAdmin()
                && !currentSecurityContext.isStaff();

        ReleasePackageConfig masterConfig =
            releasePackageConfigRepository.findByReleaseType("ALL");

        // Collect all release types
        Set<String> releaseTypes = releasePackages.stream()
            .flatMap(rp -> rp.getReleaseVersions().stream())
            .map(ReleaseVersion::getReleaseType)
            .filter(Objects::nonNull)
            .map(String::toUpperCase)
            .collect(Collectors.toSet());

        // Load configs once
        Map<String, ReleasePackageConfig> moduleConfigMap = releaseTypes.stream()
            .collect(Collectors.toMap(
                rt -> rt,
                rt -> releasePackageConfigRepository.findByReleaseType(rt),
                (a, b) -> a
            ));

        return new AccessContext(user, isStaffOrMember, isAnonymous, masterConfig, moduleConfigMap);
    }

    private AccessContext buildAccessContextForSingle(ReleaseVersion version) {

        User user = getCurrentUser();

        boolean isStaffOrMember =
            currentSecurityContext.isStaff() || currentSecurityContext.isMember();

        boolean isAnonymous =
            !currentSecurityContext.isUser()
                && !currentSecurityContext.isAdmin()
                && !currentSecurityContext.isStaff();

        ReleasePackageConfig masterConfig =
            releasePackageConfigRepository.findByReleaseType("ALL");

        Map<String, ReleasePackageConfig> moduleConfigMap = new HashMap<>();

        if (version.getReleaseType() != null) {
            String type = version.getReleaseType().toUpperCase();
            moduleConfigMap.put(
                type,
                releasePackageConfigRepository.findByReleaseType(type)
            );
        }

        return new AccessContext(user, isStaffOrMember, isAnonymous, masterConfig, moduleConfigMap);
    }

    public boolean canAccessReleaseVersion(ReleaseVersion version, AccessContext context) {

        if (currentSecurityContext.isAdmin()) return true;

        if ((currentSecurityContext.isStaff() || currentSecurityContext.isMember())
            && currentSecurityContext.isStaffFor(version.getReleasePackage().getMember())) {
            return true;
        }

        ReleasePackageConfig moduleConfig =
            version.getReleaseType() != null
                ? context.moduleConfigMap.get(version.getReleaseType().toUpperCase())
                : null;

        if (context.isAnonymous) {

            if (version.getPermissionType() != null &&
                version.getPermissionType() != ReleasePermissionType.NOT_SELECTED) {

                return isEveryone(version.getPermissionType());
            }

            if (moduleConfig != null && Boolean.TRUE.equals(moduleConfig.getActive())) {
                return isEveryone(moduleConfig);
            }

            if (context.masterConfig != null && Boolean.TRUE.equals(context.masterConfig.getActive())) {
                return isEveryone(context.masterConfig);
            }

            return false;
        }

        return evaluateAccess(
            version,
            context.user,
            context.isStaffOrMember,
            moduleConfig,
            context.masterConfig
        );
    }

    public boolean canAccessReleaseVersion(ReleaseVersion version) {

        AccessContext context = buildAccessContextForSingle(version);

        return canAccessReleaseVersion(version, context);
    }

    public ReleasePermissionType resolveEffectivePermission(ReleaseVersion version) {


        if (version.getPermissionType() != null
            && version.getPermissionType() != ReleasePermissionType.NOT_SELECTED) {
            return version.getPermissionType();
        }


        if (version.getReleaseType() != null) {
            ReleasePackageConfig moduleConfig =
                releasePackageConfigRepository.findByReleaseType(version.getReleaseType().toUpperCase());

            if (moduleConfig != null && Boolean.TRUE.equals(moduleConfig.getActive())) {
                ReleasePermissionType modulePerm = safeEnum(moduleConfig.getReleasePermissionType());
                if (modulePerm != null) return modulePerm;
            }
        }


        ReleasePackageConfig masterConfig =
            releasePackageConfigRepository.findByReleaseType("ALL");

        if (masterConfig != null && Boolean.TRUE.equals(masterConfig.getActive())) {
            ReleasePermissionType masterPerm = safeEnum(masterConfig.getReleasePermissionType());
            if (masterPerm != null) return masterPerm;
        }

        return ReleasePermissionType.ADMIN_ONLY;
    }

    public String getPermissionDescription(ReleasePermissionType permission) {

        if (permission == null) return "appropriate";

        return switch (permission) {
            case EVERYONE, ADMIN_ONLY, NOT_SELECTED -> "ADMIN";
            case ADMIN_AND_STAFF -> "ADMIN, STAFF or MEMBER";
            case ADMIN_STAFF_AFFILIATES -> "ADMIN, STAFF, MEMBER or AFFILIATE";
            case ADMIN_STAFF_SELECTED_USERS -> "ADMIN, STAFF, MEMBER or specific approved users";
        };
    }

    public boolean canAccessOwnMemberPackage(Member member) {

        if (currentSecurityContext.isStaffFor(member)
            || currentSecurityContext.isMemberFor(member)) {
            return true;
        }

        return Member.KEY_IHTSDO.equals(member.getKey());
    }
}
