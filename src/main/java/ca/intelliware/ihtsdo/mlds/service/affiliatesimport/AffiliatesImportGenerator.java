package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.StringWriter;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

/**
 * Generate a valid affiliates import file with a specified number of rows.
 */
@Service
public class AffiliatesImportGenerator extends BaseAffiliatesGenerator {

	public String generateFile(int rows) {
		StringWriter writer = new StringWriter();
		writeHeader(writer);
		writeAffiliates(writer, rows);
		return writer.toString();
	}

	private void writeAffiliates(StringWriter writer, int rows) {
		for (int i = 0; i < rows; i++) {
			writeAffiliate(writer, i);
		}
	}

	private void writeAffiliate(StringWriter writer, int i) {
		SeparatorWriter separatorWriter = new SeparatorWriter();
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			separatorWriter.append(writer);
			String stringValue = fieldMapping.getExampleValue();
			// Must have a value to allow re-import for affiliate added by users in our system.
			if (Objects.equal(fieldMapping.columnName, "importKey")) {
				stringValue = generatePlaceHolderImportKey(i);
			}
			writer.append(stringValue);
		}
		appendLineEnding(writer);
	}
}
