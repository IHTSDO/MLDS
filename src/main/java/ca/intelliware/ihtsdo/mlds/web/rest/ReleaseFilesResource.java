package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.DownloadErrorMessages;
import ca.intelliware.ihtsdo.mlds.security.DownloadException;
import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@RestController
public class ReleaseFilesResource {
    @Autowired
    ReleaseVersionRepository releaseVersionRepository;

    @Autowired
    ReleaseFileRepository releaseFileRepository;

    @Autowired
    ReleasePackageAuditEvents releasePackageAuditEvents;

    @Autowired
    ReleasePackageAuthorizationChecker authorizationChecker;

    @Autowired
    UriDownloader uriDownloader;

    @Autowired
    UserMembershipAccessor userMembershipAccessor;

    @Resource
    PlatformTransactionManager transactionManager;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Release Files

    @RequestMapping(value = Routes.RELEASE_FILE,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseFile> getReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {

        Optional<ReleaseFile> optionalReleaseFile = releaseFileRepository.findById(releaseFileId);

        if (optionalReleaseFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = optionalReleaseFile.get();

        if (!authorizationChecker.canAccessReleaseVersion(releaseFile.getReleaseVersion())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        //FIXME should we check children being consistent?
        authorizationChecker.checkCanEditReleasePackage(releaseFile.getReleaseVersion().getReleasePackage());

        return new ResponseEntity<ReleaseFile>(releaseFile, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_FILES,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseFile> createReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseFile body) {

        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

        releaseFileRepository.save(body);
        releaseVersion.addReleaseFile(body);

        releasePackageAuditEvents.logCreationOf(body);

        return new ResponseEntity<ReleaseFile>(body, HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_FILES,
            method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<ReleaseFile> updateReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseFile body) {

        Optional<ReleaseVersion> releaseVersionOptional = releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

        Optional<ReleaseFile> releaseFileOptional = releaseFileRepository.findById(body.getReleaseFileId());


        if (releaseFileOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = releaseFileOptional.get();

        releaseFile.setLabel(body.getLabel());
        releaseFile.setDownloadUrl(body.getDownloadUrl());
        releaseFile.setMd5Hash(body.getMd5Hash());
        releaseFile.setFileSize(body.getFileSize());
        releaseFile.setPrimaryFile(body.getPrimaryFile());
        return new ResponseEntity<ReleaseFile>(releaseFile, HttpStatus.OK);
    }


    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.RELEASE_FILE,
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @Timed
    public @ResponseBody ResponseEntity<ReleaseFile> deleteReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {


        Optional<ReleaseFile> releaseFileOptional = releaseFileRepository.findById(releaseFileId);

        if (releaseFileOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = releaseFileOptional.get();

        authorizationChecker.checkCanEditReleasePackage(releaseFile.getReleaseVersion().getReleasePackage());

        releaseFileRepository.delete(releaseFile);

        releasePackageAuditEvents.logDeletionOf(releaseFile);

        return new ResponseEntity<ReleaseFile>(HttpStatus.OK);
    }

    @RequestMapping(value = Routes.RELEASE_FILE_DOWNLOAD,
            method = RequestMethod.GET)
    @RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody
    void downloadReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);

        DownloadAuthorizationResult authResult = transactionTemplate.execute(status -> {
            Optional<ReleaseFile> releaseFileOptional = releaseFileRepository.findById(releaseFileId);

            if (releaseFileOptional.isEmpty()) {
                throw new DownloadException(
                    HttpStatus.NOT_FOUND,
                    DownloadErrorMessages.NOT_FOUND_TITLE,
                    DownloadErrorMessages.NOT_FOUND_SUBTITLE,
                    DownloadErrorMessages.NOT_FOUND_REASON
                );
            }

            ReleaseFile releaseFile = releaseFileOptional.get();
            ReleaseVersion releaseVersion = releaseFile.getReleaseVersion();

            authorizationChecker.checkCanDownloadReleaseVersion(releaseFile.getReleaseVersion());

            if (!authorizationChecker.canAccessReleaseVersion(releaseVersion)) {

                ReleasePermissionType permission =
                    authorizationChecker.resolveEffectivePermission(releaseVersion);

                String requiredPermission =
                    authorizationChecker.getPermissionDescription(permission);

                throw new DownloadException(
                    HttpStatus.FORBIDDEN,
                    DownloadErrorMessages.PERMISSION_TITLE,
                    DownloadErrorMessages.PERMISSION_SUBTITLE,
                    String.format(
                        "You need \"%s\" permissions to download this content. Please request access to this release.",
                        requiredPermission
                    )
                );
            }

            Affiliate affiliate = userMembershipAccessor.getAffiliate();

            if (releaseVersion != null) {
                releaseVersion.getName();
                if (releaseVersion.getReleasePackage() != null) {
                    releaseVersion.getReleasePackage().getName();
                }
            }

            return new DownloadAuthorizationResult(releaseFile, affiliate);
        });

        if (authResult == null) {
            throw new DownloadException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Download Error",
                "An error occurred while authorizing your download.",
                "Transaction returned null authorization result"
            );
        }

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        try {
            String downloadUrl = authResult.releaseFile.getDownloadUrl();
            statusCode = uriDownloader.download(downloadUrl, request, response);
        } finally {
            releasePackageAuditEvents.logDownload(authResult.releaseFile, statusCode, authResult.affiliate);
        }
    }

    private static class DownloadAuthorizationResult {
        final ReleaseFile releaseFile;
        final Affiliate affiliate;

        public DownloadAuthorizationResult(ReleaseFile releaseFile, Affiliate affiliate) {
            this.releaseFile = releaseFile;
            this.affiliate = affiliate;
        }
    }
    /*MLDS-1013 Add disclaimers or warnings for externally linked release packages -- Check whether file is from IHTSDO Domain*/
    @GetMapping(value = Routes.CHECK_IHTSDO_FILE)
    @RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public Boolean checkIhtsdoFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId,
                                                 HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        Optional<ReleaseFile> releaseFileOptional = releaseFileRepository.findById(releaseFileId);
        if (releaseFileOptional.isEmpty()) {
            throw new FileNotFoundException("no such file found");
        }
        ReleaseFile releaseFile = releaseFileOptional.get();
        String downloadUrl = releaseFile.getDownloadUrl();
        return downloadUrl.contains("ihtsdo");
    }
}
