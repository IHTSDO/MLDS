package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
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
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
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
	ReleasePackageAuthorizationChecker authorizationChecker;
	
	@Resource
	CurrentSecurityContext currentSecurityContext;

	@Resource
	ReleasePackageAuditEvents releasePackageAuditEvents;
	
	@Resource
	EntityManager entityManager;
	
	@Resource
	UserMembershipAccessor userMembershipAccessor;
	
	@Resource
	UriDownloader uriDownloader;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Packages

	@RequestMapping(value = Routes.RELEASE_PACKAGES,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@PermitAll
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
    public @ResponseBody ResponseEntity<ReleasePackage> createReleasePackage(@RequestBody ReleasePackage releasePackage) {
    	authorizationChecker.checkCanCreateReleasePackages();
    	
    	releasePackage.setCreatedBy(currentSecurityContext.getCurrentUserName());
    	releasePackage.setMember(userMembershipAccessor.getMemberAssociatedWithUser());
    	
    	releasePackageRepository.save(releasePackage);

    	releasePackageAuditEvents.logCreationOf(releasePackage);
    	
    	ResponseEntity<ReleasePackage> result = new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    	// FIXME MLDS-256 MB can we build this link? result.getHeaders().setLocation(location);
		return result;
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
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
					releaseVersions.add(filterReleaseVersionByAuthority(version));
				}
			}
			result.setReleaseVersions(releaseVersions);
		}
		
		return result;
	}

	private ReleaseVersion filterReleaseVersionByAuthority(ReleaseVersion version) {
		ReleaseVersion result = version;
		
		// FIX ME AC:Check to see if user's application is approved otherwise hide download links
		if(!currentSecurityContext.isUser()) {
			Set<ReleaseFile> filteredReleaseFiles = Sets.newHashSet();
			for(ReleaseFile releaseFile : version.getReleaseFiles()) {
				// need to detach to avoid accidentally pushing these changes back to the db.
				entityManager.detach(releaseFile);
				releaseFile.setDownloadUrl(null);
				filteredReleaseFiles.add(releaseFile);
			}
			result.setReleaseFiles(filteredReleaseFiles);
		}
		
		return result;
	}

	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public @ResponseBody ResponseEntity<ReleasePackage> updateReleasePackage(@PathVariable long releasePackageId, @RequestBody ReleasePackage body) {
    	
    	ReleasePackage releasePackage = releasePackageRepository.findOne(body.getReleasePackageId());
    	if (releasePackage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	//FIXME should we check children being consistent?		
    	authorizationChecker.checkCanEditReleasePackage(releasePackage);
    	
    	releasePackage.setName(body.getName());
    	releasePackage.setDescription(body.getDescription());
    	
    	releasePackageRepository.save(releasePackage);
    	
    	return new ResponseEntity<ReleasePackage>(releasePackage, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_PACKAGE,
    		method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
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


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Versions
	
	@RequestMapping(value = Routes.RELEASE_VERSIONS,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public @ResponseBody ResponseEntity<ReleaseVersion> createReleaseVersion(@PathVariable long releasePackageId, @RequestBody ReleaseVersion releaseVersion) {
		
		ReleasePackage releasePackage = releasePackageRepository.getOne(releasePackageId);
		if (releasePackage == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		authorizationChecker.checkCanEditReleasePackage(releasePackage);
    	
    	releaseVersion.setCreatedBy(currentSecurityContext.getCurrentUserName());

    	releaseVersionRepository.save(releaseVersion);
    	
    	releasePackage.addReleaseVersion(releaseVersion);

    	releasePackageAuditEvents.logCreationOf(releaseVersion);
    	
    	ResponseEntity<ReleaseVersion> result = new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    	// FIXME MLDS-256 MB can we build this link? result.getHeaders().setLocation(location);
		return result;
    }

	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public @ResponseBody ResponseEntity<ReleaseVersion> getReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {
    	//FIXME should we check children being consistent?
    	
    	ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	authorizationChecker.checkCanAccessReleaseVersion(releaseVersion);
    	releaseVersion = filterReleaseVersionByAuthority(releaseVersion);
    	
    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    public @ResponseBody ResponseEntity<ReleaseVersion> updateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @RequestBody ReleaseVersion body) {
    	
		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
    	if (releaseVersion == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());
    	
    	boolean preOnline = releaseVersion.isOnline();
    	
    	releaseVersion.setName(body.getName());
    	releaseVersion.setDescription(body.getDescription());
    	releaseVersion.setOnline(body.isOnline());
    	
    	if (!Objects.equal(preOnline, releaseVersion.isOnline())) {
    		if (releaseVersion.isOnline()) {
    			releasePackageAuditEvents.logTakenOnline(releaseVersion);
    		} else {
    			releasePackageAuditEvents.logTakenOffline(releaseVersion);
    		}
    	}
    	
    	return new ResponseEntity<ReleaseVersion>(releaseVersion, HttpStatus.OK);
    }
	
	@RequestMapping(value = Routes.RELEASE_VERSION,
    		method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	public @ResponseBody
	ResponseEntity<?> deactivateReleaseVersion(@PathVariable long releasePackageId, @PathVariable long releaseVersionId) {

		ReleaseVersion releaseVersion = releaseVersionRepository.findOne(releaseVersionId);
		if (releaseVersion == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		authorizationChecker.checkCanEditReleasePackage(releaseVersion.getReleasePackage());

		if (releaseVersion.isOnline()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		releasePackageAuditEvents.logDeletionOf(releaseVersion);

		// Actually mark releasePackage as being inactive and then hide from
		// subsequent calls rather than sql delete from the db
		releaseVersionRepository.delete(releaseVersion);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Release Files
	
	@RequestMapping(value = Routes.RELEASE_FILE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
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
    public @ResponseBody void downloadReleaseFile(@PathVariable long releasePackageId, @PathVariable long releaseVersionId, @PathVariable long releaseFileId, HttpServletRequest request, HttpServletResponse response) {
    	
    	ReleaseFile releaseFile = releaseFileRepository.findOne(releaseFileId);
    	if (releaseFile == null) {
    		//FIXME better 404 handling...
    		throw new RuntimeException("no such file found");
    	}
    	
    	//FIXME should we check children being consistent?
    	authorizationChecker.checkCanDownloadReleaseVersion(releaseFile.getReleaseVersion());
    	
    	String downloadUrl = releaseFile.getDownloadUrl();
    	int statusCode = uriDownloader.download(downloadUrl, request, response);
    	
    	releasePackageAuditEvents.logDownload(releaseFile, statusCode);
    }
}
