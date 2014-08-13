package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

/**
 * REST controller for getting the audit events.
 */
@RestController
public class AuditResource {

    @Inject
    private AuditEventService auditEventService;

    @RequestMapping(value = "/app/rest/audits/all",
            method = RequestMethod.GET,
            produces = "application/json")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<AuditEvent> findAll() {
        return auditEventService.findAll();
    }

    @RequestMapping(value = "/app/rest/audits/byDates",
            method = RequestMethod.GET,
            produces = "application/json")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<AuditEvent> findByDates(@RequestParam(value = "fromDate") @DateTimeFormat(iso=ISO.DATE) Instant fromDate,
                                    @RequestParam(value = "toDate") @DateTimeFormat(iso=ISO.DATE) Instant toDate) {
        return auditEventService.findByDates(fromDate, toDate);
    }

    public static final String FILTER_AUDIT_EVENT_TYPE = "auditEventType eq '(\\w+)'";
    
    @RequestMapping(value = Routes.AUDITS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    public @ResponseBody ResponseEntity<List<AuditEvent>> findByFilter(@RequestParam(value="$filter") String filter) {
    	if (StringUtils.isBlank(filter)) {
    		return new ResponseEntity<List<AuditEvent>>(auditEventService.findAll(), HttpStatus.OK);
    	}
    	Matcher auditEventTypeMatcher = Pattern.compile(FILTER_AUDIT_EVENT_TYPE).matcher(filter);
    	if (auditEventTypeMatcher.matches()) {
    		String auditEventType = auditEventTypeMatcher.group(1);
    		return new ResponseEntity<List<AuditEvent>>(auditEventService.findByAuditEventType(auditEventType), HttpStatus.OK);
    	}
    	//FIXME support more kinds of audit event filters...
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
