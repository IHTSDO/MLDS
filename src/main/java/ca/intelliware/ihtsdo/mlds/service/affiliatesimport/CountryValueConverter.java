package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;

@Service
public class CountryValueConverter extends ValueConverter {
	
	@Resource 
	CountryRepository countryRepository;

	@Override
	public Country toObject(String valueString) {
		return countryRepository.findOne(valueString);
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
	public String getExampleValue(String columnName) {
		return "CA";
	}
}