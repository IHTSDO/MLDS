package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;

import javax.annotation.Resource;

import org.joda.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "commercialUsage", description = "Commercial usage reporting and submission API")
public class CommercialUsageResource {
	
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository;
	
	@Resource
	AuthorizationChecker authorizationChecker;
	
    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody Collection<CommercialUsage> getUsageReports(@PathVariable long licenseeId) {
    	long userIdInPlaceOfImaginaryLicenceeId = licenseeId;
    	
    	authorizationChecker.checkCanAccessLicensee(userIdInPlaceOfImaginaryLicenceeId);
    	
    	// FIXME MLDS-23 need to segregate by user/licensee
    	return commercialUsageRepository.findAll();
    }
    
    public static class CommercialUsageNewSubmissionMessage {
    	LocalDate startDate;
    	LocalDate endDate;
		public LocalDate getStartDate() {
			return startDate;
		}
		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}
		public LocalDate getEndDate() {
			return endDate;
		}
		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}
    }
    /**
     * Start a new submission
     * @param userIdInPlaceOfImaginaryLicenceeId
     * @param submissionPeriod
     * @return the new CommercialUsage 
     */
    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.POST,
    		produces = "application/json")
    public @ResponseBody CommercialUsage createNewSubmission(@PathVariable("licenseeId") long licenseeId, @RequestBody CommercialUsageNewSubmissionMessage submissionPeriod) {
    	CommercialUsage commercialUsage = new CommercialUsage();
    	commercialUsage.setStartDate(submissionPeriod.getStartDate());
    	commercialUsage.setEndDate(submissionPeriod.getEndDate());
    	
    	// find existing report with most recent end date 
    	// deep copy and save ?? 200? 201? redirect?
    	
    	return commercialUsageRepository.save(commercialUsage);
    }

    @RequestMapping(value = Routes.USAGE_REPORT,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody CommercialUsage getCommercialUsageReport(@PathVariable("usageReportId") long usageReportId) {
    	authorizationChecker.checkCanAccessLicensee(usageReportId);
    	
    	// map missing to 404
    	return commercialUsageRepository.findOne(usageReportId);
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT,
    		method = RequestMethod.POST,
    		produces = "application/json")
    public @ResponseBody ResponseEntity<CommercialUsageEntry> addCommercialUsageEntry(@PathVariable("usageReportId") long usageReportId, @RequestBody CommercialUsageEntry newEntryValue) {
    	authorizationChecker.checkCanAccessUsageReport(usageReportId);

        CommercialUsageEntry newEntry = commercialUsageEntryRepository.save(newEntryValue);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromPath(Routes.USAGE_REPORT_ENTRY).build().expand(newEntry.getCommercialUsageEntryId()).toUri());
        
		ResponseEntity<CommercialUsageEntry> responseEntity = new ResponseEntity<CommercialUsageEntry>(newEntry, headers, HttpStatus.CREATED);
		return responseEntity;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody CommercialUsageEntry getCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryUrlId") long commercialUsageEntryUrlId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryUrlId);
    	
    	// FIXME MLDS-23 throw 404 on not-found
    	return commercialUsageEntryRepository.findOne(commercialUsageEntryUrlId);
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.PUT,
    		produces = "application/json")
    public @ResponseBody CommercialUsageEntry updateCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryUrlId") long commercialUsageEntryUrlId, @RequestBody CommercialUsageEntry newEntryValue) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryUrlId);

    	// validate id not null?
    	
    	CommercialUsageEntry entry = commercialUsageEntryRepository.save(newEntryValue);
    	
    	return entry;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.DELETE,
    		produces = "application/json")
    public @ResponseBody ResponseEntity<?> deleteCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryUrlId") long commercialUsageEntryUrlId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryUrlId);

    	commercialUsageEntryRepository.delete(commercialUsageEntryUrlId);
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
