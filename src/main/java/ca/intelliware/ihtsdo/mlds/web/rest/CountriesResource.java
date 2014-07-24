package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

import com.codahale.metrics.annotation.Timed;

@RestController
public class CountriesResource {
    private final Logger log = LoggerFactory.getLogger(CountriesResource.class);
    
	@Resource
	CountryRepository countryRepository;
	
    @RequestMapping(value = Routes.COUNTRIES,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public List<Country> getCountries() {
    	return countryRepository.findAll();
    }

    @RequestMapping(value = Routes.COUNTRIES +"/{isoCode2}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PermitAll
    public ResponseEntity<Country> get(@PathVariable String isoCode2) {
        log.debug("REST request to get Country : {}", isoCode2);
        Country country = countryRepository.findOne(isoCode2);
        if (country == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(country, HttpStatus.OK);
    }


    @RequestMapping(value = Routes.COUNTRIES,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void create(@RequestBody Country country) {
        log.debug("REST request to save Country : {}", country);
        countryRepository.save(country);
    }

    @RequestMapping(value = Routes.COUNTRIES + "/{isoCode2}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable String isoCode2) {
        log.debug("REST request to delete Country : {}", isoCode2);
        countryRepository.delete(isoCode2);
    }

}
