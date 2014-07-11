package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Package;
import ca.intelliware.ihtsdo.mlds.repository.PackageRepository;

import com.wordnik.swagger.annotations.Api;

@RestController
@Api(value = "package", description = "Package and Version Release API")
public class PackagesResource {

	@Resource
	PackageRepository packageRepository;

	@Resource
	AuthorizationChecker authorizationChecker;

	@RequestMapping(value = Routes.PACKAGE,
    		method = RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity<Package> getPackage(@PathVariable long packageId) {
    	authorizationChecker.checkCanAccessPackage(packageId);
    	
    	Package packageEntity = packageRepository.findOne(packageId);
    	if (packageEntity == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	} 
    	
    	return new ResponseEntity<Package>(packageEntity, HttpStatus.OK);
    }

}
