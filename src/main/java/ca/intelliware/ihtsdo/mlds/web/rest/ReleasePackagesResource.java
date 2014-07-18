package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "releasePackages", description = "Release Package and Version Release API")
public class ReleasePackagesResource {

	@Resource
	ReleasePackageRepository releasePackageRepository;

	@Resource
	ReleaseVersionRepository releaseVersionRepository;

	@Resource
	ReleaseFileRepository releaseFileRepository;

	@Resource
	AuthorizationChecker authorizationChecker;
	
	@Resource
	CurrentSecurityContext currentSecurityContext;

	@Resource
	ReleasePackageAuditEvents releasePackageAuditEvents;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Packages

	@RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<ReleasePackage>> getReleasePackages() {
		
    	Collection<ReleasePackage> releasePackages = releasePackageRepository.findAll();
    	
    	return new ResponseEntity<Collection<ReleasePackage>>(releasePackages, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.POST,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleasePackage> createReleasePackage(@RequestBody ReleasePackage releasePackage) {
    	authorizationChecker.checkCanAccessReleasePackages();
    	
    	releasePackage.setCreatedBy(currentSecurityContext.getCurrentUserName());
    	
    	releasePackageRepository.save(releasePackage);

    	releasePackageAuditEvents.logCreationOf(releasePackage);
    	
    	ResponseEntity<ReleasePackage> result = new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    	// FIXME MLDS-256 MB can we build this link? result.getHeaders().setLocation(location);
		return result;
    }
	
	@RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleasePackage> getReleasePackage(@PathVariable long releasePackageId) {
    	//FIXME should we check children being consistent?		
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.PUT,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleasePackage> updateReleasePackage(@PathVariable long releasePackageId, @RequestBody ReleasePackage body) {
    	//FIXME should we check children being consistent?		
		authorizationChecker.checkCanAccessReleasePackages();
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(body.getReleasePackageId());
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	releasePackage.setName(body.getName());
    	releasePackage.setDescription(body.getDescription());
    	
    	releasePackageRepository.save(releasePackage);
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.DELETE,
            produces = "application/json")
    public @ResponseBody ResponseEntity<?> deactivateReleasePackage(@PathVariable long releasePackageId) {
    	//FIXME should we check children being consistent?		
		authorizationChecker.checkCanAccessReleasePackages();
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
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


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Versions
	
	@RequestMapping(value = Routes.RELEASE_VERSIONS,
    		method = RequestMethod.POST,
            produces = "application/json")
	@Transactional
    public @ResponseBody ResponseEntity<ReleaseVersion> createReleaseVersion(@PathVariable long releasePackageId, @RequestBody ReleaseVersion releaseVersion) {
    	authorizationChecker.checkCanAccessReleasePackages();
    	
    	releaseVersion.setCreatedBy(currentSecurityContext.getCurrentUserName());

    	releaseVersionRepository.save(releaseVersion);
    	
    	ReleasePackage releasePackage = releasePackageRepository.getOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	releasePackage.addReleaseVersion(releaseVersion);

    	releasePackageAuditEvents.logCreationOf(releaseVersion);
    	
    	ResponseEntity<ReleaseVersion> result = new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    	// FIXME MLDS-256 MB can we build this link? result.getHeaders().setLocation(location);
		return result;
    }

	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleaseVersion> getReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {
    	//FIXME should we check children being consistent?
		authorizationChecker.checkCanAccessReleasePackages();
    	
    	ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.PUT,
            produces = "application/json")
	@Transactional
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseVersion body) {
    	//FIXME should we check children being consistent?		
		authorizationChecker.checkCanAccessReleasePackages();
    	
		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	releaseVersion.setName(body.getName());
    	releaseVersion.setDescription(body.getDescription());
    	releaseVersion.setOnline(body.isOnline());
    	
    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.DELETE,
            produces = "application/json")
	@Transactional
	public @ResponseBody
	ResponseEntity<?> deactivateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {
		authorizationChecker.checkCanAccessReleasePackages();

		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
		if (releaseVersion == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (releaseVersion.isOnline()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		releasePackageAuditEvents.logReleaseVersionDeleted(releaseVersion);

		// Actually mark releasePackage as being inactive and then hide from
		// subsequent calls rather than sql delete from the db
		releaseVersionRepository.delete(releaseVersion);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Files
	
	@RequestMapping(value = Routes.RELEASE_FILE,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleaseFile> getReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {
    	//FIXME should we check children being consistent?
		authorizationChecker.checkCanAccessReleasePackages();
    	
    	ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
    	if (releaseFile == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<ReleaseFile>(releaseFile, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_FILES,
    		method = RequestMethod.POST,
            produces = "application/json")
	@Transactional
    public @ResponseBody ResponseEntity<ReleaseFile> createReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseFile body) {
    	//FIXME should we check children being consistent?
		authorizationChecker.checkCanAccessReleasePackages();
    	
		releaseFileRepository.save(body);
		
		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	releaseVersion.addReleaseFile(body);
    	
    	releasePackageAuditEvents.logCreationOf(body);

    	return new ResponseEntity<ReleaseFile>(body, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_FILE,
			method = RequestMethod.DELETE,
			produces = "application/json")
	public @ResponseBody ResponseEntity<ReleaseFile> deleteReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId) {
		//FIXME should we check children being consistent?
		authorizationChecker.checkCanAccessReleasePackages();
		
		ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
		if (releaseFile == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		releaseFileRepository.delete(releaseFile);
		
		releasePackageAuditEvents.logDeletionOf(releaseFile);
		
		return new ResponseEntity<ReleaseFile>(HttpStatus.OK);
	}
}
