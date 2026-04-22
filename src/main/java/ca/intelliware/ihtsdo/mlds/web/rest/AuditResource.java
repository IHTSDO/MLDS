package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AuditEventRequestDTO;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleaseFileCountDTO;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;


/**
 * REST controller for getting the audit events.
 */
@RestController
public class AuditResource {

    @Autowired
    AuditEventService auditEventService;


    @RequestMapping(value = Routes.AUDITS,
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<List<AuditEvent>> findByFilter(@RequestParam(value = "$filter", required = false) String filter) throws ParseException {
        try {
            List<AuditEvent> result = auditEventService.findByFilter(filter);

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = Routes.AUDITSEVENTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.ADMIN })
    @Timed
    public List<ReleaseFileCountDTO> findReleaseFileDownloadAuditData(@RequestBody AuditEventRequestDTO request) {
        Instant[] dateRange = auditEventService.getStartEndInstant(request);
        boolean excludeAdminAndStaff = request.isExcludeAdminAndStaff();
        List<PersistentAuditEvent> response = auditEventService.getAuditEvents(excludeAdminAndStaff, dateRange[0], dateRange[1]);
        List<PersistentAuditEvent> result = auditEventService.filterDownloadEvents(response);
        Map<String, ReleaseFileCountDTO> countMap = new HashMap<>();
        for (PersistentAuditEvent event : result) {
            Map<String, String> data = event.getData();
            String key = data.get("releaseFile.label") + "|" +
                data.get("releaseVersion.name") + "|" +
                data.get("releasePackage.name");

            countMap.computeIfAbsent(key, k -> {
                ReleaseFileCountDTO countDTO = new ReleaseFileCountDTO();
                countDTO.setReleaseFileName(data.get("releaseFile.label"));
                countDTO.setReleaseVersionName(data.get("releaseVersion.name"));
                countDTO.setReleasePackageName(data.get("releasePackage.name"));
                countDTO.setCount(0); // Initialize count
                return countDTO;
            }).setCount(countMap.get(key).getCount() + 1); // Increment count
        }
        return new ArrayList<>(countMap.values());
    }

    @PostMapping(value = Routes.AUDITSEVENTS_CSV, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.ADMIN })
    @Timed
    public List<PersistentAuditEvent> findReleaseFileDownloadDataForCsv(@RequestBody AuditEventRequestDTO request) {
        Instant[] dateRange = auditEventService.getStartEndInstant(request);
        boolean excludeAdminAndStaff = request.isExcludeAdminAndStaff();
        List<PersistentAuditEvent> response = auditEventService.getAuditEvents(excludeAdminAndStaff, dateRange[0], dateRange[1]);

        return auditEventService.filterDownloadEvents(response);
    }


}


