package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.intelliware.ihtsdo.mlds.domain.File;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.BlobHelper;
import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Sets;

@RestController
public class ReleasePackagesResource {

	@Resource EntityManager entityManager;
	@Resource BlobHelper blobHelper;
	@Resource FileRepository fileRepository;
	@Resource SessionService sessionService;
	
	@Resource
	ReleasePackageRepository releasePackageRepository;

	@Resource
	ReleasePackageAuthorizationChecker authorizationChecker;
	
	@Resource
	CurrentSecurityContext currentSecurityContext;

	@Resource
	ReleasePackageAuditEvents releasePackageAuditEvents;
	
	@Resource
	UserMembershipAccessor userMembershipAccessor;
	
	@Resource
	ReleaseFilePrivacyFilter releaseFilePrivacyFilter;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Packages

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@PermitAll
	@Timed
    public @ResponseBody ResponseEntity<Collection<ReleasePackage>> getReleasePackages() {
		
    	Collection<ReleasePackage> releasePackages = releasePackageRepository.findAll();
    	
		releasePackages = filterReleasePackagesByOnline(releasePackages);
    	
    	return new ResponseEntity<Collection<ReleasePackage>>(releasePackages, HttpStatus.OK);
    }

	private Collection<ReleasePackage> filterReleasePackagesByOnline(
			Collection<ReleasePackage> releasePackages) {
		
		Collection<ReleasePackage> result = releasePackages;
		
		if (!authorizationChecker.shouldSeeOfflinePackages()) {
			result = new ArrayList<>();
			for(ReleasePackage releasePackage : releasePackages){
				if(isPackagePublished(releasePackage)) {
					result.add(filterReleasePackageByAuthority(releasePackage));
				}
			}
		}
		
		return result;
	}

	private boolean isPackagePublished(ReleasePackage releasePackage) {
		for(ReleaseVersion version : releasePackage.getReleaseVersions()) {
			if (version.isOnline()) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleasePackage> createReleasePackage(@RequestBody ReleasePackage releasePackage) {
    	authorizationChecker.checkCanCreateReleasePackages();
    	
    	releasePackage.setCreatedBy(currentSecurityContext.getCurrentUserName());
    	
    	// MLDS-740 - Allow Admin to specify the member
    	if (releasePackage.getMember() == null || !currentSecurityContext.isAdmin()) {
    		releasePackage.setMember(userMembershipAccessor.getMemberAssociatedWithUser());
    	}
    	
    	releasePackageRepository.save(releasePackage);

    	releasePackageAuditEvents.logCreationOf(releasePackage);
    	
    	ResponseEntity<ReleasePackage> result = new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
		return result;
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
    public @ResponseBody ResponseEntity<ReleasePackage> getReleasePackage(@PathVariable long releasePackageId) {
    	//FIXME should we check children being consistent?		
    	ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	releasePackage = filterReleasePackageByAuthority(releasePackage);
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }

	private ReleasePackage filterReleasePackageByAuthority(ReleasePackage releasePackage) {
		ReleasePackage result = releasePackage;
		Set<ReleaseVersion> releaseVersions = Sets.newHashSet();
		
		if (!authorizationChecker.shouldSeeOfflinePackages()) {
			for(ReleaseVersion version : releasePackage.getReleaseVersions()) {
				if (version.isOnline()) {
					releaseVersions.add(releaseFilePrivacyFilter.filterReleaseVersionByAuthority(version));
				}
			}
			result.setReleaseVersions(releaseVersions);
		}
		
		return result;
	}

	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleasePackage> updateReleasePackage(@PathVariable long releasePackageId, @RequestBody ReleasePackage body) {
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(body.getReleasePackageId());
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	//FIXME should we check children being consistent?		
    	authorizationChecker.checkCanEditReleasePackage(releasePackage);
    	
    	releasePackage.setName(body.getName());
    	releasePackage.setDescription(body.getDescription());
    	if (currentSecurityContext.isAdmin()) {
    		releasePackage.setMember(body.getMember());
    	}
    	
    	releasePackageRepository.save(releasePackage);
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<?> deactivateReleasePackage(@PathVariable long releasePackageId) {
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
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
    	File license = releasePackageRepository.findOne(releasePackageId).getLicenceFile();
    	
    	return downloadFile(request, license);
    }
	
	private ResponseEntity<?> downloadFile(HttpServletRequest request, File file) throws SQLException, IOException {
		if (file == null) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	} else if (file.getLastUpdated() != null) {
    		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    		long lastUpdatedSecondsFloor = file.getLastUpdated().getMillis() / 1000 * 1000;
			if (ifModifiedSince != -1 && lastUpdatedSecondsFloor <= ifModifiedSince) {
				return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    		}
    	}
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.valueOf(file.getMimetype()));
		httpHeaders.setContentLength(file.getContent().length());
		httpHeaders.setContentDispositionFormData("file", file.getFilename());
		if (file.getLastUpdated() != null) {
			httpHeaders.setLastModified(file.getLastUpdated().getMillis());
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
    public ResponseEntity<?> updateReleasePackageLicense(@PathVariable long releasePackageId, @RequestParam(value="file", required = false) MultipartFile multipartFile) throws IOException {
		ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

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
}
