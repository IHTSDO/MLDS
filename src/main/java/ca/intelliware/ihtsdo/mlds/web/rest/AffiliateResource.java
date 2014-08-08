package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AffiliatesImporterService;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "affiliate", description = "Affiliate API")
public class AffiliateResource {

    private final Logger log = LoggerFactory.getLogger(AffiliateResource.class);
    
	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;

	@Resource
	ApplicationAuthorizationChecker applicationAuthorizationChecker;

	@Resource
	AffiliateAuditEvents affiliateAuditEvents;
	
	@Resource
	AffiliatesImporterService affiliatesImporterService; 
	
	@Resource
	SessionService sessionService;

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATES,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliates(@RequestParam String q) {
		List<Affiliate> affiliates;
		if (!Strings.isNullOrEmpty(q)) {
			affiliates = affiliateRepository.findByTextQuery("%" + q.toLowerCase() + "%");
		} else {
			affiliates = affiliateRepository.findAll();
		}
		return new ResponseEntity<Collection<Affiliate>>(affiliates, HttpStatus.OK);
    }

	
	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATES_ME,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesMe() {
    	String username = sessionService.getUsernameOrNull();
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreator(username), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATES_CREATOR,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesForUser(@PathVariable String username) {
    	applicationAuthorizationChecker.checkCanAccessAffiliate(username);
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreator(username), HttpStatus.OK);
    }
	
	
	@RolesAllowed({AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATES_IMPORT,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Collection<Affiliate>> importAffiliates( @RequestParam("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//FIXME Is this correct that we are assuming UTF8?
		String content = IOUtils.toString(file.getInputStream(),  Charsets.UTF_8);
		log.info("Uploaded "+content.length()+"\n"+content);
		affiliatesImporterService.importFromCSV(content);
		affiliateAuditEvents.logImport();
    	return new ResponseEntity<Collection<Affiliate>>((Collection<Affiliate>)null, HttpStatus.OK);
    }

	
	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<AffiliateDetails> updateAffiliateDetail(@PathVariable Long affiliateId) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	return new ResponseEntity<AffiliateDetails>(affiliate.getAffiliateDetails(), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<AffiliateDetails> updateAffiliateDetail(@PathVariable Long affiliateId, @RequestBody AffiliateDetails body) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	AffiliateDetails affiliateDetails = affiliate.getAffiliateDetails();
    	if (affiliateDetails == null) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	
    	Application application = affiliate.getApplication();
    	if (application == null || !Objects.equal(application.getApprovalState(), ApprovalState.APPROVED)) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	
    	copyAffiliateDetailsFields(affiliateDetails, body);
    	
    	affiliateDetailsRepository.save(affiliateDetails);
    	
    	return new ResponseEntity<AffiliateDetails>(affiliateDetails, HttpStatus.OK);
    }


	private void copyAffiliateDetailsFields(AffiliateDetails affiliateDetails, AffiliateDetails body) {
		copyAddressFieldsWithoutCountry(affiliateDetails.getAddress(), body.getAddress());
    	affiliateDetails.setAlternateEmail(body.getAlternateEmail());
    	copyAddressFields(affiliateDetails.getBillingAddress(), body.getBillingAddress());
    	// Can not update: email (validation and uniqueness challenges)
    	affiliateDetails.setFirstName(body.getFirstName());
    	affiliateDetails.setLandlineExtension(body.getLandlineExtension());
    	affiliateDetails.setLandlineNumber(body.getLandlineNumber());
    	affiliateDetails.setLastName(body.getLastName());
    	affiliateDetails.setMobileNumber(body.getMobileNumber());
    	// Can not update: OrganizationName, OrganizationType, OrganizationTypeOther
    	affiliateDetails.setThirdEmail(body.getThirdEmail());
	}

	private void copyAddressFields(MailingAddress address, MailingAddress body) {
		copyAddressFieldsWithoutCountry(address, body);
		address.setCountry(body.getCountry());
	}

	private void copyAddressFieldsWithoutCountry(MailingAddress address, MailingAddress body) {
		address.setCity(body.getCity());
		address.setPost(body.getPost());
		address.setStreet(body.getStreet());
	}

}
