package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportSpec.ColumnSpec;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class AffiliatesExporterServiceTest {
	@Mock
	AffiliatesImportGenerator affiliatesImportGenerator;
	
	@Mock
	AffiliateRepository affiliateRepository;
	
	@Mock
	AffiliatesMapper affiliatesMapper;
	
	AffiliatesExporterService fixture;
	
	List<FieldMapping> fieldMappings;

	private FieldMapping importKeyColumn;

	private FieldMapping applicationTypeColumn;

	@Before
	public void setup() {
		fixture = new AffiliatesExporterService();
		
		fixture.affiliatesImportGenerator = affiliatesImportGenerator;
		fixture.affiliateRepository = affiliateRepository;
		fixture.affiliatesMapper = affiliatesMapper;
		
		importKeyColumn = new FieldMapping(0, "importKey", Affiliate.class, new Accessor(Affiliate.class, "importKey"), new ValueConverter());
		applicationTypeColumn = new FieldMapping(1, "type", AffiliateDetails.class, new Accessor(AffiliateDetails.class, "type"), new EnumValueConverter(AffiliateType.class));
		applicationTypeColumn.required = true;
		fieldMappings = Lists.newArrayList(importKeyColumn, applicationTypeColumn);
		
		when(affiliatesMapper.getMappings()).thenReturn(fieldMappings);
	}
	
	@Test
	public void exportSpecShouldIncludeGeneratedExample() {
		when(affiliatesImportGenerator.generateFile(Mockito.anyInt())).thenReturn("Test Example");
		
		AffiliatesImportSpec spec = fixture.exportSpec();
		assertEquals(spec.example, "Test Example");
	}
	
	@Test
	public void exportSpecShouldIncludeEachColumn() {
		AffiliatesImportSpec spec = fixture.exportSpec();
		assertEquals(spec.columns.size(), 2);
	}

	@Test
	public void exportSpecShouldIncludeAllColumnDetails() {
		when(affiliatesImportGenerator.generateValue(Mockito.eq(applicationTypeColumn), Mockito.anyInt(), Mockito.any(AffiliatesImportGenerator.GeneratorContext.class))).thenReturn("COMMERCIAL");
		
		AffiliatesImportSpec spec = fixture.exportSpec();
		ColumnSpec columnSpec = spec.columns.get(1);
		assertEquals(columnSpec.attributeClass, "AffiliateType");
		assertEquals(columnSpec.columnName, "type");
		assertEquals(columnSpec.example, "COMMERCIAL");
		assertEquals(columnSpec.options, Lists.newArrayList("ACADEMIC", "COMMERCIAL", "INDIVIDUAL", "OTHER"));
		assertEquals(columnSpec.required, true);
	}
	
	@Test
	public void exportToCSVShouldIncludeHeaderAndDataRow() throws IOException {
		Affiliate affiliate = new Affiliate(123L);
		affiliate.setImportKey("IMPORTED-1");
		PrimaryApplication primaryApplication = new PrimaryApplication(1);
		primaryApplication.setAffiliateDetails(new AffiliateDetails());
		primaryApplication.getAffiliateDetails().setType(AffiliateType.OTHER);
		// FIXME MLDS-32 MB is this the right thing?
		affiliate.setAffiliateDetails(new AffiliateDetails());
		affiliate.getAffiliateDetails().setType(AffiliateType.OTHER);
		affiliate.setApplication(primaryApplication);
		
		when(affiliateRepository.findAll()).thenReturn(Lists.newArrayList(affiliate));
		
		String generated = fixture.exportToCSV();
		
		List<String> lines = IOUtils.readLines(new StringReader(generated));
		assertEquals(lines.size(), 2);

		// Header Row
		String[] headerLine = splitLine(lines.get(0));
		assertEquals(headerLine.length, 2);
		assertEquals(headerLine[0], "importKey");
		assertEquals(headerLine[1], "type");

		// Data Row 1
		String[] dataRow1 = splitLine(lines.get(1));
		assertEquals(dataRow1.length, 2);
		assertEquals("IMPORTED-1", dataRow1[0]);
		assertEquals("OTHER", dataRow1[1]);
	}

	@Test
	public void exportToCSVShouldGenerateImportKeyIfMissing() throws IOException {
		Affiliate affiliate = new Affiliate(123L);
		affiliate.setImportKey(null);
		
		when(affiliateRepository.findAll()).thenReturn(Lists.newArrayList(affiliate));
		
		String generated = fixture.exportToCSV();
		
		List<String> lines = IOUtils.readLines(new StringReader(generated));

		// Data Row 1
		String[] dataRow1 = splitLine(lines.get(1));
		assertEquals(dataRow1[0], "AFFILIATE-123");
	}

	private String[] splitLine(String line) {
		return line.split(AffiliateFileFormat.COLUMN_SEPARATOR_REGEX, -1);
	}
}
