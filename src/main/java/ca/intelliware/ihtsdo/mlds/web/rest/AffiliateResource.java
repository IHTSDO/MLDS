package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
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

}
