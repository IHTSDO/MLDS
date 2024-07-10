package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.*;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class CommercialUsageResource {

	private static final String FILTER_STATE_SUBMITTED = "approvalState/not submitted eq false";

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

	@Resource
	CommercialUsageService commercialUsageService;

    @RequestMapping(value = Routes.USAGE_REPORTS,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<Collection<CommercialUsage>> getUsageReports(@PathVariable long affiliateId) {

    	Optional<Affiliate> affiliate = affiliateRepository.findById(affiliateId);
    	authorizationChecker.checkCanAccessAffiliate(String.valueOf(Optional.of(affiliate)));
    	if (affiliate.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    	return new ResponseEntity<Collection<CommercialUsage>>(affiliate.get().getCommercialUsages(), HttpStatus.OK);
    }

    @GetMapping(value = Routes.USAGE_REPORTS_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public ResponseEntity<CommercialUsageCollection> getAllUsageReports(
        @RequestParam(value="$filter", required=false) String filter,
        @RequestParam(value="page", defaultValue="0") int page,
        @RequestParam(value="size", defaultValue="50") int size,
        @RequestParam(value="orderby", required=false) String orderby) {

        Page<CommercialUsage> usageReports;
        Sort sort = createUsageReportsSort(orderby);
        Pageable pageRequest = PageRequest.of(page, size, sort);

        if (filter == null) {
            usageReports = commercialUsageRepository.findAll(pageRequest);
        } else if (Objects.equals(filter, FILTER_STATE_SUBMITTED)) {
            usageReports = commercialUsageRepository.findByNotStateAndEffectiveToIsNull(UsageReportState.NOT_SUBMITTED, pageRequest);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new CommercialUsageCollection(usageReports), HttpStatus.OK);
    }

    private Sort createUsageReportsSort(String orderby) {
        if (orderby == null || orderby.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "startDate"); // Default sort
        }
        String[] orderParams = orderby.split(",");
        return Sort.by(orderParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderParams[0]);
    }

    public static class CommercialUsageTransitionMessage {
		UsageReportTransition transition;

		public UsageReportTransition getTransition() {
    		return transition;
    	}

		public void setTransition(UsageReportTransition transition) {
    		this.transition = transition;
    	}
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

    	Optional<Affiliate> affiliateOptional = affiliateRepository.findById(affiliateId);


    	if (affiliateOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

        Affiliate affiliate = affiliateOptional.get();
        authorizationChecker.checkCanAccessAffiliate(affiliate);
    	CommercialUsage commercialUsage = null;
		List<CommercialUsage> commercialUsages = commercialUsageRepository.findActiveBySamePeriod(affiliate, submissionPeriod.getStartDate(), submissionPeriod.getEndDate());
		if (commercialUsages.size() > 0) {
			// Return the existing commercial usage unmodified
			commercialUsage = commercialUsages.get(0);
			return new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
		} else {
			commercialUsages = commercialUsageRepository.findActiveByMostRecentPeriod(affiliate);
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


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
    	if (commercialUsageOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
        CommercialUsage commercialUsage = commercialUsageOptional.get();

    	return new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
    }

	@RequestMapping(value = Routes.USAGE_REPORT_CONTEXT,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN })
	@Timed
    public @ResponseBody ResponseEntity<UsageContext> updateCommercialUsageContext(@PathVariable long commercialUsageId, @RequestBody UsageContext context) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);

        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

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

        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

    	commercialUsage.setType(type);

    	commercialUsage = commercialUsageRepository.save(commercialUsage);

    	return new ResponseEntity<UsageContext>(commercialUsage.getContext(), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = Routes.USAGE_REPORT_APPROVAL,
    		method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @Timed
	public @ResponseBody
	ResponseEntity<CommercialUsage> transitionCommercialUsageApproval(@PathVariable("commercialUsageId") long commercialUsageId,
			@RequestBody CommercialUsageTransitionMessage applyTransition) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
    	if (commercialUsageOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
        CommercialUsage commercialUsage = commercialUsageOptional.get();

    	commercialUsage = commercialUsageService.transitionCommercialUsageApproval(commercialUsage, applyTransition.getTransition());

		return new ResponseEntity<CommercialUsage>(commercialUsage, HttpStatus.OK);
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


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

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


        CommercialUsageEntry commercialUserEntity = commercialUsageEntryRepository.findByCommercialUsageEntryId(commercialUsageEntryId);
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


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

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
        commercialUsageEntryRepository.deleteById(commercialUsageEntryId);
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


        CommercialUsageCountry commercialUsageCountry = commercialUsageCountryRepository.findByCommercialUsageCountId(commercialUsageCountId);
    	if (commercialUsageCountry == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();


        commercialUsageCountryRepository.deleteByCommercialUsageCountId(commercialUsageCountId);

		// Yes delete usage from this country, but only for the current usage report!
		List<CommercialUsageEntry> entries = commercialUsageEntryRepository.findByCountryAndCommercialUsage(
				commercialUsageCountry.getCountry(), commercialUsage);
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
	public @ResponseBody
	ResponseEntity<?> addCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId,
			@RequestBody CommercialUsageCountry newCountValue) {
    	authorizationChecker.checkCanAccessUsageReport(commercialUsageId);


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

		// MLDS-889 We're seeing duplicate country counts being created which I'm guessing happens when they come in without a primary key
		// but already exist in the database. I'll also sync this block, in case we're looking at double clicking.
		synchronized (commercialUsage) {
			// If this new Value is not known as already being in the database, check if we have anything for the same country
			if (newCountValue.getCommercialUsageCountId() == null && commercialUsage.exists(newCountValue)) {
				return new ResponseEntity<String>("Country already exists for this usage report.", HttpStatus.CONFLICT);
			}
			commercialUsageCountryRepository.save(newCountValue);
			commercialUsage.addCount(newCountValue);
		}

        HttpHeaders headers = new HttpHeaders();
        // FIXME flush and get ids back
        //headers.setLocation(ServletUriComponentsBuilder.fromPath(Routes.USAGE_REPORT_ENTRY).build().expand(newEntry.getCommercialUsageEntryId()).toUri());

		ResponseEntity<CommercialUsageCountry> responseEntity = new ResponseEntity<CommercialUsageCountry>(newCountValue, headers, HttpStatus.CREATED);
		return responseEntity;
    }

	private void synchronize() {
		// TODO Auto-generated method stub

	}

	@RequestMapping(value = Routes.USAGE_REPORT_COUNTRY,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @Timed
    public @ResponseBody ResponseEntity<CommercialUsageCountry> getCommercialUsageCountry(@PathVariable("commercialUsageId") long commercialUsageId, @PathVariable("commercialUsageCountId") long commercialUsageCountId) {
    	authorizationChecker.checkCanAccessCommercialUsageCount(commercialUsageId, commercialUsageCountId);


        CommercialUsageCountry commercialUsageCount = commercialUsageCountryRepository.findByCommercialUsageCountId(commercialUsageCountId);
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


        Optional<CommercialUsage> commercialUsageOptional = commercialUsageRepository.findById(commercialUsageId);
        if (commercialUsageOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommercialUsage commercialUsage = commercialUsageOptional.get();

    	commercialUsage.addCount(count);

		ResponseEntity<CommercialUsageCountry> responseEntity = new ResponseEntity<CommercialUsageCountry>(count, HttpStatus.OK);
		return responseEntity;
    }


    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/
    @RequestMapping(value = Routes.REVIEW_USAGE_REPORTS,
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.STAFF,AuthoritiesConstants.ADMIN })
    @Timed
    @Transactional
    public @ResponseBody Collection<Object[]> reviewUsageReportCsv(){
        Collection<Object[]> collections=commercialUsageRepository.findUsageReport();
        return ResponseEntity.ok().body(collections).getBody();
    }
    /*MLDS 985---To Download Commercial usage CSV files this below code is used*/
}
