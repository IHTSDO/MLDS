package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.ReleasePackagePrioritizer;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
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
    ReleaseVersionAccessRepository releaseVersionAccessRepository;


    ReleasePackageConfigRepository releasePackageConfigRepository;


    UserRepository userRepository;



    public ReleasePackagesResource(UserRepository userRepository, ReleasePackageConfigRepository releasePackageConfigRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.releasePackageConfigRepository = releasePackageConfigRepository;
        this.sessionService = sessionService;
    }

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

        if (currentSecurityContext.isAdmin()) {
            return ResponseEntity.ok(releasePackages);
        }

        ReleasePackageAuthorizationChecker.AccessContext context =
            authorizationChecker.buildAccessContext(releasePackages);

        for (ReleasePackage releasePackage : releasePackages) {

            Set<ReleaseVersion> filteredVersions = releasePackage.getReleaseVersions().stream()
                .filter(version -> {
                    if (version.isArchive()) {
                        return currentSecurityContext.isStaffFor(releasePackage.getMember());
                    }
                    return authorizationChecker.canAccessReleaseVersion(version, context);
                })
                .collect(Collectors.toSet());

            releasePackage.setReleaseVersions(filteredVersions);
        }

        return ResponseEntity.ok(
            releasePackages.stream()
                .filter(rp ->
                    !rp.getReleaseVersions().isEmpty()
                        || (rp.getReleaseVersions().isEmpty()
                        && currentSecurityContext.isStaffFor(rp.getMember()))
                )
                .toList()
        );
    }


    @GetMapping(value = Routes.ARCHIVE_RELEASE_PACKAGES,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF})
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
            if (version.getReleaseType().equalsIgnoreCase(RELEASE_TYPE_ONLINE) || version.getReleaseType().equalsIgnoreCase("alpha/beta")) {
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

        if (optionalReleasePackage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = optionalReleasePackage.get();

        if (currentSecurityContext.isAdmin()) {
            return ResponseEntity.ok(releasePackage);
        }

        boolean isStaffOwner = currentSecurityContext.isStaffFor(releasePackage.getMember());

        Set<ReleaseVersion> allowedVersions = releasePackage.getReleaseVersions().stream()
            .filter(version -> {
                if (version.isArchive()) {
                    return isStaffOwner;
                }
                return authorizationChecker.canAccessReleaseVersion(version);
            })
            .collect(Collectors.toSet());

        if (allowedVersions.isEmpty()
            && !isStaffOwner) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        releasePackage.setReleaseVersions(allowedVersions);

        return ResponseEntity.ok(releasePackage);
    }

    private ReleasePackage filterReleasePackageByAuthority(ReleasePackage releasePackage) {
        if (authorizationChecker.shouldSeeOfflinePackages()) {
            return releasePackage;
        }

        Set<ReleaseVersion> releaseVersions = releasePackage.getReleaseVersions().stream()
            .filter(version -> version.getReleaseType().equalsIgnoreCase(RELEASE_TYPE_ONLINE) ||
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
    private static final String RELEASE_TYPE_ONLINE = "online";
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
            if (RELEASE_TYPE_ONLINE.equalsIgnoreCase(releaseVersion.getReleaseType())) {
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

    @GetMapping(value = Routes.MASTER_RELEASE_PERMISSION, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ReleasePackageConfig>> getMasterReleasePermissions() {
        List<ReleasePackageConfig> masterPermissionList = releasePackageConfigRepository.findAll().stream()
            .filter(releasePackage -> !NOT_SELECTED.equals(releasePackage.getReleasePermissionType()))
            .toList();

        return ResponseEntity.ok(masterPermissionList);
    }

    @GetMapping(value = Routes.MASTER_CONFIG_USER_ACCESS, produces = MediaType.APPLICATION_JSON_VALUE)
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

        List<String> logins = releaseVersionAccessRepository.findLoginsByUserIds(userIds);
        return ResponseEntity.ok(logins);
    }

}
