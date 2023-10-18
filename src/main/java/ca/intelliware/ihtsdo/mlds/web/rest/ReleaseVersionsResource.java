package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
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

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import java.util.UUID;

@RestController
public class ReleaseVersionsResource {
	@Resource
	ReleasePackageRepository releasePackageRepository;

	@Resource
	ReleaseVersionRepository releaseVersionRepository;

	@Resource
	ReleasePackageAuthorizationChecker authorizationChecker;

	@Resource
	CurrentSecurityContext currentSecurityContext;

	@Resource
	ReleasePackageAuditEvents releasePackageAuditEvents;

	@Resource
	ReleaseFilePrivacyFilter releaseFilePrivacyFilter;

	@Resource
	UserNotifier userNotifier;

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Versions

	@RequestMapping(value = Routes.RELEASE_VERSIONS,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> createReleaseVersion(@PathVariable long releasePackageId, @RequestBody ReleaseVersion releaseVersion) {

		ReleasePackage releasePackage = releasePackageRepository.getOne(releasePackageId);
		if (releasePackage == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
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
	@RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> getReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {
    	//FIXME should we check children being consistent?

    	ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	authorizationChecker.checkCanAccessReleaseVersion(releaseVersion);
    	releaseVersion = releaseFilePrivacyFilter.filterReleaseVersionByAuthority(releaseVersion);

    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseVersion body) {

		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());
/*MLDS*/
        String preOnline = String.valueOf(releaseVersion.getReleaseType()=="offline "
            ||releaseVersion.getReleaseType()=="alpha/beta");

    	releaseVersion.setName(body.getName());
    	releaseVersion.setDescription(body.getDescription());
        releaseVersion.setReleaseType(body.getReleaseType());
//    	releaseVersion.setOnline(body.isOnline());
    	releaseVersion.setPublishedAt(body.getPublishedAt());

        releaseVersion.setSummary(body.getSummary());
        releaseVersion.setReleaseType(body.getReleaseType());
        releaseVersion.setVersionDependentURI(body.getVersionDependentURI());
        releaseVersion.setVersionDependentDerivativeURI(body.getVersionDependentDerivativeURI());
        releaseVersion.setVersionURI(body.getVersionURI());

    	/*if (!Objects.equal(preOnline, releaseVersion.isOnline())) {
    		if (releaseVersion.isOnline()) {
    			releasePackageAuditEvents.logTakenOnline(releaseVersion);
    		} else {
    			releasePackageAuditEvents.logTakenOffline(releaseVersion);
    		}
    	}
*/
        if (!Objects.equal(preOnline, releaseVersion.getReleaseType())) {
            if (releaseVersion.getReleaseType().equalsIgnoreCase("online")) {
                releasePackageAuditEvents.logTakenOnline(releaseVersion);
            }
            else if(releaseVersion.getReleaseType().equalsIgnoreCase("offline")){
                releasePackageAuditEvents.logTakenOffline(releaseVersion);
            }
            else{releasePackageAuditEvents.logTakenAlphaAndBeta(releaseVersion);}
        }
    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_VERSION_NOTIFICATIONS,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersionNotification(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {

		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

		userNotifier.notifyReleasePackageUpdated(releaseVersion);

    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
	public @ResponseBody
	ResponseEntity<?> deactivateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {

		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
		if (releaseVersion == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

//		if (releaseVersion.isOnline()) {
//			return new ResponseEntity<>(HttpStatus.CONFLICT);

        if (releaseVersion.getReleaseType().equalsIgnoreCase("online")) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

		releasePackageAuditEvents.logDeletionOf(releaseVersion);

		// Actually mark releasePackage as being inactive and then hide from
		// subsequent calls rather than sql delete from the db
		releaseVersionRepository.delete(releaseVersion);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
