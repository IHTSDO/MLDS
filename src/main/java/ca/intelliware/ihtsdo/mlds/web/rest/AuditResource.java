package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.repository.PersistenceAuditEventRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AuditEventRequestDTO;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ReleaseFileCountDTO;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * REST controller for getting the audit events.
 */
@RestController
public class AuditResource {

    @Autowired
    AuditEventService auditEventService;


    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    public static final String FILTER_BY_AUDIT_EVENT_TYPE = "auditEventType eq '(\\w+)'";
    public static final String FILTER_BY_AFFILIATE_ID = "affiliateId eq '(\\w+)'";
    public static final String FILTER_BY_APPLICATION_ID = "applicationId eq '(\\w+)'";
    public static final String FILTER_BY_AUDIT_EVENT_DATE_BETWEEN = "auditEventDate ge '(.*)' and auditEventDate le '(.*)'";

    public AuditResource(PersistenceAuditEventRepository persistenceAuditEventRepository) {
        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
    }

    @RequestMapping(value = Routes.AUDITS,
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<List<AuditEvent>> findByFilter(@RequestParam(value = "$filter", required = false) String filter) throws ParseException {
        try {
            if (StringUtils.isBlank(filter)) {
                return new ResponseEntity<List<AuditEvent>>(auditEventService.findAll(), HttpStatus.OK);
            }
            Matcher auditEventTypeMatcher = Pattern.compile(FILTER_BY_AUDIT_EVENT_TYPE).matcher(filter);
            if (auditEventTypeMatcher.matches()) {
                String auditEventType = auditEventTypeMatcher.group(1);
                return new ResponseEntity<List<AuditEvent>>(auditEventService.findByAuditEventType(auditEventType), HttpStatus.OK);
            }
            Matcher affiliateIdMatcher = Pattern.compile(FILTER_BY_AFFILIATE_ID).matcher(filter);
            if (affiliateIdMatcher.matches()) {
                Long affiliateId = Long.parseLong(affiliateIdMatcher.group(1));
                return new ResponseEntity<List<AuditEvent>>(auditEventService.findByAffiliateId(affiliateId), HttpStatus.OK);
            }
            Matcher applicationIdMatcher = Pattern.compile(FILTER_BY_APPLICATION_ID).matcher(filter);
            if (applicationIdMatcher.matches()) {
                Long applicationId = Long.parseLong(applicationIdMatcher.group(1));
                return new ResponseEntity<List<AuditEvent>>(auditEventService.findByApplicationId(applicationId), HttpStatus.OK);
            }
            Matcher auditEventDateBetweenMatcher = Pattern.compile(FILTER_BY_AUDIT_EVENT_DATE_BETWEEN).matcher(filter);
            if (auditEventDateBetweenMatcher.matches()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Instant fromDate = dateFormat.parse(auditEventDateBetweenMatcher.group(1)).toInstant();
                Instant toDate = dateFormat.parse(auditEventDateBetweenMatcher.group(2)).toInstant();
                return new ResponseEntity<List<AuditEvent>>(auditEventService.findByDates(fromDate, toDate), HttpStatus.OK);
            }
            //TODO support more kinds of audit event filters...
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ParseException e) {
            // Handle the ParseException here, you can log it or return an appropriate error response
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = Routes.AUDITSEVENTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public List<ReleaseFileCountDTO> findReleaseFileDownloadAuditData(@RequestBody AuditEventRequestDTO request) {
        Instant[] dateRange = auditEventService.getStartEndInstant(request);
        boolean excludeAdminAndStaff = request.isExcludeAdminAndStaff();
        List<PersistentAuditEvent> response = auditEventService.getAuditEvents(excludeAdminAndStaff, dateRange[0], dateRange[1]);
        List<PersistentAuditEvent> result = auditEventService.filterDownloadEvents(response);
        Map<String, ReleaseFileCountDTO> countMap = new HashMap<>();
        for (PersistentAuditEvent event : result) {
            Map<String, String> data = event.getData();
            String key = data.get("releaseFile.label");

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
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public List<PersistentAuditEvent> findReleaseFileDownloadDataForCsv(@RequestBody AuditEventRequestDTO request) {
        Instant[] dateRange = auditEventService.getStartEndInstant(request);
        boolean excludeAdminAndStaff = request.isExcludeAdminAndStaff();
        List<PersistentAuditEvent> response = auditEventService.getAuditEvents(excludeAdminAndStaff, dateRange[0], dateRange[1]);

        return auditEventService.filterDownloadEvents(response);
    }


}


