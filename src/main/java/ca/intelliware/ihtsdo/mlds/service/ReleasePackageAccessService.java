package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackageConfig;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReleasePackageAccessService {

    private final Logger log = LoggerFactory.getLogger(ReleasePackageAccessService.class);

    private final ReleasePackageConfigRepository releasePackageConfigRepository;
    private final ReleasePackageAccessRepository releasePackageAccessRepository;
    private final UserRepository userRepository;
    private final CurrentSecurityContext currentSecurityContext;

    public ReleasePackageAccessService(ReleasePackageConfigRepository releasePackageConfigRepository,
                                       ReleasePackageAccessRepository releasePackageAccessRepository,
                                       UserRepository userRepository,
                                       CurrentSecurityContext currentSecurityContext) {
        this.releasePackageConfigRepository = releasePackageConfigRepository;
        this.releasePackageAccessRepository = releasePackageAccessRepository;
        this.userRepository = userRepository;
        this.currentSecurityContext = currentSecurityContext;
    }

    private static final String PERMISSION_EVERYONE = "EVERYONE";
    private static final String PERMISSION_ADMIN_ONLY = "ADMIN_ONLY";
    private static final String PERMISSION_ADMIN_AND_STAFF = "ADMIN_AND_STAFF";
    private static final String PERMISSION_ADMIN_STAFF_AFFILIATES = "ADMIN_STAFF_AFFILIATES";

    private static final String PERMISSION_NOT_SELECTED = "NOT_SELECTED";
    private static final String RELEASE_TYPE_ONLINE = "ONLINE";
    private static final String RELEASE_TYPE_ALPHA_BETA = "ALPHA/BETA";
    private static final String RELEASE_TYPE_OFFLINE = "OFFLINE";


    public Collection<ReleasePackage> getAccessiblePackagesForStaff(
        Collection<ReleasePackage> allPackages,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages,
        String staffMemberKey
    ) {
        Set<ReleasePackage> result = new HashSet<>();
        if(currentSecurityContext.isStaff()) {
            result = getStaffOwnedPackages(allPackages, staffMemberKey);
        }

        List<ReleasePackageConfig> grantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionTypeNot(PERMISSION_NOT_SELECTED);

        List<ReleasePackageConfig> notGrantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionType(PERMISSION_NOT_SELECTED);

        // Handle granted permissions
        for (ReleasePackageConfig config : grantedPermissions) {
            String type = config.getReleaseType();
            String permission = config.getReleasePermissionType();

            if ("ALL".equalsIgnoreCase(type)) {
                return handleAllTypePermission(permission, allPackages, result);
            }

            if (!PERMISSION_ADMIN_ONLY.equalsIgnoreCase(permission)) {
                addByType(result, type, onlinePackages, alphaBetaPackages, offlinePackages);
            }
        }

        // Handle not granted permissions
        for (ReleasePackageConfig config : notGrantedPermissions) {
            String type = config.getReleaseType().toUpperCase();
            List<ReleasePackage> filtered = getFilteredPackagesByType(
                type, onlinePackages, alphaBetaPackages, offlinePackages
            );
            result.addAll(filtered);
        }

        return result;
    }

    private Set<ReleasePackage> getStaffOwnedPackages(Collection<ReleasePackage> packages, String staffMemberKey) {
        return packages.stream()
            .filter(pkg -> pkg.getMember().getKey().equals(staffMemberKey))
            .collect(Collectors.toSet());
    }

    private Collection<ReleasePackage> handleAllTypePermission(
        String permissionType,
        Collection<ReleasePackage> allPackages,
        Set<ReleasePackage> staffOwned
    ) {
        if (PERMISSION_ADMIN_ONLY.equalsIgnoreCase(permissionType)) {
            return new HashSet<>(staffOwned);
        }
        return allPackages;
    }

    private void addByType(
        Set<ReleasePackage> result,
        String type,
        List<ReleasePackage> online,
        List<ReleasePackage> alphaBeta,
        List<ReleasePackage> offline
    ) {
        switch (type.toUpperCase()) {
            case RELEASE_TYPE_ONLINE -> result.addAll(online);
            case RELEASE_TYPE_ALPHA_BETA -> result.addAll(alphaBeta);
            case RELEASE_TYPE_OFFLINE -> result.addAll(offline);
            default -> log.warn("Unknown release type encountered: {}", type);
        }
    }

    private List<ReleasePackage> getFilteredPackagesByType(
        String type,
        List<ReleasePackage> online,
        List<ReleasePackage> alphaBeta,
        List<ReleasePackage> offline
    ) {
        List<ReleasePackage> source = switch (type) {
            case RELEASE_TYPE_ONLINE -> online;
            case RELEASE_TYPE_ALPHA_BETA -> alphaBeta;
            case RELEASE_TYPE_OFFLINE -> offline;
            default -> Collections.emptyList();
        };

        return source.stream()
            .filter(pkg -> pkg.getPermissionType() != ReleasePermissionType.ADMIN_ONLY &&
                pkg.getPermissionType() != ReleasePermissionType.NOT_SELECTED)
            .toList();
    }

    private List<ReleasePackage> getPackageListByType(
        String type,
        List<ReleasePackage> online,
        List<ReleasePackage> alphaBeta,
        List<ReleasePackage> offline
    ) {
        return switch (type) {
            case RELEASE_TYPE_ONLINE -> online;
            case RELEASE_TYPE_ALPHA_BETA -> alphaBeta;
            case RELEASE_TYPE_OFFLINE -> offline;
            default -> Collections.emptyList();
        };
    }

    public Collection<ReleasePackage> getAccessiblePackagesForUnauthenticatedUser(
        Collection<ReleasePackage> allPackages,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        Set<ReleasePackage> result = new HashSet<>();

        List<ReleasePackageConfig> grantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionTypeNot(PERMISSION_NOT_SELECTED);
        List<ReleasePackageConfig> notGrantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionType(PERMISSION_NOT_SELECTED);

        handleGrantedPermissionsForUnauthUser(grantedPermissions, result, allPackages, onlinePackages, alphaBetaPackages, offlinePackages);
        handleNotGrantedPermissionsForUnauthUser(notGrantedPermissions, result, onlinePackages, alphaBetaPackages, offlinePackages);

        return result;
    }

    private void handleGrantedPermissionsForUnauthUser(
        List<ReleasePackageConfig> grantedPermissions,
        Set<ReleasePackage> result,
        Collection<ReleasePackage> allPackages,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        for (ReleasePackageConfig config : grantedPermissions) {
            String type = config.getReleaseType();
            String permission = config.getReleasePermissionType();

            if ("ALL".equalsIgnoreCase(type)) {
                if (isRestrictedPermission(permission)) {
                    return;
                }
                if (PERMISSION_EVERYONE.equalsIgnoreCase(permission)) {
                    result.addAll(allPackages);
                    return;
                }
            } else if (PERMISSION_EVERYONE.equalsIgnoreCase(permission)) {
                addPackagesByType(result, type, onlinePackages, alphaBetaPackages, offlinePackages);
            }
        }
    }

    private void handleNotGrantedPermissionsForUnauthUser(
        List<ReleasePackageConfig> notGrantedPermissions,
        Set<ReleasePackage> result,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        for (ReleasePackageConfig config : notGrantedPermissions) {
            String type = config.getReleaseType().toUpperCase();
            List<ReleasePackage> packages = getPackageListByType(type, onlinePackages, alphaBetaPackages, offlinePackages);
            result.addAll(packages.stream()
                .filter(pkg -> pkg.getPermissionType() == ReleasePermissionType.EVERYONE)
                .toList());
        }
    }


    public Collection<ReleasePackage> getAccessiblePackagesForUser(
        Collection<ReleasePackage> allPackages,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages,
        String username
    ) {
        Set<ReleasePackage> finalResult = new HashSet<>();
        User user = userRepository.findByLoginIgnoreCase(username);
        long userId = user.getUserId();

        List<ReleasePackageConfig> grantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionTypeNot(PERMISSION_NOT_SELECTED);
        List<ReleasePackageConfig> notGrantedPermissions =
            releasePackageConfigRepository.findByReleasePermissionType(PERMISSION_NOT_SELECTED);

        processGrantedPermissionsForUser(grantedPermissions, userId, allPackages, finalResult, onlinePackages, alphaBetaPackages, offlinePackages);
        processNotGrantedPermissionsForUser(notGrantedPermissions, userId, finalResult, onlinePackages, alphaBetaPackages, offlinePackages);

        return finalResult;
    }

    private void processGrantedPermissionsForUser(
        List<ReleasePackageConfig> configs,
        long userId,
        Collection<ReleasePackage> allPackages,
        Set<ReleasePackage> result,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        for (ReleasePackageConfig config : configs) {
            String type = config.getReleaseType().toUpperCase();
            String permission = config.getReleasePermissionType();

            if ("ALL".equals(type)) {
                if (isAdminOnlyOrStaff(permission)) return;
                if (isEveryoneOrAffiliates(permission) || config.getUserList().contains(String.valueOf(userId))) {
                    result.addAll(allPackages);
                    return;
                }
            } else if (!isAdminOnlyOrStaff(permission) &&
                (isEveryoneOrAffiliates(permission) || config.getUserList().contains(String.valueOf(userId)))) {
                addPackagesByType(result, type, onlinePackages, alphaBetaPackages, offlinePackages);
            }
        }
    }

    private void processNotGrantedPermissionsForUser(
        List<ReleasePackageConfig> configs,
        long userId,
        Set<ReleasePackage> result,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        for (ReleasePackageConfig config : configs) {
            String type = config.getReleaseType().toUpperCase();
            List<ReleasePackage> packages = getPackageListByType(type, onlinePackages, alphaBetaPackages, offlinePackages);

            result.addAll(packages.stream()
                .filter(pkg -> pkg.getPermissionType() == ReleasePermissionType.EVERYONE ||
                    pkg.getPermissionType() == ReleasePermissionType.ADMIN_STAFF_AFFILIATES)
                .toList());

            packages.stream()
                .filter(pkg -> pkg.getPermissionType() == ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS)
                .filter(pkg -> releasePackageAccessRepository.existsByReleasePackageIdAndUserId(pkg.getReleasePackageId(), userId))
                .forEach(result::add);
        }
    }

    private void addPackagesByType(
        Set<ReleasePackage> target,
        String type,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        target.addAll(getPackageListByType(type, onlinePackages, alphaBetaPackages, offlinePackages));
    }

    private boolean isRestrictedPermission(String permission) {
        return List.of(PERMISSION_ADMIN_ONLY, PERMISSION_ADMIN_AND_STAFF,
                PERMISSION_ADMIN_STAFF_AFFILIATES, "ADMIN_STAFF_SELECTED_USERS")
            .contains(permission.toUpperCase());
    }

    private boolean isAdminOnlyOrStaff(String permission) {
        return PERMISSION_ADMIN_ONLY.equalsIgnoreCase(permission) ||
            PERMISSION_ADMIN_AND_STAFF.equalsIgnoreCase(permission);
    }

    private boolean isEveryoneOrAffiliates(String permission) {
        return PERMISSION_EVERYONE.equalsIgnoreCase(permission) ||
            PERMISSION_ADMIN_STAFF_AFFILIATES.equalsIgnoreCase(permission);
    }


    // for each release version access

    public boolean isStaffOwner(ReleasePackage rp) {
        return currentSecurityContext.isStaff() &&
            currentSecurityContext.getStaffMemberKey().equals(rp.getMember().getKey());
    }

    public boolean isAdminOnly(ReleasePackage rp, ReleasePackageConfig config, ReleasePackageConfig master) {

        if (rp.getPermissionType() == ReleasePermissionType.ADMIN_ONLY) {
            return true;
        }

        if (rp.getPermissionType() == ReleasePermissionType.NOT_SELECTED) {
            String masterPermission = master.getReleasePermissionType();
            String configPermission = config.getReleasePermissionType();

            if (ReleasePermissionType.ADMIN_ONLY.toString().equals(masterPermission)
                || ReleasePermissionType.ADMIN_ONLY.toString().equals(configPermission)) {
                return true;
            }

            return ReleasePermissionType.NOT_SELECTED.toString().equals(masterPermission)
                && ReleasePermissionType.NOT_SELECTED.toString().equals(configPermission);
        }

        return false;
    }


    public ResponseEntity<ReleasePackage> handlePublicAccess(ReleasePackage rp, ReleasePackageConfig config, ReleasePackageConfig master) {
        if (rp.getPermissionType() == ReleasePermissionType.EVERYONE) return ResponseEntity.ok(rp);

        if (rp.getPermissionType() == ReleasePermissionType.NOT_SELECTED &&
            (ReleasePermissionType.EVERYONE.toString().equals(master.getReleasePermissionType()) ||
                ReleasePermissionType.EVERYONE.toString().equals(config.getReleasePermissionType()))) {
            return ResponseEntity.ok(rp);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<ReleasePackage> handleUserAccess(ReleasePackage rp, ReleasePackageConfig config, ReleasePackageConfig master, long rpId) {
        long userId = userRepository.findByLoginIgnoreCase(currentSecurityContext.getCurrentUserName()).getUserId();
        ReleasePermissionType type = rp.getPermissionType();

        if (type == ReleasePermissionType.ADMIN_ONLY || type == ReleasePermissionType.ADMIN_AND_STAFF)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (type == ReleasePermissionType.EVERYONE || type == ReleasePermissionType.ADMIN_STAFF_AFFILIATES)
            return ResponseEntity.ok(rp);

        if (type == ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS &&
            releasePackageAccessRepository.existsByReleasePackageIdAndUserId(rpId, userId)) {
            return ResponseEntity.ok(rp);
        }

        if (type == ReleasePermissionType.NOT_SELECTED) {
            if (isEveryoneOrAffiliate(master) || isEveryoneOrAffiliate(config)) {
                return ResponseEntity.ok(rp);
            }

            if (isSelectedUser(master, userId) || isSelectedUser(config, userId)) {
                return ResponseEntity.ok(rp);
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isEveryoneOrAffiliate(ReleasePackageConfig cfg) {
        String perm = cfg.getReleasePermissionType();
        return ReleasePermissionType.EVERYONE.toString().equals(perm)
            || ReleasePermissionType.ADMIN_STAFF_AFFILIATES.toString().equals(perm);
    }

    private boolean isSelectedUser(ReleasePackageConfig cfg, long userId) {
        return ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS.toString().equals(cfg.getReleasePermissionType())
            && cfg.getUserList().contains(String.valueOf(userId));
    }


}

