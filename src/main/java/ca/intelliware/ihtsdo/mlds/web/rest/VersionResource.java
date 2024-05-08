package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.SpringEnabledMavenWebappMetadataLookup;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;


/**
 * Publish our Maven version info so we know what's running on each server.
 */
@RestController
public class VersionResource {
    @Resource
    SpringEnabledMavenWebappMetadataLookup mavenWebappMetadataLookup;

    @RolesAllowed({AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.MEMBER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.VERSION,
            method = RequestMethod.GET)
    public Properties getVersionInfo() {
        Properties properties = mavenWebappMetadataLookup.getProperties();
        if (properties.isEmpty()) {
            properties = new Properties();
            properties.put("version", "unknown");
        }
        return properties;
    }

}
