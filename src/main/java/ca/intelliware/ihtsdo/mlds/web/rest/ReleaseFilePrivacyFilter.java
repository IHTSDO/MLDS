package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import com.google.common.collect.Sets;

/**
 * Hide the file urls for non-staff users.
 * We should drop this in favour of a Jackson filter.
 *
 */
@Service
public class ReleaseFilePrivacyFilter {
	@Resource
	EntityManager entityManager;
	
	@Resource
	CurrentSecurityContext currentSecurityContext;

	public ReleaseVersion filterReleaseVersionByAuthority(ReleaseVersion version) {
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

}
