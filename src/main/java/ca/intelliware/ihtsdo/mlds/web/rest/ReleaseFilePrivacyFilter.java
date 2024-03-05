package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import com.google.common.collect.Sets;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Set;

/**
 * Hide the file urls for non-staff users.
 * We should drop this in favour of a Jackson filter.
 */
@Service
public class ReleaseFilePrivacyFilter {
    @Autowired
    EntityManager entityManager;

    @Autowired
    CurrentSecurityContext currentSecurityContext;

    public ReleaseVersion filterReleaseVersionByAuthority(ReleaseVersion version) {
        ReleaseVersion result = version;

        // FIX ME AC:Check to see if user's application is approved otherwise hide download links
        if (!currentSecurityContext.isUser()) {
            Set<ReleaseFile> filteredReleaseFiles = Sets.newHashSet();
            for (ReleaseFile releaseFile : version.getReleaseFiles()) {
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
