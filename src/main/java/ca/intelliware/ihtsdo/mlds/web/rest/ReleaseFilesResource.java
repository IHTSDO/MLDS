package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

import com.codahale.metrics.annotation.Timed;

@RestController
public class ReleaseFilesResource {
	@Resource
	ReleaseVersionRepository releaseVersionRepository;

	@Resource
	ReleaseFileRepository releaseFileRepository;

	@Resource
	ReleasePackageAuditEvents releasePackageAuditEvents;

	@Resource
	ReleasePackageAuthorizationChecker authorizationChecker;
	
	@Resource
	UriDownloader uriDownloader;
	
	@Resource
	UserMembershipAccessor userMembershipAccessor;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Files
	
	@RequestMapping(value = Routes.RELEASE_FILE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseFile> getReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {
    	
    	ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
    	if (releaseFile == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	//FIXME should we check children being consistent?
    	authorizationChecker.checkCanEditReleasePackage(releaseFile.getReleaseVersion().getReleasePackage());
    	
    	return new ResponseEntity<ReleaseFile>(releaseFile, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_FILES,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseFile> createReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseFile body) {
		
		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());
    	
    	releaseFileRepository.save(body);
    	releaseVersion.addReleaseFile(body);
    	
    	releasePackageAuditEvents.logCreationOf(body);

    	return new ResponseEntity<ReleaseFile>(body, HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@RequestMapping(value = Routes.RELEASE_FILE,
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Timed
	public @ResponseBody ResponseEntity<ReleaseFile> deleteReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {
		
		ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
		if (releaseFile == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		authorizationChecker.checkCanEditReleasePackage(releaseFile.getReleaseVersion().getReleasePackage());
		
		releaseFileRepository.delete(releaseFile);
		
		releasePackageAuditEvents.logDeletionOf(releaseFile);
		
		return new ResponseEntity<ReleaseFile>(HttpStatus.OK);
	}
	
	@RequestMapping(value = Routes.RELEASE_FILE_DOWNLOAD,
    		method = RequestMethod.GET)
	@RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
	public @ResponseBody
	void downloadReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
    	if (releaseFile == null) {
    		//TODO better 404 handling
    		throw new RuntimeException("no such file found");
    	}
    	
    	//FIXME should we check children being consistent?
    	authorizationChecker.checkCanDownloadReleaseVersion(releaseFile.getReleaseVersion());
    	
    	int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    	try {
	    	String downloadUrl = releaseFile.getDownloadUrl();
	    	statusCode = uriDownloader.download(downloadUrl, request, response);
    	} finally {
    		releasePackageAuditEvents.logDownload(releaseFile, statusCode, userMembershipAccessor.getAffiliate());
    	}
    }
}
