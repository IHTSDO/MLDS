package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Sets;

@RestController
public class ReleasePackagesResource {

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


}
