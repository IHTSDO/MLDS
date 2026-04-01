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
    }

    private String serializeUserList(List<String> users) {
        try {
            List<Long> userIds = users.stream()
                .map(Long::valueOf)
                .toList();

            return objectMapper.writeValueAsString(userIds);

        } catch (Exception e) {
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

            String permission;

            if (version.getPermissionType() != null
                && version.getPermissionType() != ReleasePermissionType.NOT_SELECTED) {

                permission = version.getPermissionType().toString();

            } else if (typeConfig != null && Boolean.TRUE.equals(typeConfig.getActive())) {

                permission = typeConfig.getReleasePermissionType();

            } else if (masterAll != null && Boolean.TRUE.equals(masterAll.getActive())) {

                permission = masterAll.getReleasePermissionType();

            } else {

                permission = ReleasePermissionType.NOT_SELECTED.toString();
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


        if (version.getPermissionType() == ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS) {
            return releaseVersionAccessRepository
                .findLoginsByReleaseVersionId(releaseVersionId);
        }

        String releaseType = version.getReleaseType() != null
            ? version.getReleaseType().toUpperCase()
            : "";


        ReleasePackageConfig typeConfig =
            releasePackageConfigRepository.findByReleaseTypeIgnoreCase(releaseType);

        if (typeConfig != null && typeConfig.getActive()) {
            return extractUsers(typeConfig);
        }


        ReleasePackageConfig masterAll =
            releasePackageConfigRepository.findByReleaseType("ALL");

        if (masterAll != null && masterAll.getActive()) {
            return extractUsers(masterAll);
        }

        return Collections.emptyList();
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

    @Transactional
    public String revokeAllVersionLevelPermissions() {
        releaseVersionRepository.updatePermissionTypeForAllVersions(
            ReleasePermissionType.NOT_SELECTED
        );
        releaseVersionAccessRepository.deleteAllAccess();

        return "All version-level permissions revoked successfully.";
    }
}
