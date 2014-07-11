package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

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

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Packages

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<ReleasePackage>> getReleasePackages() {
    	authorizationChecker.checkCanAccessReleasePackages();
    	
    	Collection<ReleasePackage> releasePackages = releasePackageRepository.findAll();
    	
    	return new ResponseEntity<Collection<ReleasePackage>>(releasePackages, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.POST,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleasePackage> createReleasePackage(@RequestBody ReleasePackage releasePackage) {
    	authorizationChecker.checkCanAccessReleasePackages();
    	
    	releasePackage.setCreatedBy(authorizationChecker.getCurrentUserName());
    	
    	releasePackageRepository.save(releasePackage);
    	
    	ResponseEntity<ReleasePackage> result = new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    	// FIXME MLDS-256 MB can we build this link? result.getHeaders().setLocation(location);
		return result;
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<ReleasePackage> getPackage(@PathVariable long releasePackageId) {
    	//FIXME should we check children being consistent?		
		authorizationChecker.checkCanAccessReleasePackages();
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(releasePackageId);
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Versions
	
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
}
