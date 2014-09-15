package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
public class EnumValueConverterTest {

	private EnumValueConverter fixture;

	LineRecord lineRecord;

	private FieldMapping fieldMapping;

	private ImportResult result;

	public static enum SomeEnum {
		ONE_ENUM,
		ANOTHER_ENUM
	}
	
	@Before
	public void setUp() {
		fixture = new EnumValueConverter(SomeEnum.class);
		
		lineRecord = new LineRecord(1, Arrays.asList("field"), false);
		fieldMapping = new FieldMapping(2, "columnName", null, null, null);
		result = new ImportResult();
	}

	
	@Test
	public void toObjectShouldMatchOnString() {
		assertEquals(fixture.toObject("ONE_ENUM"), SomeEnum.ONE_ENUM);
		
		assertEquals(fixture.toObject("UNKNOWN_ENUM"), null);
		assertEquals(fixture.toObject(null), null);
	}
	
	@Test
	public void validateShouldPassForEnumString() {
		fixture.validate("ONE_ENUM", lineRecord, fieldMapping, result);
		
		assertTrue(result.errors.isEmpty());
	}

	@Test
	public void validateShouldAddErrorForUnknownEnum() {
		fixture.validate("UNKNOWN_ENUM", lineRecord, fieldMapping, result);
		
		assertEquals(result.errors.size(), 1);
	}

	@Test
	public void validateShouldIndicateInputAndValidOptionsForFailure() {
		fixture.validate("UNKNOWN_ENUM", lineRecord, fieldMapping, result);
		
		assertThat(result.errors.get(0), Matchers.containsString("value=UNKNOWN_ENUM"));
		assertThat(result.errors.get(0), Matchers.containsString("[ONE_ENUM, ANOTHER_ENUM]"));
	}
	
	@Test
	public void toStringShouldReturnString() {
		assertEquals(fixture.toString(SomeEnum.ONE_ENUM), "ONE_ENUM");
		
		assertEquals(fixture.toString(null), "");
	}
	
	@Test
	public void getOptionsShouldProvideSortedListOfOptionsAsStrings() {
		assertEquals(fixture.getOptions(), Lists.newArrayList("ANOTHER_ENUM", "ONE_ENUM"));
	}
	
	
}
