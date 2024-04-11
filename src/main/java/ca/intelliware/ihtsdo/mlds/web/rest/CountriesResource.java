package ca.intelliware.ihtsdo.mlds.web.rest;


import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        Optional<Country> optionalCountry = countryRepository.findById(isoCode2);
        if (optionalCountry.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Country country = optionalCountry.get();
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
        countryRepository.deleteByIsoCode2(isoCode2);
    }

}
