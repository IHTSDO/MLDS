package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * REST controller for getting the audit events.
 */
@RestController
public class AuditResource {

    @Autowired
	AuditEventService auditEventService;

    public static final String FILTER_BY_AUDIT_EVENT_TYPE = "auditEventType eq '(\\w+)'";
    public static final String FILTER_BY_AFFILIATE_ID = "affiliateId eq '(\\w+)'";
    public static final String FILTER_BY_APPLICATION_ID = "applicationId eq '(\\w+)'";
    public static final String FILTER_BY_AUDIT_EVENT_DATE_BETWEEN = "auditEventDate ge '(.*)' and auditEventDate le '(.*)'";
    
    @RequestMapping(value = Routes.AUDITS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
    public @ResponseBody ResponseEntity<List<AuditEvent>> findByFilter(@RequestParam(value="$filter",required = false) String filter) {
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
    		Instant fromDate = Instant.parse(auditEventDateBetweenMatcher.group(1), ISODateTimeFormat.date());
    		Instant toDate = Instant.parse(auditEventDateBetweenMatcher.group(2), ISODateTimeFormat.date());
    		return new ResponseEntity<List<AuditEvent>>(auditEventService.findByDates(fromDate, toDate), HttpStatus.OK);
    	}
    	//TODO support more kinds of audit event filters...
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
