package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.ReleasePackageAccessService;
import ca.intelliware.ihtsdo.mlds.service.ReleasePackagePrioritizer;
import ca.intelliware.ihtsdo.mlds.service.ReleasePackageService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleasePermissionRequestDTO;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReleasePackagesResource {

    @Autowired
    EntityManager entityManager;
    @Autowired
    BlobHelper blobHelper;
    @Autowired
    FileRepository fileRepository;
    SessionService sessionService;

    @Autowired
    ReleasePackageRepository releasePackageRepository;

    @Resource
    ReleasePackageAuthorizationChecker authorizationChecker;

    @Resource
    CurrentSecurityContext currentSecurityContext;

    @Resource
    ReleasePackageAuditEvents releasePackageAuditEvents;

    @Resource
    UserMembershipAccessor userMembershipAccessor;

    @Autowired
    ReleaseFilePrivacyFilter releaseFilePrivacyFilter;

    @Autowired
    ReleasePackagePrioritizer releasePackagePrioritizer;


    @Autowired
    ReleasePackageAccessRepository releasePackageAccessRepository;


    ReleasePackageConfigRepository releasePackageConfigRepository;


    UserRepository userRepository;


    ReleasePackageService releasePackageService;


    public ReleasePackagesResource(ReleasePackageAccessService releasePackageAccessService, UserRepository userRepository, ReleasePackageConfigRepository releasePackageConfigRepository, ReleasePackageService releasePackageService, SessionService sessionService) {
        this.releasePackageAccessService = releasePackageAccessService;
        this.userRepository = userRepository;
        this.releasePackageConfigRepository = releasePackageConfigRepository;
        this.releasePackageService = releasePackageService;
        this.sessionService = sessionService;
    }

    ReleasePackageAccessService releasePackageAccessService;

    private static final String NOT_SELECTED = "NOT_SELECTED";


//
//	////////////////////////////////////////////////////////////////////////////////////////////////////////
//	// Release Packages

    @GetMapping(value = Routes.RELEASE_PACKAGES,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PermitAll
    @Timed
    public ResponseEntity<Collection<ReleasePackage>> getReleasePackages() {

            Collection<ReleasePackage> releasePackages = releasePackageRepository.findAll();

            //admin role checks - no restriction to admin role returns all the packages
            if(currentSecurityContext.isAdmin()){
                return new ResponseEntity<>(releasePackages, HttpStatus.OK);
            }

            // split online alphabeta offline package logic
            List<ReleasePackage> onlinePackages = new ArrayList<>();
            List<ReleasePackage> alphaBetaPackages = new ArrayList<>();
            List<ReleasePackage> offlinePackages = new ArrayList<>();
            List<ReleasePackage> finalMasterResult = new ArrayList<>();

            for (ReleasePackage eachReleasePackage : releasePackages) {
                releasePackageService.classifyPackage(eachReleasePackage, onlinePackages, alphaBetaPackages, offlinePackages);
            }

            if (currentSecurityContext.isStaff() || currentSecurityContext.isMember()) {
                Collection<ReleasePackage> staffAccessiblePackages = releasePackageAccessService.getAccessiblePackagesForStaff(
                    releasePackages,
                    onlinePackages,
                    alphaBetaPackages,
                    offlinePackages,
                    currentSecurityContext.getStaffMemberKey()
                );
                return ResponseEntity.ok(staffAccessiblePackages);
            }

            if (currentSecurityContext.isUser()) {
                Collection<ReleasePackage> userAccessiblePackages =
                    releasePackageAccessService.getAccessiblePackagesForUser(
                        releasePackages,
                        onlinePackages,
                        alphaBetaPackages,
                        offlinePackages,
                        currentSecurityContext.getCurrentUserName()
                    );

                return new ResponseEntity<>(userAccessiblePackages, HttpStatus.OK);
            }

            if (!currentSecurityContext.isUser()
                && !currentSecurityContext.isAdmin()
                && !currentSecurityContext.isStaff()) {

                Collection<ReleasePackage> result = releasePackageAccessService
                    .getAccessiblePackagesForUnauthenticatedUser(
                        releasePackages,
                        onlinePackages,
                        alphaBetaPackages,
                        offlinePackages
                    );

                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            return new ResponseEntity<>(finalMasterResult, HttpStatus.OK);
    }

    @GetMapping(value = Routes.ARCHIVE_RELEASE_PACKAGES,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<List<ReleasePackage>> getArchiveReleasePackages() {

        List<ReleasePackage> releasePackages = releasePackageRepository.findAll();

        releasePackages = filterReleasePackagesByOnline(releasePackages);
        List<ReleasePackage> response = releasePackages.stream()
            .map(releasePackage -> {
                Set<ReleaseVersion> archivedVersions = releasePackage.getReleaseVersions().stream()
                    .filter(ReleaseVersion::isArchive)
                    .collect(Collectors.toSet());
                if (!archivedVersions.isEmpty()) {
                    releasePackage.setReleaseVersions(archivedVersions); // Keep only archived versions
                    return releasePackage; // Include this package
                }
                return null; // Skip this package
            })
            .filter(Objects::nonNull) // Exclude null packages
            .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<ReleasePackage> filterReleasePackagesByOnline(
        List<ReleasePackage> releasePackages) {

        List<ReleasePackage> result = releasePackages;

        if (!authorizationChecker.shouldSeeOfflinePackages()) {
            result = new ArrayList<>();
            for (ReleasePackage releasePackage : releasePackages) {
                if (isPackagePublished(releasePackage)) {
                    result.add(filterReleasePackageByAuthority(releasePackage));
                }
            }
        }

        return result;
    }

    private boolean isPackagePublished(ReleasePackage releasePackage) {
        for (ReleaseVersion version : releasePackage.getReleaseVersions()) {
            if (version.getReleaseType().equalsIgnoreCase("online") || version.getReleaseType().equalsIgnoreCase("alpha/beta")) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = Routes.RELEASE_PACKAGES,
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleasePackage> createReleasePackage(@RequestBody ReleasePackage releasePackage) {
        authorizationChecker.checkCanCreateReleasePackages();

        releasePackage.setCreatedBy(currentSecurityContext.getCurrentUserName());

        // MLDS-740 - Allow Admin to specify the member
        if (releasePackage.getMember() == null || !currentSecurityContext.isAdmin()) {
            releasePackage.setMember(userMembershipAccessor.getMemberAssociatedWithUser());
        }

        releasePackagePrioritizer.prioritize(releasePackage, ReleasePackagePrioritizer.END_PRIORITY);

        releasePackageRepository.save(releasePackage);

        releasePackageAuditEvents.logCreationOf(releasePackage);

        ResponseEntity<ReleasePackage> result = new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
        return result;
    }


    @GetMapping(value = Routes.RELEASE_PACKAGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public ResponseEntity<ReleasePackage> getReleasePackage(@PathVariable long releasePackageId) {
        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        if (optionalReleasePackage.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ReleasePackage releasePackage = optionalReleasePackage.get();
        String releaseType = releasePackageService.categorizePackage(releasePackage.getReleaseVersions());
        ReleasePackageConfig config = releasePackageConfigRepository.findByReleaseType(releaseType);
        ReleasePackageConfig masterConfig = releasePackageConfigRepository.findByReleaseType("ALL");

        if (currentSecurityContext.isAdmin()) {
            return ResponseEntity.ok(releasePackage);
        }

        if (currentSecurityContext.isStaff() || currentSecurityContext.isMember()) {
            if (releasePackageAccessService.isStaffOwner(releasePackage)) return ResponseEntity.ok(releasePackage);
            if (releasePackageAccessService.isAdminOnly(releasePackage, config, masterConfig)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(releasePackage);
        }

        if (currentSecurityContext.isUser()) {
            return releasePackageAccessService.handleUserAccess(releasePackage, config, masterConfig, releasePackageId);
        }

        return releasePackageAccessService.handlePublicAccess(releasePackage, config, masterConfig);
    }

    private ReleasePackage filterReleasePackageByAuthority(ReleasePackage releasePackage) {
        if (authorizationChecker.shouldSeeOfflinePackages()) {
            return releasePackage;
        }

        Set<ReleaseVersion> releaseVersions = releasePackage.getReleaseVersions().stream()
            .filter(version -> version.getReleaseType().equalsIgnoreCase("online") ||
                (authorizationChecker.shouldSeeAlphaBetaPackages() && version.getReleaseType().equalsIgnoreCase("alpha/beta")))
            .map(releaseFilePrivacyFilter::filterReleaseVersionByAuthority)
            .collect(Collectors.toSet());

        releasePackage.setReleaseVersions(releaseVersions);
        return releasePackage;
    }

    @RequestMapping(value = Routes.RELEASE_PACKAGE,
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleasePackage> updateReleasePackage(@PathVariable long releasePackageId, @RequestBody ReleasePackage body) {

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);

        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = optionalReleasePackage.get();

        //FIXME should we check children being consistent?
        authorizationChecker.checkCanEditReleasePackage(releasePackage);

        releasePackage.setName(body.getName());
        releasePackage.setDescription(body.getDescription());
        releasePackage.setReleasePackageURI(body.getReleasePackageURI());
        releasePackage.setCopyrights(body.getCopyrights());
        if (currentSecurityContext.isAdmin()) {
            releasePackage.setMember(body.getMember());
        }
        releasePackagePrioritizer.prioritize(releasePackage, body.getPriority());

        releasePackageRepository.save(releasePackage);

        return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_PACKAGE,
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<?> deactivateReleasePackage(@PathVariable long releasePackageId) {

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);

        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = optionalReleasePackage.get();

        authorizationChecker.checkCanEditReleasePackage(releasePackage);

        for (ReleaseVersion releaseVersion : releasePackage.getReleaseVersions()) {
            if (releaseVersion.isOnline()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        releasePackageAuditEvents.logDeletionOf(releasePackage);

        // Actually mark releasePackage as being inactive and then hide from subsequent calls rather than sql delete from the db
        releasePackageRepository.delete(releasePackage);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_PACKAGE_LICENSE,
        method = RequestMethod.GET)
    @PermitAll
    @Transactional
    @Timed
    public ResponseEntity<?> getReleasePackageLicense(@PathVariable long releasePackageId, HttpServletRequest request) throws SQLException, IOException {

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File license = optionalReleasePackage.get().getLicenceFile();
        return downloadFile(request, license);
    }

    private ResponseEntity<?> downloadFile(HttpServletRequest request, File file) throws SQLException, IOException {
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (file.getLastUpdated() != null) {
            long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            Instant lastUpdatedInstant = file.getLastUpdated();
            long lastUpdatedMillis = lastUpdatedInstant.toEpochMilli();
            long lastUpdatedSecondsFloor = (lastUpdatedMillis / 1000) * 1000;
            if (ifModifiedSince != -1 && lastUpdatedSecondsFloor <= ifModifiedSince) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(file.getMimetype()));
        httpHeaders.setContentLength(file.getContent().length());
        httpHeaders.setContentDispositionFormData("file", file.getFilename());
        if (file.getLastUpdated() != null) {
            Date lastUpdatedDate = Date.from(file.getLastUpdated());
            long lastModified = lastUpdatedDate.getTime();
            httpHeaders.setLastModified(lastModified);
        }

        byte[] byteArray = IOUtils.toByteArray(file.getContent().getBinaryStream());
        org.springframework.core.io.Resource contents = new ByteArrayResource(byteArray);
        return new ResponseEntity<org.springframework.core.io.Resource>(contents, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_PACKAGE_LICENSE,
        method = RequestMethod.POST,
        headers = "content-type=multipart/*",
        produces = "application/json")
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Transactional
    @Timed
    public ResponseEntity<?> updateReleasePackageLicense(@PathVariable long releasePackageId, @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws IOException {

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);

        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = optionalReleasePackage.get();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            File licenseFile = updateFile(multipartFile, releasePackage.getLicenceFile());
            releasePackage.setLicenceFile(licenseFile);
        }

        releasePackageRepository.save(releasePackage);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private File updateFile(MultipartFile multipartFile, File existingFile) throws IOException {
        File newFile = new File();

        if (existingFile != null) {
            entityManager.detach(existingFile);
        }

        Blob blob = blobHelper.createBlobFrom(multipartFile);
        newFile.setContent(blob);
        newFile.setCreator(sessionService.getUsernameOrNull());
        newFile.setFilename(multipartFile.getOriginalFilename());
        newFile.setMimetype(multipartFile.getContentType());
        newFile.setLastUpdated(Instant.now());

        fileRepository.save(newFile);
        return newFile;
    }

    @PutMapping(value = Routes.RELEASE_PACKAGE_PERMISSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<ReleasePackage> updateReleasePackageType(@PathVariable long releasePackageId, @RequestBody Map<String, Object> request) {

        Optional<ReleasePackage> optionalReleasePackage = releasePackageRepository.findById(releasePackageId);
        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ReleasePackage releasePackage = optionalReleasePackage.get();
        authorizationChecker.checkCanEditReleasePackage(releasePackage);
        String releasePackageType = (String) request.get("releasePackageType");
        if (releasePackageType != null) {
            releasePackage.setPermissionType(ReleasePermissionType.valueOf(releasePackageType));
        }

        if(releasePackage.getPermissionType() == ReleasePermissionType.ADMIN_STAFF_SELECTED_USERS) {
            List<String> users = (List<String>) request.get("users");
            if (users != null && !users.isEmpty()) {
                users.forEach(user ->{
                    ReleasePackageAccess access = new ReleasePackageAccess();
                    access.setReleasePackageId(releasePackage.getReleasePackageId());
                    access.setUserId(Long.parseLong(user));
                    releasePackageAccessRepository.save(access);
                });
            }
        }

        releasePackageRepository.save(releasePackage);
        return new ResponseEntity<>(releasePackage, HttpStatus.OK);
    }

    @PutMapping(value = Routes.RELEASE_PACKAGES_PERMISSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> updateReleasePackagesType(@RequestBody Map<String, Object> request) {
        releasePackageService.updateReleasePackagesPermission(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = Routes.RELEASE_PACKAGES_MASTER_PERMISSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> updateReleaseMasterConfig(@RequestBody Map<String, Object> request) {
        try {
            releasePackageService.updateReleaseMasterConfig(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping(value = "/api/releasePermission", produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ReleasePermissionRequestDTO>> getReleasePermissions() {
        List<ReleasePermissionRequestDTO> permissionDTOList = releasePackageRepository.findAll().stream()
            .filter(releasePackage -> releasePackage.getPermissionType() != ReleasePermissionType.NOT_SELECTED)
            .map(releasePackage -> new ReleasePermissionRequestDTO(
                releasePackage.getReleasePackageId(),
                releasePackage.getName(),
                releasePackage.getPermissionType())
            )
            .toList();

        return ResponseEntity.ok(permissionDTOList);
    }


    @GetMapping(value = "/api/masterReleasePermission", produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ReleasePackageConfig>> getMasterReleasePermissions() {
        List<ReleasePackageConfig> masterPermissionList = releasePackageConfigRepository.findAll().stream()
            .filter(releasePackage -> !NOT_SELECTED.equals(releasePackage.getReleasePermissionType()))
            .toList();

        return ResponseEntity.ok(masterPermissionList);
    }


    @GetMapping("/api/{releasePackageId}/usersAccess")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getUsersByReleasePackageId(@PathVariable Long releasePackageId) {
        List<String> logins = releasePackageAccessRepository.findLoginsByReleasePackageId(releasePackageId);
        return ResponseEntity.ok(logins);
    }

    @GetMapping("/api/releaseTypes")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getAllReleaseTypes() {
        List<String> releaseTypes = releasePackageConfigRepository.findAll()
            .stream()
            .map(ReleasePackageConfig::getReleaseType)
            .distinct()
            .toList();

        return ResponseEntity.ok(releaseTypes);
    }


    @GetMapping("/api/masterUsersAccess")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<String>> getMasterAccessUsersByReleasePackageId(@RequestParam String releaseType) {
        ReleasePackageConfig result = releasePackageConfigRepository.findByReleaseType(releaseType);

        if (result == null || result.getUserList() == null || result.getUserList().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Long> userIds;

        try {
            userIds = new ObjectMapper().readValue(result.getUserList(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList("Failed to parse user list"));
        }

        if (userIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> logins = releasePackageAccessRepository.findLoginsByUserIds(userIds);
        return ResponseEntity.ok(logins);
    }
    private static final String RELEASE_ID = "releaseId";

    @PutMapping(value = "/api/userAccessRevoke", produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> updateIndividualUserAccess(@RequestBody Map<String, Object> request) {
        String releaseId = request.get(RELEASE_ID) != null ? request.get(RELEASE_ID).toString() : null;
        String requestUser = (String) request.get("user");

        try {
            String resultMessage = releasePackageService.revokeIndividualUserAccess(releaseId, requestUser);
            if (resultMessage.equals("User access revoked successfully.")) {
                return ResponseEntity.ok(resultMessage);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMessage);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing user list.");
        }
    }

    @PutMapping(value = "/api/releaseAccessRevoke", produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<String> releaseAccessRevoke(@RequestBody Map<String, Object> request) {
        String releaseId = request.get(RELEASE_ID) != null ? request.get(RELEASE_ID).toString() : null;

        String resultMessage = releasePackageService.revokeAllReleaseAccess(releaseId);
        if (resultMessage.equals("User access revoked successfully.")) {
            return ResponseEntity.ok(resultMessage);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMessage);
    }


    @GetMapping("/api/viewVisiblity/{releasePackageId}")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<PermissionVisibilityResponse> getVisibilityDetails(@PathVariable Long releasePackageId) {
        String releaseType = "ALL";
        ReleasePackageConfig masterPermission = releasePackageService.getMasterPermission(releaseType);

        if (masterPermission != null && Boolean.FALSE.equals(masterPermission.getActive())) {
            Optional<ReleasePackage> releasePackage = releasePackageRepository.findById(releasePackageId);

            if (releasePackage.isPresent()) {
                PermissionVisibilityResponse response = releasePackageService.getVisibilityDetails(releasePackage.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            String permissionType = masterPermission != null ? masterPermission.getReleasePermissionType() : "";
            PermissionVisibilityResponse response = new PermissionVisibilityResponse(true, permissionType, releaseType);
            return ResponseEntity.ok(response);
        }
    }


    @PostMapping("/api/releasePackages/checkConfigPermissionType")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Boolean> checkReleaseMasterConfig(@RequestBody Map<String, Object> request) {
        String releasePackageType = (String) request.get("releaseType");

        List<ReleasePackage> allReleasePackages = releasePackageRepository.findAll();
        List<ReleasePackageConfig> configuredReleaseConfigs = releasePackageConfigRepository.findByReleasePermissionTypeNot(NOT_SELECTED);

        boolean hasAnyConfiguredPermission = allReleasePackages.stream()
            .anyMatch(rp -> rp.getPermissionType() != ReleasePermissionType.NOT_SELECTED);

        boolean hasMasterConfigured = configuredReleaseConfigs.stream()
            .anyMatch(rp -> !"ALL".equals(rp.getReleaseType()));

        if (Objects.equals(releasePackageType, "ALL")) {
            return ResponseEntity.ok(hasAnyConfiguredPermission || hasMasterConfigured);
        } else {
            boolean hasAllTypeConfigured = configuredReleaseConfigs.stream()
                .anyMatch(rp -> "ALL".equals(rp.getReleaseType()));

            if (hasAllTypeConfigured) {
                return ResponseEntity.ok(true);
            }

            Set<String> categorizationResults = allReleasePackages.stream()
                .filter(rp -> rp.getPermissionType() != ReleasePermissionType.NOT_SELECTED)
                .map(rp -> releasePackageService.categorizePackage(rp.getReleaseVersions()))
                .collect(Collectors.toSet());

            return ResponseEntity.ok(categorizationResults.contains(releasePackageType.toLowerCase()));
        }
    }


    @PostMapping("/api/releasePackages/checkUpdatePermissionType")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Boolean> checkReleasePackageUpdate(@RequestBody Map<String, Object> request) {
        List<Long> releasePackages = (List<Long>) request.get("releases");

        if(Boolean.TRUE.equals(releasePackageConfigRepository.findByReleaseType("ALL").getActive())){
            return ResponseEntity.ok(true);
        }

        List<ReleasePackageConfig> masterConfigurationList = releasePackageConfigRepository.findByReleasePermissionTypeNot(NOT_SELECTED).stream()
            .filter(rp -> !"ALL".equals(rp.getReleaseType())).toList();
        if(!masterConfigurationList.isEmpty()) {
            List<ReleasePackage> updatereleaseList = releasePackageRepository.findAllByReleasePackageIdIn(releasePackages);
            Set<String> categorizationResults = new HashSet<>();
            for (ReleasePackage updateReleasePackage : updatereleaseList) {
                String type = releasePackageService.categorizePackage(updateReleasePackage.getReleaseVersions());
                categorizationResults.add(type);
            }

            for (ReleasePackageConfig releasePackageConfig : masterConfigurationList) {
                if (categorizationResults.contains(releasePackageConfig.getReleaseType().toLowerCase())) {
                    return ResponseEntity.ok(true);
                }
            }
        }

        return ResponseEntity.ok(false);
    }

}
