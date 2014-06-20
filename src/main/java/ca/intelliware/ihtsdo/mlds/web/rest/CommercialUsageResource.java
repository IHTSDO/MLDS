package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.PathParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;

//@RestController
public class CommercialUsageResource {
	
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository;
	
	@Resource
	AuthorizationChecker authorizationChecker;
	
    @RequestMapping(value = Routes.USAGE_REPORTS,
            produces = "application/json")
    public @ResponseBody Collection<CommercialUsage> getUsageReports(@PathParam("licenseeId") long userIdInPlaceOfImaginaryLicenceeId) {
    	
    	authorizationChecker.checkCanAccessLicensee(userIdInPlaceOfImaginaryLicenceeId);
    	
    	// FIXME MLDS-23 need to segregate by user/licensee
    	return commercialUsageRepository.findAll();
    }
    
    /*
    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.POST,
    		produces = "application/json")
    public @ResponseBody Collection<CommercialUsage> createNewSnapshot(@PathParam("licenseeId") long userIdInPlaceOfImaginaryLicenceeId, String name) {
    	// FIXME MLDS-23 need to segregate by user/licensee
    	return commercialUsageRepository.findAll();
    }
    */

    @RequestMapping(value = Routes.USAGE_REPORT,
            produces = "application/json")
    public @ResponseBody CommercialUsage getCommercialUsageReport(@PathParam("usageReportId") long usageReportId) {
    	authorizationChecker.checkCanAccessLicensee(usageReportId);
    	
    	return commercialUsageRepository.findOne(usageReportId);
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT,
    		method = RequestMethod.POST,
    		produces = "application/json")
    public @ResponseBody ResponseEntity<CommercialUsageEntry> addCommercialUsageEntry(@PathParam("usageReportId") long usageReportId, @RequestBody CommercialUsageEntry newEntry) {
    	authorizationChecker.checkCanAccessUsageReport(usageReportId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromPath(Routes.USAGE_REPORT_ENTRY).build().expand(newEntry.getCommercialUsageEntryId()).toUri());

        // FIXME MLDS-23
        
		ResponseEntity<CommercialUsageEntry> responseEntity = new ResponseEntity<CommercialUsageEntry>(newEntry, headers, HttpStatus.CREATED);
		return responseEntity;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
            produces = "application/json")
    public @ResponseBody CommercialUsageEntry getCommercialUsageEntry(@PathParam("commercialUsageEntryUrlId") long commercialUsageEntryUrlId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageEntryUrlId);
    	// find entry with matching publicId contained in magic current collection for implicit user
    	// return it
    	return null;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.PUT,
    		produces = "application/json")
    public @ResponseBody CommercialUsageEntry updateCommercialUsageEntry(@PathParam("commercialUsageEntryUrlId") long commercialUsageEntryUrlId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageEntryUrlId);
    	
    	// find entry with matching publicId contained in magic current collection for implicit user
    	// remove from current
    	// insert new object into current
    	
    	return null;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.DELETE,
    		produces = "application/json")
    public @ResponseBody ResponseEntity<?> deleteCommercialUsageEntry(@PathParam("commercialUsageEntryUrlId") long commercialUsageEntryUrlId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageEntryUrlId);

    	// find entry with matching publicId contained in magic current collection for implicit user
    	// remove from current
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    

    /*
	static final String USAGE_REPORT_ENTRY = "/app/rest/usageReportEntries/{usageReportEntryPublicId}"; // control endpoint for single entry: put to edit, or delete
	*/

}
