package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Licensee;
import ca.intelliware.ihtsdo.mlds.repository.LicenseeRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "licensee", description = "Licensee API")
public class LicenseeResource {

	@Resource
	LicenseeRepository licenseeRepository;

	@Resource
	AuthorizationChecker authorizationChecker;

	@Resource
	SessionService sessionService;

	@RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = Routes.LICENSEES,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Licensee>> getLicensees() {
    	return new ResponseEntity<Collection<Licensee>>(licenseeRepository.findAll(), HttpStatus.OK);
    }

	
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.LICENSEES_ME,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Licensee>> getLicenseesMe() {
    	String username = sessionService.getUsernameOrNull();
    	return new ResponseEntity<Collection<Licensee>>(licenseeRepository.findByCreator(username), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.LICENSEES_CREATOR,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Licensee>> getLicenseesForUser(@PathVariable String username) {
    	authorizationChecker.checkCanAccessLicensee(username);
    	return new ResponseEntity<Collection<Licensee>>(licenseeRepository.findByCreator(username), HttpStatus.OK);
    }

}
