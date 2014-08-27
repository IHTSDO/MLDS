package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class CountryValueConverterTest {
    @Mock
    private CountryRepository countryRepository;
    
	private CountryValueConverter fixture;
	
	private Country countryAB;
	private Country countryST;

	private LineRecord lineRecord;

	private FieldMapping fieldMapping;

	private ImportResult result;

    @Before
    public void setup() {
    	fixture = new CountryValueConverter();
    	
    	fixture.countryRepository = countryRepository;
    	
    	countryAB = new Country("AB", "ABC", "AB Country");
    	countryST = new Country("ST", "STT", "ST Country");
    	
		lineRecord = new LineRecord(1, new String[]{"field"}, false);
		fieldMapping = new FieldMapping(2, "columnName", null, null, null);
		result = new ImportResult();
    }
    
    @Test
    public void toStringShouldReturnEmptyStringForNull() {
    	assertEquals(fixture.toString(null), "");
    }

    @Test
    public void toStringShouldReturnCountry() {
    	assertEquals(fixture.toString(countryAB), "AB");
    }

    @Test
    public void toObjectShouldReturnNullForNullLookup() {
    	assertEquals(fixture.toObject(null), null);
    }

    @Test
    public void toObjectShouldReturnNullForUknownCountry() {
    	assertEquals(fixture.toObject("ZZ"), null);
    }

    @Test
    public void toObjectShouldReturnCountry() {
    	when(countryRepository.findOne("AB")).thenReturn(countryAB);
    	
    	assertEquals(fixture.toObject("AB"), countryAB);
    }
    
    @Test
    public void validateShouldPassForKnownCountry() {
    	when(countryRepository.findOne("AB")).thenReturn(countryAB);
    	
    	fixture.validate("AB", lineRecord, fieldMapping, result);
    	
    	assertTrue(result.errors.isEmpty());
    }

    @Test
    public void validateShouldAddErrorForUnknownCountry() {
    	when(countryRepository.findOne("ZZ")).thenReturn(null);
    	
    	fixture.validate("ZZ", lineRecord, fieldMapping, result);
    	
    	assertEquals(result.errors.size(), 1);
    }
    
    @Test
    public void getOptionsShouldProvideSortedListOfOptionsAsStrings() {
    	when(countryRepository.findAll()).thenReturn(Lists.newArrayList(countryST, countryAB));
    	
    	assertEquals(fixture.getOptions(), Lists.newArrayList("AB", "ST"));
    }
}
