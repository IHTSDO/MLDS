package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportGenerator.GeneratorContext;

@RunWith(MockitoJUnitRunner.class)
public class AffiliatesImportGeneratorTest {
	private AffiliatesImportGenerator fixture;
	
	private FieldMapping fieldMapping;

	private GeneratorContext context;

	@Before
	public void setup() {
		fixture = new AffiliatesImportGenerator();
		fieldMapping = new FieldMapping(0, "column", Object.class, null, new ValueConverter());
		context = new AffiliatesImportGenerator.GeneratorContext();
		context.setSourceCountryKey("CA");
		context.setSourceMemberKey("CA");
	}
	
	@Test
	public void generateValueForBasicMappingShouldGenerateStringWithoutHintOnFirstRow() {
		fieldMapping.columnName = "testColumn";
		String value = fixture.generateValue(fieldMapping, 0, context);
		assertEquals(value, "Example Test Column");
	}

	@Test
	public void generateValueForBasicMappingShouldGenerateStringWithHint() {
		fieldMapping.columnName = "testColumn";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "Example Test Column 1");
	}

	@Test
	public void generateValueShouldOverideImportKeyToEnsureValuePresent() {
		fieldMapping.columnName = "importKey";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "AFFILIATE-1");
	}

	@Test
	public void generateValueShouldOverideMemberToBeConsistent() {
		fieldMapping.columnName = "member";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "CA");
	}

	@Test
	public void generateValueShouldOveridePrimaryCountryToBeConsistentWithMember() {
		fieldMapping.columnName = "addressCountry";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "CA");
	}

	@Test
	public void generateValueForEmail() {
		fieldMapping.columnName = "someEmail";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "someEmail+1@email.com");
	}

	@Test
	public void generateValueForStreet() {
		fieldMapping.columnName = "someStreet";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "1 Some Street");
	}

	@Test
	public void generateValueForCity() {
		fieldMapping.columnName = "someCity";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "Jerup");
	}

	@Test
	public void generateValueForPost() {
		fieldMapping.columnName = "somePost";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "9108");
	}

	@Test
	public void generateValueForTelephoneNumber() {
		fieldMapping.columnName = "someNumber";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals("+1 416 709 0001", value);
	}

	@Test
	public void generateValueForTelephoneExtension() {
		fieldMapping.columnName = "someExtension";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals("707", value);
	}

	@Test
	public void generateValueForFirstName() {
		fieldMapping.columnName = "firstName";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "Lucas");
	}

	@Test
	public void generateValueForLastName() {
		fieldMapping.columnName = "lastName";
		String value = fixture.generateValue(fieldMapping, 1, context);
		assertEquals(value, "Nielsen 1");
	}
}
