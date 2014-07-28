package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "affiliate", description = "Affiliate API")
public class AffiliateResource {

	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;

	@Resource
	ApplicationAuthorizationChecker authorizationChecker;

	@Resource
	SessionService sessionService;

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATES,
    		method = RequestMethod.GET,
            produces = "application/json")
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
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesMe() {
    	String username = sessionService.getUsernameOrNull();
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreator(username), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATES_CREATOR,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesForUser(@PathVariable String username) {
    	authorizationChecker.checkCanAccessAffiliate(username);
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreator(username), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<AffiliateDetails> updateAffiliateDetail(@PathVariable Long affiliateId) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	authorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	return new ResponseEntity<AffiliateDetails>(affiliate.getAffiliateDetails(), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.PUT,
            produces = "application/json")
    public @ResponseBody ResponseEntity<AffiliateDetails> updateAffiliateDetail(@PathVariable Long affiliateId, @RequestBody AffiliateDetails body) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	authorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	AffiliateDetails affiliateDetails = affiliate.getAffiliateDetails();
    	if (affiliateDetails == null) {
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
    	//FIXME can they set email?
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
