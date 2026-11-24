package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.config.SearchConfiguration;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminSearchResource {
    private final Logger log = LoggerFactory.getLogger(AdminSearchResource.class);

    @Resource
    SearchConfiguration searchConfiguration;

    @PostMapping("/admin/search/reindex")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Map<String, String>> triggerReindex() throws IOException {
        log.info("Manual full-text search reindex requested via admin endpoint.");
        searchConfiguration.triggerManualReindex();
        return ResponseEntity.ok(Map.of(
            "status", "STARTED",
            "message", "Full-text search reindexing started in background."
        ));
    }
}

