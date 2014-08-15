package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ValueConverterTest {
	private ValueConverter fixture;
	
	private LineRecord lineRecord;

	private FieldMapping fieldMapping;

	private ImportResult result;

    @Before
    public void setup() {
    	fixture = new ValueConverter();
    	
		lineRecord = new LineRecord(1, new String[]{"field"}, false);
		fieldMapping = new FieldMapping(2, "columnName", null, null, null);
		result = new ImportResult();
    }
    
    @Test
    public void toStringShouldReturnEmptyStringForNull() {
    	assertEquals(fixture.toString(null), "");
    }

    @Test
    public void toStringShouldReturnValueAsString() {
    	assertEquals(fixture.toString(123), "123");
    	assertEquals(fixture.toString("A String"), "A String");
    }

    @Test
    public void toObjectShouldReturnNullForNullLookup() {
    	assertEquals(fixture.toObject(null), null);
    }

    @Test
    public void toObjectShouldReturnValue() {
    	assertEquals(fixture.toObject("A String"), "A String");
    }

    
    @Test
    public void validateShouldAlwaysPass() {
    	fixture.validate("Some Value", lineRecord, fieldMapping, result);
    	
    	assertTrue(result.errors.isEmpty());
    }
    
    @Test
    public void getOptionsShouldProvideEmpyList() {
    	assertEquals(fixture.getOptions(), Lists.newArrayList());
    }
}
