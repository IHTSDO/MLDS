package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ca.intelliware.ihtsdo.mlds.Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class AffiliatesImportGeneratorIntegrationTest {
	@Resource AffiliatesImportGenerator affiliatesImportGenerator;
	@Resource AffiliatesMapper affiliatesMapper;
	
	@Before
	public void setup() {
	}
	
	
	@Test
	public void generateFileShouldGeneratePopulatedHeaderAndDataRows() throws IOException {
		String generated = affiliatesImportGenerator.generateFile(2);
		
		List<String> lines = IOUtils.readLines(new StringReader(generated));
		assertEquals(lines.size(), 3);

		// Header Row
		String[] headerLine = splitLine(lines.get(0));
		assertEquals(headerLine.length, affiliatesMapper.getMappings().size());
		assertEquals(headerLine[0], "member");

		// Data Row 1
		String[] dataRow1 = splitLine(lines.get(1));
		assertEquals(dataRow1.length, affiliatesMapper.getMappings().size());
		assertEquals("DK", dataRow1[0]);
		assertEquals("Example Other Text", dataRow1[4]);

		// Data Row 2
		String[] dataRow2 = splitLine(lines.get(2));
		assertEquals(dataRow2.length, affiliatesMapper.getMappings().size());
		assertEquals("DK", dataRow2[0]);
		assertEquals("Example Other Text 1", dataRow2[4]);
	}
	
	private String[] splitLine(String line) {
		return line.split(AffiliateFileFormat.COLUMN_SEPARATOR_REGEX, -1);
	}
}
