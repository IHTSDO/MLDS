package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CountryValueConverter extends ValueConverter {

	@Resource
	CountryRepository countryRepository;

	@Override
	public Country toObject(String valueString) {
        return countryRepository.findById(valueString).orElse(null);
	}

	@Override
	public void validate(String valueString, LineRecord lineRecord, FieldMapping mapping, ImportResult result) {
		Country member = toObject(valueString);
		if (member == null) {
			result.addError(lineRecord, mapping, "Field value="+valueString+" not one of the recognized ISO 3166-1 alpha-2 country codes");
		}
	}

	@Override
	public String toString(Object value) {
		if (value != null) {
			Country country = (Country) value;
			return country.getIsoCode2();
		} else {
			return "";
		}
	}

	@Override
	public List<String> getOptions() {
		List<String> options = new ArrayList<String>();
		for (Country country : countryRepository.findAll()) {
			options.add(country.getIsoCode2());
		}
		Collections.sort(options);
		return options;
	}
}
