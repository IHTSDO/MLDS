package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsagePeriod;
import ca.intelliware.ihtsdo.mlds.domain.UsageContext;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageResetter;

@RestController
public class CommercialUsageResource {
	
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository;

	@Resource
	CommercialUsageCountryRepository commercialUsageCountryRepository;

	@Resource
	AffiliateRepository affiliateRepository;

	@Resource
	CommercialUsageAuthorizationChecker authorizationChecker;
	
	@Resource
	CommercialUsageResetter commercialUsageResetter;
	
	@Resource
	CommercialUsageAuditEvents commercialUsageAuditEvents;
	
    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<Collection<CommercialUsage>> getUsageReports(@PathVariable long affiliateId) {

    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	authorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	return new ResponseEntity<Collection<CommercialUsage>>(affiliate.getCommercialUsages(), HttpStatus.OK);
    }
    
    @RequestMapping(value = Routes.USAGE_REPORTS_ALL,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<Collection<CommercialUsage>> getAllUsageReports(@RequestParam(value="$filter") String filter) {
    	Collection<CommercialUsage> usageReports = null;
    	if (filter == null) {
    		usageReports = commercialUsageRepository.findAll();
    	} else {
    		if (Objects.equal(filter, "approvalState/submitted eq true")) {
    			usageReports = commercialUsageRepository.findByApprovalStateIn(Lists.newArrayList(ApprovalState.SUBMITTED));
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
    	}
    	
    	return new ResponseEntity<Collection<CommercialUsage>>(usageReports, HttpStatus.OK);
    }
       
    public static class CommercialUsageApprovalTransitionMessage {
    	ApprovalTransition transition;
    	public ApprovalTransition getTransition() {
    		return transition;
    	}
    	public void setTransition(ApprovalTransition transition) {
    		this.transition = transition;
    	}
    }
    
    public static enum ApprovalTransition {
    	SUBMIT,
    	RETRACT,
    	REVIEWED,
    }
    
    /**
     * Start a new submission
     * @param affiliateId
     * @param submissionPeriod
     * @return the new CommercialUsage or an existing CommercialUsage with matching date period
     */
    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsage> createNewSubmission(@PathVariable long affiliateId, @RequestBody CommercialUsagePeriod submissionPeriod) {
    	
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	authorizationChecker.checkCanAccessAffiliate(affiliate);
    	
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	CommercialUsage commercialUsage = null;
		List<CommercialUsage> commercialUsages = commercialUsageRepository.findBySamePeriod(affiliate, submissionPeriod.getStartDate(), submissionPeriod.getEndDate());
		if (commercialUsages.size() > 0) {
			// Return the existing commercial usage unmodified
			commercialUsage = commercialUsages.get(0);
			return new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
		} else {
			commercialUsages = commercialUsageRepository.findByMostRecentPeriod(affiliate);
			if (commercialUsages.size() > 0) {
				commercialUsage = commercialUsages.get(0);
			}
		}
    	if (commercialUsage == null) {
	    	commercialUsage = new CommercialUsage();
	    	commercialUsage.setType(affiliate.getType());
    	}
    	
    	commercialUsageResetter.detachAndReset(commercialUsage, submissionPeriod.getStartDate(), submissionPeriod.getEndDate());
    	
    	commercialUsage = commercialUsageRepository.save(commercialUsage);
    	
    	affiliate.addCommercialUsage(commercialUsage);
    	
    	commercialUsageAuditEvents.logCreationOf(commercialUsage);
    	
		ResponseEntity<CommercialUsage> responseEntity = new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
		return responseEntity;
    }

	@RequestMapping(value = Routes.USAGE_REPORT,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<CommercialUsage> getCommercialUsageReport(@PathVariable long commercialUsageId) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.USAGE_REPORT_CONTEXT,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<UsageContext> updateCommercialUsageContext(@PathVariable long commercialUsageId, @RequestBody UsageContext context) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 

    	commercialUsage.setContext(context);
    	
    	commercialUsage = commercialUsageRepository.save(commercialUsage);
    	
    	return new ResponseEntity<UsageContext>(commercialUsage.getContext(), HttpStatus.OK);
    }

	
	
	@RequestMapping(value = Routes.USAGE_REPORT_TYPE,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<UsageContext> updateCommercialUsageType(@PathVariable long commercialUsageId, @PathVariable AffiliateType type) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 

    	commercialUsage.setType(type);
    	
    	commercialUsage = commercialUsageRepository.save(commercialUsage);
    	
    	return new ResponseEntity<UsageContext>(commercialUsage.getContext(), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_APPROVAL,
    		method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsage> transitionCommercialUsageApproval(@PathVariable("commercialUsageId") long commercialUsageId, @RequestBody CommercialUsageApprovalTransitionMessage applyTransition) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	// FIXME Extract out to "workflow" handler
    	if (ApprovalState.NOT_SUBMITTED.equals(commercialUsage.getApprovalState()) && ApprovalTransition.SUBMIT.equals(applyTransition.getTransition())) {
    		commercialUsage.setApprovalState(ApprovalState.SUBMITTED);
    		commercialUsage.setSubmitted(Instant.now());
    		commercialUsage = commercialUsageRepository.save(commercialUsage);
    	} else if ((ApprovalState.SUBMITTED.equals(commercialUsage.getApprovalState()) || ApprovalState.APPROVED.equals(commercialUsage.getApprovalState())) && ApprovalTransition.RETRACT.equals(applyTransition.getTransition())) {
        	commercialUsage.setApprovalState(ApprovalState.NOT_SUBMITTED);
        	commercialUsage.setSubmitted(null);
        	commercialUsage = commercialUsageRepository.save(commercialUsage);
    	} else if (ApprovalState.SUBMITTED.equals(commercialUsage.getApprovalState()) && ApprovalTransition.REVIEWED.equals(applyTransition.getTransition())) {
    		authorizationChecker.checkCanReviewUsageReports();
        	commercialUsage.setApprovalState(ApprovalState.APPROVED);
        	commercialUsage = commercialUsageRepository.save(commercialUsage);
    	} else {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
        
    	commercialUsageAuditEvents.logApprovalStateChange(commercialUsage);
    	
		ResponseEntity<CommercialUsage> responseEntity = new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
		return responseEntity;
    }

    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT,
    		method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageEntry> addCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @RequestBody CommercialUsageEntry newEntryValue) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	commercialUsageEntryRepository.save(newEntryValue);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	commercialUsage.addEntry(newEntryValue);
        
        HttpHeaders headers = new HttpHeaders();
        // FIXME flush and get ids back
        //headers.setLocation(ServletUriComponentsBuilder.fromPath(Routes.USAGE_REPORT_ENTRY).build().expand(newEntry.getCommercialUsageEntryId()).toUri());
        
		ResponseEntity<CommercialUsageEntry> responseEntity = new ResponseEntity<CommercialUsageEntry>(newEntryValue, headers, HttpStatus.CREATED);
		return responseEntity;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageEntry> getCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryId") long commercialUsageEntryId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryId);
    	
    	CommercialUsageEntry commercialUserEntity = commercialUsageEntryRepository.findOne(commercialUsageEntryId);
    	if (commercialUserEntity == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	//FIXME should validate entry is part of usage report 
    	
		return new ResponseEntity<CommercialUsageEntry>(commercialUserEntity, HttpStatus.OK);
    }
    
    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.PUT,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageEntry> updateCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryId") long commercialUsageEntryId, @RequestBody CommercialUsageEntry newEntryValue) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryId);
    	Validate.isTrue(newEntryValue.getCommercialUsageEntryId() != null && newEntryValue.getCommercialUsageEntryId() == commercialUsageEntryId,"Must include commercialUsageEntryId in message");

    	CommercialUsageEntry entry = commercialUsageEntryRepository.save(newEntryValue);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	commercialUsage.addEntry(entry);
    	
		ResponseEntity<CommercialUsageEntry> responseEntity = new ResponseEntity<CommercialUsageEntry>(entry, HttpStatus.OK);
		return responseEntity;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_ENTRY,
    		method = RequestMethod.DELETE,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<?> deleteCommercialUsageEntry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageEntryId") long commercialUsageEntryId) {
    	authorizationChecker.checkCanAccessCommercialUsageEntry(commercialUsageId, commercialUsageEntryId);

    	commercialUsageEntryRepository.delete(commercialUsageEntryId);
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a country record and in addition all entries associated with the same country
     * 
     * @param commercialUsageId
     * @param commercialUsageCountId
     * @return
     */
    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_COUNTRY,
    		method = RequestMethod.DELETE,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<?> deleteCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageCountId") long commercialUsageCountId) {
    	authorizationChecker.checkCanAccessCommercialUsageCount(commercialUsageId, commercialUsageCountId);

    	CommercialUsageCountry commercialUsageCountry = commercialUsageCountryRepository.findOne(commercialUsageCountId);
    	if (commercialUsageCountry == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	commercialUsageCountryRepository.delete(commercialUsageCountId);
    	
    	List<CommercialUsageEntry> entries = commercialUsageEntryRepository.findByCountry(commercialUsageCountry.getCountry());
    	for (CommercialUsageEntry commercialUsageEntry : entries) {
			commercialUsageEntryRepository.delete(commercialUsageEntry);
		}
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_COUNTRIES,
    		method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageCountry> addCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId, @RequestBody CommercialUsageCountry newCountValue) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);
    	
    	commercialUsageCountryRepository.save(newCountValue);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	commercialUsage.addCount(newCountValue);
        
        HttpHeaders headers = new HttpHeaders();
        // FIXME flush and get ids back
        //headers.setLocation(ServletUriComponentsBuilder.fromPath(Routes.USAGE_REPORT_ENTRY).build().expand(newEntry.getCommercialUsageEntryId()).toUri());
        
		ResponseEntity<CommercialUsageCountry> responseEntity = new ResponseEntity<CommercialUsageCountry>(newCountValue, headers, HttpStatus.CREATED);
		return responseEntity;
    }
    
    @RequestMapping(value = Routes.USAGE_REPORT_COUNTRY,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageCountry> getCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageCountId") long commercialUsageCountId) {
    	authorizationChecker.checkCanAccessCommercialUsageCount(commercialUsageId, commercialUsageCountId);
    	
    	CommercialUsageCountry commercialUsageCount = commercialUsageCountryRepository.findOne(commercialUsageCountId);
    	if (commercialUsageCount == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	return new ResponseEntity<CommercialUsageCountry>(commercialUsageCount, HttpStatus.OK);
    }
    
    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_COUNTRY,
    		method = RequestMethod.PUT,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageCountry> updateCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageCountId") long commercialUsageCountId, @RequestBody CommercialUsageCountry newCountValue) {
    	authorizationChecker.checkCanAccessCommercialUsageCount(commercialUsageId, commercialUsageCountId);
    	Validate.isTrue(newCountValue.getCommercialUsageCountId() != null && newCountValue.getCommercialUsageCountId() == commercialUsageCountId, "Must include commercialUsageCountId in message");

    	// validate id not null?
    	
    	CommercialUsageCountry count = commercialUsageCountryRepository.save(newCountValue);
    	
    	CommercialUsage commercialUsage = commercialUsageRepository.findOne(commercialUsageId);
    	if (commercialUsage == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	commercialUsage.addCount(count);
    	
		ResponseEntity<CommercialUsageCountry> responseEntity = new ResponseEntity<CommercialUsageCountry>(count, HttpStatus.OK);
		return responseEntity;
    }
}
