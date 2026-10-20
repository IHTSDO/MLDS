package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.DownloadErrorMessages;
import ca.intelliware.ihtsdo.mlds.security.DownloadException;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import com.codahale.metrics.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestBody;

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

    @Resource
    ReleaseUrlTrustChecker releaseUrlTrustChecker;

    // -------------------------------------------------------------------------
    // Get Release File
    // -------------------------------------------------------------------------

    @Operation(
        summary = "Get a single Release File",
        description = "Returns a Release File identified by its IDs."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Release File returned successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ReleaseFile.class),
            examples = @ExampleObject(
                name = "Release File response",
                value = """
                    {
                      "releaseFileId": 211928,
                      "label": "<p>Example file</p>",
                      "createdAt": "2015-10-29T14:44:52.682Z",
                      "clientDownloadUrl": "/api/releasePackages/211920/releaseVersions/211924/releaseFiles/211928/download",
                      "downloadUrl": "http://files.com/example.txt"
                    }
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "The current user is not authorized to access the Release File."
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release File not found."
    )
    @GetMapping(
        value = Routes.RELEASE_FILE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed({
        AuthoritiesConstants.USER,
        AuthoritiesConstants.MEMBER,
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseFile> getReleaseFile(
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

        @Parameter(
            description = "Unique identifier of the Release File.",
            required = true,
            example = "211928"
        )
        @PathVariable long releaseFileId) {

        Optional<ReleaseFile> optionalReleaseFile =
            releaseFileRepository.findById(releaseFileId);

        if (optionalReleaseFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = optionalReleaseFile.get();

        if (!authorizationChecker.canAccessReleaseVersion(
            releaseFile.getReleaseVersion())) {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Release Version and Release Package?
        authorizationChecker.checkCanEditReleasePackage(
            releaseFile.getReleaseVersion().getReleasePackage());

        return new ResponseEntity<>(releaseFile, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Create Release File
    // -------------------------------------------------------------------------

    @PostMapping(
        value = Routes.RELEASE_FILES,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseFile> createReleaseFile(
        @PathVariable long releasePackageId,
        @PathVariable long releaseVersionId,
        @RequestBody ReleaseFile body) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage());

        /*
         * This is a create operation. Do not allow an incoming ID such as 0
         * to make Spring Data JPA treat this entity as an existing record.
         * Hibernate must generate the ID using the configured sequence.
         */
        body.setReleaseFileId(null);

        /*
         * Set the owning side of the relationship before saving.
         */
        body.setReleaseVersion(releaseVersion);
        releaseVersion.addReleaseFile(body);

        ReleaseFile savedReleaseFile =
            releaseFileRepository.save(body);

        releasePackageAuditEvents.logCreationOf(savedReleaseFile);

        return new ResponseEntity<>(savedReleaseFile, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Update Release File
    // -------------------------------------------------------------------------

    @PutMapping(
        value = Routes.RELEASE_FILES,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public ResponseEntity<ReleaseFile> updateReleaseFile(
        @PathVariable long releasePackageId,
        @PathVariable long releaseVersionId,
        @RequestBody ReleaseFile body) {

        Optional<ReleaseVersion> releaseVersionOptional =
            releaseVersionRepository.findById(releaseVersionId);

        if (releaseVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseVersion releaseVersion = releaseVersionOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseVersion.getReleasePackage());

        if (body.getReleaseFileId() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<ReleaseFile> releaseFileOptional =
            releaseFileRepository.findById(body.getReleaseFileId());

        if (releaseFileOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = releaseFileOptional.get();

        releaseFile.setLabel(body.getLabel());
        releaseFile.setDownloadUrl(body.getDownloadUrl());
        releaseFile.setMd5Hash(body.getMd5Hash());
        releaseFile.setFileSize(body.getFileSize());
        releaseFile.setPrimaryFile(body.getPrimaryFile());

        return new ResponseEntity<>(releaseFile, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Delete Release File
    // -------------------------------------------------------------------------

    @RolesAllowed({
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @DeleteMapping(
        value = Routes.RELEASE_FILE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @Timed
    public ResponseEntity<ReleaseFile> deleteReleaseFile(
        @PathVariable long releasePackageId,
        @PathVariable long releaseVersionId,
        @PathVariable long releaseFileId) {

        Optional<ReleaseFile> releaseFileOptional =
            releaseFileRepository.findById(releaseFileId);

        if (releaseFileOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReleaseFile releaseFile = releaseFileOptional.get();

        authorizationChecker.checkCanEditReleasePackage(
            releaseFile.getReleaseVersion().getReleasePackage());

        releaseFileRepository.delete(releaseFile);

        releasePackageAuditEvents.logDeletionOf(releaseFile);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Download Release File
    // -------------------------------------------------------------------------

    @Operation(
        summary = "Download Release File Content",
        description = """
            Downloads the content associated with a Release File.

            The content can be downloaded using the value of the
            clientDownloadUrl.

            The supplied user credentials must be approved to get access
            to the member's files.

            Where possible, the response headers Content-Disposition and
            Content-Type are set with file content metadata, such as the
            filename.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Release File content returned successfully.",
        content = @Content(
            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            examples = @ExampleObject(
                name = "File download response",
                description = """
                    The response body contains the binary file content.
                    Example response headers:
                    Content-Disposition: attachment; filename="pdfSample.pdf"
                    Content-Type: application/pdf
                    Content-Length: 113801
                    """
            )
        )
    )
    @ApiResponse(
        responseCode = "403",
        description = "The supplied user credentials are not approved to access the member's files."
    )
    @ApiResponse(
        responseCode = "404",
        description = "Release File not found."
    )
    @GetMapping(value = Routes.RELEASE_FILE_DOWNLOAD)
    @RolesAllowed({
        AuthoritiesConstants.USER,
        AuthoritiesConstants.MEMBER,
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public void downloadReleaseFile(
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

        @Parameter(
            description = "Unique identifier of the Release File.",
            required = true,
            example = "211928"
        )
        @PathVariable long releaseFileId,

        HttpServletRequest request,
        HttpServletResponse response) throws IOException {

        TransactionTemplate transactionTemplate =
            new TransactionTemplate(transactionManager);

        transactionTemplate.setReadOnly(true);

        DownloadAuthorizationResult authResult =
            transactionTemplate.execute(status -> {

                Optional<ReleaseFile> releaseFileOptional =
                    releaseFileRepository.findById(releaseFileId);

                if (releaseFileOptional.isEmpty()) {
                    throw new DownloadException(
                        HttpStatus.NOT_FOUND,
                        DownloadErrorMessages.NOT_FOUND_TITLE,
                        DownloadErrorMessages.NOT_FOUND_SUBTITLE,
                        DownloadErrorMessages.NOT_FOUND_REASON
                    );
                }

                ReleaseFile releaseFile = releaseFileOptional.get();
                ReleaseVersion releaseVersion =
                    releaseFile.getReleaseVersion();

                authorizationChecker.checkCanDownloadReleaseVersion(
                    releaseVersion);

                if (!authorizationChecker.canAccessReleaseVersion(
                    releaseVersion)) {

                    ReleasePermissionType permission =
                        authorizationChecker.resolveEffectivePermission(
                            releaseVersion);

                    String requiredPermission =
                        authorizationChecker.getPermissionDescription(
                            permission);

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

                Affiliate affiliate =
                    userMembershipAccessor.getAffiliate();

                if (releaseVersion != null) {
                    releaseVersion.getName();

                    if (releaseVersion.getReleasePackage() != null) {
                        releaseVersion.getReleasePackage().getName();
                    }
                }

                return new DownloadAuthorizationResult(
                    releaseFile,
                    affiliate
                );
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
            String downloadUrl =
                authResult.releaseFile.getDownloadUrl();

            statusCode = uriDownloader.download(
                downloadUrl,
                request,
                response
            );
        } finally {
            releasePackageAuditEvents.logDownload(
                authResult.releaseFile,
                statusCode,
                authResult.affiliate
            );
        }
    }

    private static class DownloadAuthorizationResult {

        final ReleaseFile releaseFile;
        final Affiliate affiliate;

        public DownloadAuthorizationResult(
            ReleaseFile releaseFile,
            Affiliate affiliate) {

            this.releaseFile = releaseFile;
            this.affiliate = affiliate;
        }
    }

    // -------------------------------------------------------------------------
    // Check IHTSDO File
    // -------------------------------------------------------------------------

    @GetMapping(value = Routes.CHECK_IHTSDO_FILE)
    @RolesAllowed({
        AuthoritiesConstants.USER,
        AuthoritiesConstants.MEMBER,
        AuthoritiesConstants.STAFF,
        AuthoritiesConstants.ADMIN
    })
    @Timed
    public Boolean checkIhtsdoFile(
        @PathVariable long releasePackageId,
        @PathVariable long releaseVersionId,
        @PathVariable long releaseFileId,
        HttpServletRequest request,
        HttpServletResponse response) throws FileNotFoundException {

        Optional<ReleaseFile> releaseFileOptional =
            releaseFileRepository.findById(releaseFileId);

        if (releaseFileOptional.isEmpty()) {
            throw new FileNotFoundException("no such file found");
        }

        String downloadUrl =
            releaseFileOptional.get().getDownloadUrl();

        return releaseUrlTrustChecker.isInternalUrl(downloadUrl);
    }
}
