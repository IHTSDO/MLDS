package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Licensee;
import ca.intelliware.ihtsdo.mlds.repository.LicenseeRepository;
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
	
    @RequestMapping(value = Routes.LICENSEES_USERNAME,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Collection<Licensee>> getLicensees() {
    	String username = sessionService.getUsernameOrNull();
    	System.out.println("username="+username);
    	return new ResponseEntity<Collection<Licensee>>(licenseeRepository.findByCreator(username), HttpStatus.OK);
    }

}
