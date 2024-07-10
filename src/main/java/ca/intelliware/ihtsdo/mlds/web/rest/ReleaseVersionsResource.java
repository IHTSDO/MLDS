package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Release Versions

    @RequestMapping(value = Routes.RELEASE_VERSIONS,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> createReleaseVersion(@PathVariable long releasePackageId, @RequestBody ReleaseVersion releaseVersion) {

        Optional<ReleasePackage> releasePackageOptional = releasePackageRepository.findById(releasePackageId);

        if (releasePackageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleasePackage releasePackage = releasePackageOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releasePackage);

        releaseVersion.setId(String.valueOf(UUID.randomUUID()));

        releaseVersion.setCreatedBy(currentSecurityContext.getCurrentUserName());

        releaseVersionRepository.save(releaseVersion);

        releasePackage.addReleaseVersion(releaseVersion);

        releasePackageAuditEvents.logCreationOf(releaseVersion);

        ResponseEntity<ReleaseVersion> result = new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
        return result;
    }

    @RequestMapping(value = Routes.RELEASE_VERSION,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> getReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {
        //FIXME should we check children being consistent?


        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();
        authorizationChecker.checkCanAccessReleaseVersion(releaseVersion);
        releaseVersion = releaseFilePrivacyFilter.filterReleaseVersionByAuthority(releaseVersion);

        return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_VERSION,
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseVersion body) {

        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());
        /*MLDS*/
        String preOnline = String.valueOf(releaseVersion.getReleaseType() == "offline "
                || releaseVersion.getReleaseType() == "alpha/beta");

        releaseVersion.setName(body.getName());
        releaseVersion.setDescription(body.getDescription());
        releaseVersion.setReleaseType(body.getReleaseType());
        releaseVersion.setPublishedAt(body.getPublishedAt());

        releaseVersion.setSummary(body.getSummary());
        releaseVersion.setReleaseType(body.getReleaseType());
        releaseVersion.setVersionDependentURI(body.getVersionDependentURI());
        releaseVersion.setVersionDependentDerivativeURI(body.getVersionDependentDerivativeURI());
        releaseVersion.setVersionURI(body.getVersionURI());
        releaseVersion.setId(String.valueOf(UUID.randomUUID()));
        releaseVersion.setLastUpdated(Instant.now());
        releaseVersion.setPackageType(body.getPackageType());

        if (!Objects.equal(preOnline, releaseVersion.getReleaseType())) {
            if (releaseVersion.getReleaseType().equalsIgnoreCase("online")) {
                releasePackageAuditEvents.logTakenOnline(releaseVersion);
            } else if (releaseVersion.getReleaseType().equalsIgnoreCase("offline")) {
                releasePackageAuditEvents.logTakenOffline(releaseVersion);
            } else {
                releasePackageAuditEvents.logTakenAlphaAndBeta(releaseVersion);
            }
        }
        return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_VERSION_NOTIFICATIONS,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersionNotification(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {

        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

        userNotifier.notifyReleasePackageUpdated(releaseVersion);

        return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_VERSION,
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody
    ResponseEntity<?> deactivateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {

        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());
        if (releaseVersion.getReleaseType().equalsIgnoreCase("online")) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        releasePackageAuditEvents.logDeletionOf(releaseVersion);
        releaseVersionRepository.delete(releaseVersion);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value = Routes.RELEASE_VERSION_DEPENDENCY,
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public Boolean checkVersionDependency( @PathVariable long releaseVersionId) {
        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);
        if (releaseVersionOptional.isEmpty()) {
            return false;
        }
        String versionURI=releaseVersionOptional.get().getVersionURI();
        Long response = releaseVersionRepository.checkDependent(versionURI);
        return response != null && response == 1;
    }
}
