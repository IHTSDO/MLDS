package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ReleasePackageService {

    private final ReleasePackageRepository releasePackageRepository;
    private final ReleasePackageAccessRepository releasePackageAccessRepository;
    private final ReleasePackageConfigRepository releasePackageConfigRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private static final String RELEASE_TYPE_ONLINE = "online";
    private static final String RELEASE_TYPE_ALPHA_BETA = "alpha/beta";
    private static final String RELEASE_TYPE_OFFLINE = "offline";
    private static final String USER_ACCESS_REVOKED_SUCCESS = "User access revoked successfully.";


    public ReleasePackageService(ReleasePackageRepository releasePackageRepository,
                                 ReleasePackageAccessRepository releasePackageAccessRepository,
                                 ReleasePackageConfigRepository releasePackageConfigRepository,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper) {
        this.releasePackageRepository = releasePackageRepository;
        this.releasePackageAccessRepository = releasePackageAccessRepository;
        this.releasePackageConfigRepository = releasePackageConfigRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }


    public void updateReleasePackagesPermission(Map<String, Object> request) {
        List<Integer> releasePackages = (List<Integer>) request.get("releases");
        String releasePackageType = (String) request.get("releasePackageType");
        List<String> users = (List<String>) request.get("users");

        List<Long> ids = releasePackages.stream().map(Integer::longValue).toList();
        List<ReleasePackage> packages = releasePackageRepository.findAllByReleasePackageIdIn(ids);

        Set<String> affectedTypes = new HashSet<>();
        List<ReleasePackageAccess> accessList = new ArrayList<>();

        for (ReleasePackage pkg : packages) {
            updatePermissionType(pkg, releasePackageType);

            if (requiresUserAccess(pkg, users)) {
                accessList.addAll(createAccessList(pkg, users));
            }

            if (!pkg.getReleaseVersions().isEmpty()) {
                Set<String> types = extractAffectedTypes(pkg);
                affectedTypes.addAll(types);
            }
        }

        if (!accessList.isEmpty()) {
            releasePackageAccessRepository.saveAll(accessList);
        }

        releasePackageRepository.saveAll(packages);

        revokePermissions(affectedTypes);
    }

    private void updatePermissionType(ReleasePackage pkg, String type) {
        pkg.setPermissionType(ReleasePermissionType.valueOf(type));
    }

    private boolean requiresUserAccess(ReleasePackage pkg, List<String> users) {
        return pkg.getPermissionType() == ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS && users != null;
    }

    private List<ReleasePackageAccess> createAccessList(ReleasePackage pkg, List<String> users) {
        List<ReleasePackageAccess> list = new ArrayList<>();
        for (String user : users) {
            ReleasePackageAccess access = new ReleasePackageAccess();
            access.setReleasePackageId(pkg.getReleasePackageId());
            access.setUserId(Long.parseLong(user));
            list.add(access);
        }
        return list;
    }

    private Set<String> extractAffectedTypes(ReleasePackage pkg) {
        Map<String, Integer> typeCount = new HashMap<>();
        for (ReleaseVersion version : pkg.getReleaseVersions()) {
            if (!version.isArchive()) {
                String type = version.getReleaseType();
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }
        }

        List<String> priority = Arrays.asList(RELEASE_TYPE_ONLINE, RELEASE_TYPE_ALPHA_BETA, RELEASE_TYPE_OFFLINE);
        for (String type : priority) {
            if (typeCount.containsKey(type)) {
                return Set.of(type.toUpperCase());
            }
        }

        return Collections.emptySet();
    }

    private void revokePermissions(Set<String> releaseTypes) {
        for (String type : releaseTypes) {
            deactivateConfig(type);
        }
        deactivateConfig("ALL");
    }


    // updateReleaseMasterConfig

    public void updateReleaseMasterConfig(Map<String, Object> request) {
        String releaseType = (String) request.get("releaseType");
        String releasePackage = (String) request.get("releasePackage");
        String releasePermissionType = (String) request.get("releasePermissionType");
        List<String> users = (List<String>) request.get("users");

        String userListJson = serializeUserList(users);

        ReleasePackageConfig config = releasePackageConfigRepository.findByReleaseType(releaseType);
        if (config == null) {
            throw new IllegalStateException("Release type config not found: " + releaseType);
        }

        config.setReleasePackageAccess(releasePackage);
        config.setReleasePermissionType(releasePermissionType);
        config.setActive(true);
        config.setUserList(userListJson);
        releasePackageConfigRepository.save(config);

        classifyAndRevokePackages(releaseType);
    }

    private String serializeUserList(List<String> users) {
        try {
            return objectMapper.writeValueAsString(users);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize user list", e);
        }
    }

    private void classifyAndRevokePackages(String releaseType) {
        Collection<ReleasePackage> releasePackages = releasePackageRepository.findAll();

        List<ReleasePackage> onlinePackages = new ArrayList<>();
        List<ReleasePackage> alphaBetaPackages = new ArrayList<>();
        List<ReleasePackage> offlinePackages = new ArrayList<>();

        for (ReleasePackage pkg : releasePackages) {
            classifyPackage(pkg, onlinePackages, alphaBetaPackages, offlinePackages);
        }

        switch (releaseType.toLowerCase()) {
            case RELEASE_TYPE_ONLINE -> revokeAndUpdate(onlinePackages);
            case RELEASE_TYPE_ALPHA_BETA -> revokeAndUpdate(alphaBetaPackages);
            case RELEASE_TYPE_OFFLINE -> revokeAndUpdate(offlinePackages);
            case "all" -> revokeAllTypes();
            default -> throw new IllegalArgumentException("Unsupported release type: " + releaseType);
        }
    }

    public void classifyPackage(
        ReleasePackage pkg,
        List<ReleasePackage> onlinePackages,
        List<ReleasePackage> alphaBetaPackages,
        List<ReleasePackage> offlinePackages
    ) {
        boolean hasOnline = false;
        boolean hasAlphaBeta = false;

        for (ReleaseVersion version : pkg.getReleaseVersions()) {
            if (!version.isArchive()) {
                String type = version.getReleaseType().toLowerCase();

                if (type.equals(RELEASE_TYPE_ONLINE)) {
                    hasOnline = true;
                    break;
                }

                if (type.equals(RELEASE_TYPE_ALPHA_BETA)) {
                    hasAlphaBeta = true;
                }
            }
        }


        if (hasOnline) {
            onlinePackages.add(pkg);
        } else if (hasAlphaBeta) {
            alphaBetaPackages.add(pkg);
        } else {
            offlinePackages.add(pkg);
        }
    }


    private void revokeAndUpdate(List<ReleasePackage> packages) {
        List<Long> ids = packages.stream()
            .map(ReleasePackage::getReleasePackageId)
            .toList();

        releasePackageRepository.updatePermissionTypeForPackages(ReleasePermissionType.NOT_SELECTED, ids);
        releasePackageAccessRepository.deleteReleasePackageAccessByPackageIds(ids);
        deactivateConfig("ALL");
    }

    private void revokeAllTypes() {
        releasePackageRepository.updatePermissionTypeForAllPackages(ReleasePermissionType.NOT_SELECTED);
        releasePackageAccessRepository.deleteAll();

        List<ReleasePackageConfig> configs = releasePackageConfigRepository.findAll();
        for (ReleasePackageConfig config : configs) {
            if (!"ALL".equalsIgnoreCase(config.getReleaseType())) {
                config.setReleasePermissionType(ReleasePermissionType.NOT_SELECTED.name());
                config.setActive(false);
                config.setUserList("[]");
                releasePackageConfigRepository.save(config);
            }
        }
    }

    private void deactivateConfig(String type) {
        ReleasePackageConfig config = releasePackageConfigRepository.findByReleaseType(type);
        if (config != null) {
            config.setReleasePermissionType(ReleasePermissionType.NOT_SELECTED.toString());
            config.setActive(false);
            config.setUserList("[]");
            releasePackageConfigRepository.save(config);
        }
    }


    // user access revoking by clicking from ui logics


    public String revokeIndividualUserAccess(String releaseId, String requestUser) throws JsonProcessingException {
        List<String> masterConfigIds = Arrays.asList("ONLINE", "ALPHA/BETA", "OFFLINE", "ALL");

        User user = userRepository.findByLoginIgnoreCase(requestUser);
        long userId = user.getUserId();

        if (masterConfigIds.contains(releaseId)) {
            ReleasePackageConfig masterPermission = releasePackageConfigRepository.findByReleaseType(releaseId);
            List<Long> userList = objectMapper.readValue(masterPermission.getUserList(), new TypeReference<List<Long>>() {});

            if (userList.remove(userId)) {
                masterPermission.setUserList(objectMapper.writeValueAsString(userList));
                releasePackageConfigRepository.save(masterPermission);
                return USER_ACCESS_REVOKED_SUCCESS;
            }
            return "User ID not found in the list.";
        }

        releasePackageAccessRepository.deleteReleasePackageAccessByPackageIdAndUserId(Long.valueOf(releaseId), userId);
        return USER_ACCESS_REVOKED_SUCCESS;
    }

    public String revokeAllReleaseAccess(String releaseId) {
        List<String> masterConfigIds = Arrays.asList("ONLINE", "ALPHA/BETA", "OFFLINE", "ALL");

        if (masterConfigIds.contains(releaseId)) {
            ReleasePackageConfig config = releasePackageConfigRepository.findByReleaseType(releaseId);
            config.setUserList("[]");
            config.setReleasePermissionType(String.valueOf(ReleasePermissionType.NOT_SELECTED));
            config.setActive(false);
            releasePackageConfigRepository.save(config);
        } else {
            Optional<ReleasePackage> config = releasePackageRepository.findById(Long.valueOf(releaseId));
            if (config.isPresent()) {
                ReleasePackage releasePackage = config.get();

                if (releasePackage.getPermissionType().equals(ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS)) {
                    releasePackageAccessRepository.deleteReleasePackageAccessByPackageId(Long.valueOf(releaseId));
                }

                releasePackage.setPermissionType(ReleasePermissionType.NOT_SELECTED);
                releasePackageRepository.save(releasePackage);
            } else {
                return "Release package not found.";
            }
        }

        return USER_ACCESS_REVOKED_SUCCESS;
    }

    // check visiblity

    public PermissionVisibilityResponse getVisibilityDetails(ReleasePackage releasePackage) {
        String packageType = categorizePackage(releasePackage.getReleaseVersions());
        ReleasePackageConfig releaseTypePermission = releasePackageConfigRepository.findByReleaseTypeIgnoreCase(packageType);

        boolean isMasterPermission = true;
        String permissionType = "";

        if (releaseTypePermission != null && Boolean.FALSE.equals(releaseTypePermission.getActive())) {
            isMasterPermission = false;
            permissionType = releasePackage.getPermissionType().toString();
        } else {
            permissionType = releaseTypePermission != null ? releaseTypePermission.getReleasePermissionType() : "";
        }

        return new PermissionVisibilityResponse(isMasterPermission, permissionType, packageType);
    }

    public ReleasePackageConfig getMasterPermission(String releaseType) {
        return releasePackageConfigRepository.findByReleaseType(releaseType);
    }

    public String categorizePackage(Set<ReleaseVersion> releaseVersions) {
        boolean hasOnline = false;
        boolean hasAlphaOrBeta = false;

        for (ReleaseVersion version : releaseVersions) {
            if (!version.isArchive()) {
                String releaseType = version.getReleaseType().toLowerCase();

                if (RELEASE_TYPE_ONLINE.equals(releaseType)) {
                    hasOnline = true;
                    break; // No need to continue if we've already found an online version
                }

                if (RELEASE_TYPE_ALPHA_BETA.equals(releaseType)) {
                    hasAlphaOrBeta = true;
                }
            }
        }

        if (hasOnline) {
            return RELEASE_TYPE_ONLINE;
        }
        else if (hasAlphaOrBeta) {
            return RELEASE_TYPE_ALPHA_BETA;
        }
        else {
            return RELEASE_TYPE_OFFLINE;
        }
    }

}
