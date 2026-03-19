package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReleaseVersionService {


    private static final Logger log = LoggerFactory.getLogger(ReleaseVersionService.class);
    private final ReleaseVersionAccessRepository releaseVersionAccessRepository;
    private final ReleasePackageConfigRepository releasePackageConfigRepository;
    private final ReleaseVersionRepository releaseVersionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private static final String USER_ACCESS_REVOKED_SUCCESS = "User access revoked successfully.";

    private static final String RELEASE_TYPE_ONLINE = "online";
    private static final String RELEASE_TYPE_ALPHA_BETA = "alpha/beta";
    private static final String RELEASE_TYPE_OFFLINE = "offline";

    public ReleaseVersionService(
        ReleaseVersionAccessRepository releaseVersionAccessRepository,
        ReleasePackageConfigRepository releasePackageConfigRepository,
        ReleaseVersionRepository releaseVersionRepository,
        UserRepository userRepository,
        ObjectMapper objectMapper) {
        this.releaseVersionAccessRepository = releaseVersionAccessRepository;
        this.releasePackageConfigRepository = releasePackageConfigRepository;
        this.releaseVersionRepository = releaseVersionRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

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


    public String revokeIndividualUserAccess(String releaseVersionId, String requestUser) throws JsonProcessingException {
        List<String> masterConfigIds = Arrays.asList("ONLINE", "ALPHA/BETA", "OFFLINE", "ALL");

        User user = userRepository.findByLoginIgnoreCase(requestUser);
        if (user == null) {
            return "User not found.";
        }
        long userId = user.getUserId();

        if (masterConfigIds.contains(releaseVersionId)) {
            ReleasePackageConfig masterPermission = releasePackageConfigRepository.findByReleaseType(releaseVersionId);
            List<Long> userList = objectMapper.readValue(masterPermission.getUserList(), new TypeReference<List<Long>>() {
            });
            if (masterPermission.getUserList() == null) {
                return "No user list found.";
            }

            if (userList.remove(userId)) {
                masterPermission.setUserList(objectMapper.writeValueAsString(userList));
                releasePackageConfigRepository.save(masterPermission);
                return USER_ACCESS_REVOKED_SUCCESS;
            }
            return "User ID not found in the list.";
        }

        releaseVersionAccessRepository.deleteReleaseVersionAccessByVersionIdAndUserId(Long.valueOf(releaseVersionId), userId);
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
            Optional<ReleaseVersion> config = releaseVersionRepository.findById(Long.valueOf(releaseId));
            if (config.isPresent()) {
                ReleaseVersion releaseVersion = config.get();
                ReleasePermissionType perm = releaseVersion.getPermissionType();

                if (ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS.equals(perm)) {
                    releaseVersionAccessRepository.deleteReleaseVersionAccessByVersionId(Long.valueOf(releaseId));
                }

                releaseVersion.setPermissionType(ReleasePermissionType.NOT_SELECTED);
                releaseVersionRepository.save(releaseVersion);
            } else {
                return "Release package not found.";
            }
        }

        return USER_ACCESS_REVOKED_SUCCESS;
    }

    private void classifyAndRevokePackages(String releaseType) {

        Collection<ReleaseVersion> releaseVersions = releaseVersionRepository.findAll();

        List<ReleaseVersion> onlineVersions = new ArrayList<>();
        List<ReleaseVersion> alphaBetaVersions = new ArrayList<>();
        List<ReleaseVersion> offlineVersions = new ArrayList<>();

        for (ReleaseVersion version : releaseVersions) {

            if (version.isArchive() || version.getReleaseType() == null) {
                continue;
            }

            String type = version.getReleaseType().toLowerCase();

            type = type.toLowerCase();

            if (RELEASE_TYPE_ONLINE.equals(type)) {
                onlineVersions.add(version);
            } else if (RELEASE_TYPE_ALPHA_BETA.equals(type)) {
                alphaBetaVersions.add(version);
            } else {
                offlineVersions.add(version);
            }
        }

        switch (releaseType.toLowerCase()) {
            case RELEASE_TYPE_ONLINE -> revokeAndUpdateVersions(onlineVersions);
            case RELEASE_TYPE_ALPHA_BETA -> revokeAndUpdateVersions(alphaBetaVersions);
            case RELEASE_TYPE_OFFLINE -> revokeAndUpdateVersions(offlineVersions);
            case "all" -> revokeAllVersions();
            default -> throw new IllegalArgumentException("Unsupported release type: " + releaseType);
        }
    }

    private void revokeAndUpdateVersions(List<ReleaseVersion> versions) {

        List<Long> ids = versions.stream()
            .map(ReleaseVersion::getReleaseVersionId)
            .toList();

        releaseVersionRepository
            .updatePermissionTypeForVersions(ReleasePermissionType.NOT_SELECTED, ids);

        releaseVersionAccessRepository
            .deleteByReleaseVersionIds(ids);

        deactivateConfig("ALL");
    }

    private void revokeAllVersions() {

        releaseVersionRepository
            .updatePermissionTypeForAllVersions(ReleasePermissionType.NOT_SELECTED);

        releaseVersionAccessRepository.deleteAll();

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


    public void revokePermissions(Set<String> releaseTypes) {
        for (String type : releaseTypes) {
            deactivateConfig(type);
        }
        deactivateConfig("ALL");
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

    public List<PermissionVisibilityResponse> getVisibilityForVersions(List<ReleaseVersion> versions) {

        ReleasePackageConfig masterAll =
            releasePackageConfigRepository.findByReleaseType("ALL");

        List<ReleasePackageConfig> configs =
            releasePackageConfigRepository.findAll();

        Map<String, ReleasePackageConfig> configMap = configs.stream()
            .collect(Collectors.toMap(
                c -> c.getReleaseType().toUpperCase(),
                c -> c
            ));

        return versions.stream().map(version -> {

            String releaseType = version.getReleaseType() != null
                ? version.getReleaseType().toUpperCase()
                : "";

            ReleasePackageConfig typeConfig = configMap.get(releaseType);

            boolean isMaster = masterAll != null && Boolean.TRUE.equals(masterAll.getActive());

            String permission;

            if (isMaster) {

                permission = masterAll.getReleasePermissionType();

            } else if (typeConfig != null && Boolean.TRUE.equals(typeConfig.getActive())) {

                permission = typeConfig.getReleasePermissionType();

            } else {

                permission = version.getPermissionType() != null
                    ? version.getPermissionType().toString()
                    : ReleasePermissionType.NOT_SELECTED.toString();

            }

            return new PermissionVisibilityResponse(
                version.getReleaseVersionId(),
                version.getName(),
                permission,
                releaseType
            );

        }).toList();
    }

    public List<String> getUsersForReleaseVersion(Long releaseVersionId) {

        ReleaseVersion version = releaseVersionRepository
            .findById(releaseVersionId)
            .orElseThrow(() -> new IllegalArgumentException("Release version not found"));

        String releaseType = version.getReleaseType() != null
            ? version.getReleaseType().toUpperCase()
            : "";

        ReleasePackageConfig masterAll =
            releasePackageConfigRepository.findByReleaseType("ALL");

        if (masterAll != null && masterAll.getActive()) {
            return extractUsers(masterAll);
        }

        ReleasePackageConfig typeConfig =
            releasePackageConfigRepository.findByReleaseTypeIgnoreCase(releaseType);

        if (typeConfig != null && typeConfig.getActive()) {
            return extractUsers(typeConfig);
        }

        return releaseVersionAccessRepository
            .findLoginsByReleaseVersionId(releaseVersionId);
    }

    private List<String> extractUsers(ReleasePackageConfig config) {

        if (config.getUserList() == null || config.getUserList().isEmpty()) {
            return Collections.emptyList();
        }

        try {

            List<Long> userIds =
                objectMapper.readValue(config.getUserList(),
                    new TypeReference<List<Long>>() {
                    });

            if (userIds.isEmpty()) {
                return Collections.emptyList();
            }

            return releaseVersionAccessRepository.findLoginsByUserIds(userIds);

        } catch (JsonProcessingException e) {
            log.error("Invalid userList JSON for config {}", config.getReleaseType(), e);
            return Collections.emptyList();

        }
    }
}
