package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageConfigRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionAccessRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.ReleaseVersionService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleasePermissionRequestDTO;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleaseVersionCheckViewDTO;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

import static ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType.NOT_SELECTED;

@RestController
public class ReleaseVersionsResource {

    @Autowired
    ReleasePackageRepository releasePackageRepository;

    @Autowired
    ReleaseVersionRepository releaseVersionRepository;

    @Resource
    ReleasePackageAuthorizationChecker authorizationChecker;

    @Resource
    CurrentSecurityContext currentSecurityContext;

    @Resource
    ReleasePackageAuditEvents releasePackageAuditEvents;

    @Autowired
    ReleaseFilePrivacyFilter releaseFilePrivacyFilter;

    @Resource
    UserNotifier userNotifier;

    @Resource
    ReleaseVersionAccessRepository releaseVersionAccessRepository;

    @Resource
    ReleaseVersionService releaseVersionService;

    @Resource
    ReleasePackageConfigRepository releasePackageConfigRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Release Versions

    @Operation(
        summary = "Create a new Release Version",
        description = """
            Creates a new Release Version within a Release Package.

            The name and description identify the Release Version.
            The publishedAt value is optional and specifies the publish date
            of the Release Version in YYYY-MM-DD format.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Release Version created successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ReleaseVersion.class),
            examples = @ExampleObject(
                name = "Create Release Version response",
                value = """
                    {
                      "releaseVersionId": 211924,
                      "createdAt": "2015-10-28T20:48:21.796Z",
                      "name": "First Version",
                      "description": "<p><b>First</b> version description <br/></p>",
                      "online": false,
                      "publishedAt": "2015-10-28",
                      "releaseFiles": []
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release Package not found."
    )
    @PostMapping(
        value = Routes.RELEASE_VERSIONS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public ResponseEntity<ReleaseVersion> createReleaseVersion(
        @Parameter(
            description = "Unique identifier of the Release Package.",
            required = true,
            example = "211920"
        )
        @PathVariable long releasePackageId,

        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Release Version to create.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReleaseVersion.class),
                examples = @ExampleObject(
                    name = "Create Release Version request",
                    value = """
                        {
                          "name": "First Version",
                          "description": "<p><b>First</b> version description <br/></p>"
                        }
                        """
                )
            )
        )
        ReleaseVersion releaseVersion) {

        Optional<ReleasePackage> releasePackageOptional =
            releasePackageRepository.findById(releasePackageId);

        if (releasePackageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = releasePackageOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releasePackage);

        releaseVersion.setId(String.valueOf(UUID.randomUUID()));

        releaseVersion.setCreatedBy(
            currentSecurityContext.getCurrentUserName()
        );

        releaseVersion.setPermissionType(NOT_SELECTED);

        releaseVersionRepository.save(releaseVersion);

        releasePackage.addReleaseVersion(releaseVersion);

        releasePackageAuditEvents.logCreationOf(releaseVersion);

        return  new ResponseEntity<>(
            releaseVersion,
            HttpStatus.OK
        );

    }


    @Operation(
        summary = "Get a single Release Version",
        description = "Returns a single Release Version identified by its Release Package ID and Release Version ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Release Version returned successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ReleaseVersion.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release Version not found."
    )
    @ApiResponse(
        responseCode = "403",
        description = "The current user is not authorized to access the Release Version."
    )
    @GetMapping(
        value = Routes.RELEASE_VERSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed({
        AuthoritiesConstants.USER,
        AuthoritiesConstants.MEMBER,
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseVersion> getReleaseVersion(

        @Parameter(
            description = "Unique identifier of the Release Package.",
            required = true,
            example = "211920"
        )
        @PathVariable long releasePackageId,

        @Parameter(
            description = "Unique identifier of the Release Version.",
            required = true,
            example = "211924"
        )
        @PathVariable long releaseVersionId) {

        //FIXME should we check children being consistent?

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion =
            releaseVersionOptional.get();

        if (!authorizationChecker.canAccessReleaseVersion(
            releaseVersion)) {

            return new ResponseEntity<>(
                HttpStatus.FORBIDDEN
            );
        }

        releaseVersion =
            releaseFilePrivacyFilter.filterReleaseVersionByAuthority(
                releaseVersion
            );

        return new ResponseEntity<>(
            releaseVersion,
            HttpStatus.OK
        );
    }


    @Operation(
        summary = "Publish a Release Version Online",
        description = """
            To publish a Release Version online the Release Version's
            online flag should be set to true.

            To take the Release Version offline the online flag should
            be set to false.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Release Version updated successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ReleaseVersion.class),
            examples = @ExampleObject(
                name = "Publish Release Version response",
                value = """
                    {
                      "releaseVersionId": 211924,
                      "createdAt": "2015-10-28T20:48:21.796Z",
                      "name": "First Version",
                      "description": "<p><b>First</b> version description <br/></p>",
                      "online": true,
                      "publishedAt": "2015-10-29",
                      "releaseFiles": [
                        {
                          "releaseFileId": 211928,
                          "label": "<p>Example file</p>",
                          "createdAt": "2015-10-29T14:44:52.682Z",
                          "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
                          "downloadUrl": "http://files.com/example.txt"
                        }
                      ]
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release Version not found."
    )
    @PutMapping(
        value = Routes.RELEASE_VERSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseVersion> updateReleaseVersion(

        @Parameter(
            description = "Unique identifier of the Release Package.",
            required = true,
            example = "211920"
        )
        @PathVariable long releasePackageId,

        @Parameter(
            description = "Unique identifier of the Release Version.",
            required = true,
            example = "211924"
        )
        @PathVariable long releaseVersionId,

        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Release Version information. Set online to true to make the Release Version available to affiliates.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReleaseVersion.class),
                examples = @ExampleObject(
                    name = "Publish Release Version request",
                    value = """
                        {
                          "name": "First Version",
                          "description": "<p><b>First</b> version description <br/></p>",
                          "online": true
                        }
                        """
                )
            )
        )
        ReleaseVersion body) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion =
            releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage()
        );

        /*MLDS*/
        String preOnline =
            String.valueOf(
                releaseVersion.getReleaseType().equalsIgnoreCase ("offline ")
                    || releaseVersion.getReleaseType() .equalsIgnoreCase( "alpha/beta")
            );

        releaseVersion.setName(body.getName());
        releaseVersion.setDescription(body.getDescription());

        if (!releaseVersion.isArchive()) {
            releaseVersion.setReleaseType(body.getReleaseType());
        }

        releaseVersion.setPublishedAt(body.getPublishedAt());

        releaseVersion.setSummary(body.getSummary());
        releaseVersion.setVersionDependentURI(
            body.getVersionDependentURI()
        );
        releaseVersion.setVersionDependentDerivativeURI(
            body.getVersionDependentDerivativeURI()
        );
        releaseVersion.setVersionURI(
            body.getVersionURI()
        );

        if (releaseVersion.getId() == null
            || releaseVersion.getId().isBlank()) {

            releaseVersion.setId(
                UUID.randomUUID().toString()
            );
        }

        releaseVersion.setLastUpdated(
            Instant.now()
        );

        releaseVersion.setPackageType(
            body.getPackageType()
        );

        if (!Objects.equal(
            preOnline,
            releaseVersion.getReleaseType()
        )) {

            if (releaseVersion.getReleaseType()
                .equalsIgnoreCase("online")) {

                releasePackageAuditEvents.logTakenOnline(
                    releaseVersion
                );

            } else if (releaseVersion.getReleaseType()
                .equalsIgnoreCase("offline")) {

                releasePackageAuditEvents.logTakenOffline(
                    releaseVersion
                );

            } else {

                releasePackageAuditEvents.logTakenAlphaAndBeta(
                    releaseVersion
                );
            }
        }

        return new ResponseEntity<>(
            releaseVersion,
            HttpStatus.OK
        );
    }


    @Operation(
        summary = "Notify Affiliates of a Release",
        description = """
            Affiliates are not notified automatically when a new Release
            Version has been published online.

            When a notification to affiliates with download access to the
            Release Package is warranted, this endpoint can be used.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Affiliates have been notified successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ReleaseVersion.class),
            examples = @ExampleObject(
                name = "Notification response",
                value = """
                    {
                      "releaseVersionId": 211924,
                      "createdAt": "2015-10-28T20:48:21.796Z",
                      "name": "First Version",
                      "description": "<p><b>First</b> version description <br/></p>",
                      "online": true,
                      "publishedAt": "2015-10-29",
                      "releaseFiles": [
                        {
                          "releaseFileId": 211928,
                          "label": "<p>Example file</p>",
                          "createdAt": "2015-10-29T14:44:52.682Z",
                          "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
                          "downloadUrl": "http://files.com/example.txt"
                        }
                      ]
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release Version not found."
    )
    @PostMapping(
        value = Routes.RELEASE_VERSION_NOTIFICATIONS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseVersion>
    updateReleaseVersionNotification(

        @Parameter(
            description = "Unique identifier of the Release Package.",
            required = true,
            example = "211920"
        )
        @PathVariable long releasePackageId,

        @Parameter(
            description = "Unique identifier of the Release Version.",
            required = true,
            example = "211924"
        )
        @PathVariable long releaseVersionId) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
            );
        }

        ReleaseVersion releaseVersion =
            releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage()
        );

        if (releaseVersion.isNotified()) {
            return new ResponseEntity<>(
                releaseVersion,
                HttpStatus.OK
            );
        }

        userNotifier.notifyReleasePackageUpdated(
            releaseVersion
        );

        releaseVersion.setNotified(true);

        releaseVersionRepository.save(
            releaseVersion
        );

        return new ResponseEntity<>(
            releaseVersion,
            HttpStatus.OK
        );
    }


    @DeleteMapping(
        value = Routes.RELEASE_VERSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public @ResponseBody
    ResponseEntity<Void> deactivateReleaseVersion(
        @PathVariable long releasePackageId,
        @PathVariable long releaseVersionId) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(
                releaseVersionId
            );

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
            );
        }

        ReleaseVersion releaseVersion =
            releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage()
        );

        if (releaseVersion.getReleaseType()
            .equalsIgnoreCase("online")) {

            return new ResponseEntity<>(
                HttpStatus.CONFLICT
            );
        }

        releasePackageAuditEvents.logDeletionOf(
            releaseVersion
        );

        releaseVersionAccessRepository.deleteByReleaseVersionId(
            releaseVersion.getReleaseVersionId()
        );

        releaseVersionRepository.delete(
            releaseVersion
        );

        return new ResponseEntity<>(
            HttpStatus.OK
        );
    }


    @GetMapping(
        value = Routes.RELEASE_VERSION_DEPENDENCY,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public Boolean checkVersionDependency(
        @PathVariable long releaseVersionId) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(
                releaseVersionId
            );

        if (releaseVersionOptional.isEmpty()) {
            return false;
        }

        String versionURI =
            releaseVersionOptional.get().getVersionURI();

        Long response =
            releaseVersionRepository.checkDependent(
                versionURI
            );

        return response != null && response == 1;
    }


    @PostMapping(
        value = Routes.RELEASE_VERSION_ARCHIVE_UPDATE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseVersion> updateArchive(
        @PathVariable long releaseVersionId,
        @RequestParam boolean isArchive) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(
                releaseVersionId
            );

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
            );
        }

        ReleaseVersion releaseVersion =
            releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage()
        );

        releaseVersion.setArchive(isArchive);
        releaseVersion.setPermissionType(NOT_SELECTED);

        releaseVersionAccessRepository
            .deleteReleaseVersionAccessByVersionId(
                releaseVersionId
            );

        if (isArchive) {
            releasePackageAuditEvents.logArchived(
                releaseVersion
            );
        } else {
            releasePackageAuditEvents.logUnarchived(
                releaseVersion
            );
        }

        return new ResponseEntity<>(
            releaseVersion,
            HttpStatus.OK
        );
    }


    @GetMapping(
        value = Routes.RELEASE_VERSION_DEPENDENCY_NAMES,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public List<ReleaseVersionCheckViewDTO> getDependentVersionsNames(
        @PathVariable long releaseVersionId) {

        ReleaseVersion releaseVersion =
            releaseVersionRepository
                .findById(releaseVersionId)
                .orElse(null);

        if (releaseVersion == null) {
            return new ArrayList<>();
        }

        return releaseVersionRepository.getDependentVersionNames(
            releaseVersion.getVersionURI()
        );
    }


    @PostMapping(
        value = Routes.RELEASE_PACKAGES_MASTER_PERMISSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> updateReleaseMasterConfig(
        @RequestBody Map<String, Object> request) {

        try {
            releaseVersionService.updateReleaseMasterConfig(
                request
            );

            return new ResponseEntity<>(
                HttpStatus.OK
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }


    @PutMapping(
        value = Routes.RELEASE_VERSIONS_PERMISSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> updateReleaseVersionPermissions(
        @RequestBody Map<String, Object> request) {

        List<Long> releases =
            (List<Long>) request.get("releases");

        String permissionType =
            (String) request.get("releasePackageType");

        List<String> users =
            (List<String>) request.get("users");

        if (releases == null || releases.isEmpty()) {
            return new ResponseEntity<>(
                HttpStatus.BAD_REQUEST
            );
        }

        ReleasePermissionType newPermission =
            ReleasePermissionType.valueOf(
                permissionType
            );

        List<ReleaseVersion> versions =
            releaseVersionRepository.findAllById(
                releases
            );

        if (versions.isEmpty()) {
            return new ResponseEntity<>(
                HttpStatus.NOT_FOUND
            );
        }

        Set<String> affectedTypes =
            new HashSet<>();

        for (ReleaseVersion releaseVersion : versions) {

            releaseVersion.setPermissionType(
                newPermission
            );

            if (releaseVersion.getReleaseType() != null) {
                affectedTypes.add(
                    releaseVersion
                        .getReleaseType()
                        .toUpperCase()
                );
            }

            releaseVersionAccessRepository
                .deleteByReleaseVersionId(
                    releaseVersion.getReleaseVersionId()
                );

            if (newPermission ==
                ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS
                && users != null
                && !users.isEmpty()) {

                List<ReleaseVersionAccess> accessList =
                    users.stream()
                        .map(userId -> {

                            ReleaseVersionAccess access =
                                new ReleaseVersionAccess();

                            access.setReleaseVersionId(
                                releaseVersion.getReleaseVersionId()
                            );

                            access.setUserId(
                                Long.valueOf(userId)
                            );

                            return access;
                        })
                        .toList();

                releaseVersionAccessRepository
                    .saveAll(accessList);
            }
        }

        releaseVersionRepository.saveAll(
            versions
        );

        return new ResponseEntity<>(
            HttpStatus.OK
        );
    }


    @GetMapping(
        value = Routes.RELEASE_PERMISSION,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ReleasePermissionRequestDTO>>
    getReleasePermissions() {

        List<ReleasePermissionRequestDTO> permissionDTOList =
            releaseVersionRepository.findAll().stream()
                .filter(java.util.Objects::nonNull)
                .filter(version ->
                    version.getPermissionType() != null
                        && version.getPermissionType() != NOT_SELECTED
                )
                .filter(version ->
                    version.getReleasePackage() != null
                )
                .map(version ->
                    new ReleasePermissionRequestDTO(
                        version.getReleaseVersionId(),
                        version.getReleasePackage().getName() != null
                            ? version.getReleasePackage().getName()
                            : "",
                        version.getName(),
                        version.getPermissionType(),
                        version.getReleaseType()
                    )
                )
                .toList();

        return ResponseEntity.ok(
            permissionDTOList
        );
    }


    @GetMapping(
        value = Routes.RELEASE_PERMISSION_VISIBILITY,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getUsersForReleaseVersion(
        @PathVariable Long releaseVersionId) {

        List<String> users =
            releaseVersionService.getUsersForReleaseVersion(
                releaseVersionId
            );

        return ResponseEntity.ok(users);
    }


    @GetMapping(
        value = Routes.VERSION_USER_ACCESS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getUsersByReleaseVersionId(
        @PathVariable Long releaseVersionId) {

        List<String> logins =
            releaseVersionAccessRepository
                .findLoginsByReleaseVersionId(
                    releaseVersionId
                );

        return ResponseEntity.ok(logins);
    }


    private static final String RELEASE_ID = "releaseId";


    @PutMapping(
        value = Routes.REVOKE_USER_ACCESS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> updateIndividualUserAccess(
        @RequestBody Map<String, Object> request) {

        String releaseId =
            request.get(RELEASE_ID) != null
                ? request.get(RELEASE_ID).toString()
                : null;

        String requestUser =
            (String) request.get("user");

        try {

            String resultMessage =
                releaseVersionService.revokeIndividualUserAccess(
                    releaseId,
                    requestUser
                );

            if (resultMessage.equals(
                "User access revoked successfully."
            )) {

                return ResponseEntity.ok(
                    resultMessage
                );
            }

            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resultMessage);

        } catch (JsonProcessingException e) {

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing user list.");
        }
    }


    @PutMapping(
        value = Routes.REVOKE_RELEASE_ACCESS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> releaseAccessRevoke(
        @RequestBody Map<String, Object> request) {

        String releaseId =
            request.get(RELEASE_ID) != null
                ? request.get(RELEASE_ID).toString()
                : null;

        String resultMessage =
            releaseVersionService.revokeAllReleaseAccess(
                releaseId
            );

        if (resultMessage.equals(
            "User access revoked successfully."
        )) {

            return ResponseEntity.ok(
                resultMessage
            );
        }

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(resultMessage);
    }


    @GetMapping(
        value = Routes.RELEASE_PACKAGE_PERMISSION_VISIBILITY,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<PermissionVisibilityResponse>>
    getPackageVisibility(
        @PathVariable Long releasePackageId) {

        List<ReleaseVersion> versions =
            releaseVersionRepository
                .findByReleasePackageIdAndArchiveFalse(
                    releasePackageId
                );

        List<PermissionVisibilityResponse> response =
            releaseVersionService.getVisibilityForVersions(
                versions
            );

        return ResponseEntity.ok(
            response
        );
    }


    @GetMapping(
        value = Routes.RELEASE_TYPES,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getAllReleaseTypes() {

        List<String> releaseTypes =
            releasePackageConfigRepository.findAll()
                .stream()
                .map(ReleasePackageConfig::getReleaseType)
                .distinct()
                .toList();

        return ResponseEntity.ok(
            releaseTypes
        );
    }


    @PutMapping(
        value = Routes.REVOKE_ALL_RELEASE_ACCESS,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> revokeAllVersionPermissions() {

        String result =
            releaseVersionService
                .revokeAllVersionLevelPermissions();

        return ResponseEntity.ok(
            result
        );
    }
}
