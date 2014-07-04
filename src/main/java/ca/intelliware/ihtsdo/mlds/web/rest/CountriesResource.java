package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;

@RestController
public class CountriesResource {
	@Resource
	CountryRepository countryRepository;
	
    @RequestMapping(value = Routes.COUNTRIES,
            method = RequestMethod.GET,
            produces = "application/json")
    @PermitAll
    public List<Country> getCountries() {
    	return countryRepository.findAll();
    }

}
