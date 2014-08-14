package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

/**
 * Generate a valid affiliates import file with a specified number of rows.
 */
@Service
public class AffiliatesImportGenerator extends BaseAffiliatesGenerator {

	public String generateFile(int rows) {
		GeneratorContext context = new GeneratorContext();
		context.rows = rows;
		
		return generateFile(context);
	}

	public String generateFile(GeneratorContext context) {
		StringWriter writer = new StringWriter();
		writeHeader(writer);
		writeAffiliates(writer, context);
		return writer.toString();
	}

	private void writeAffiliates(StringWriter writer, GeneratorContext context) {
		for (int i = 0; i < context.rows; i++) {
			writeAffiliate(writer, i, context);
		}
	}

	private void writeAffiliate(StringWriter writer, int i, GeneratorContext context) {
		SeparatorWriter separatorWriter = new SeparatorWriter();
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			separatorWriter.append(writer);
			String stringValue = generateValue(fieldMapping, i, context);
			writer.append(stringValue);
		}
		appendLineEnding(writer);
	}

	private String generateValue(FieldMapping fieldMapping, int i, GeneratorContext context) {
		String stringValue = fieldMapping.getExampleValue(i);
		// Must have a value to allow re-import for affiliate added by users in our system.
		if (Objects.equal(fieldMapping.columnName, "importKey")) {
			stringValue = context.getBaseKey()+i;
		} else if (StringUtils.containsIgnoreCase(fieldMapping.columnName, "email")) {
			stringValue = generateEmail(fieldMapping, i, context);
		} else if (StringUtils.containsIgnoreCase(fieldMapping.columnName, "street")) {
			stringValue = generateStreet(fieldMapping, i, context);
		} else if (StringUtils.containsIgnoreCase(fieldMapping.columnName, "number")) {
			stringValue = generateTelephoneNumber(fieldMapping, i, context);
		} else if (StringUtils.containsIgnoreCase(fieldMapping.columnName, "first")) {
			stringValue = generateFirstName(fieldMapping, i, context);
		} else if (StringUtils.containsIgnoreCase(fieldMapping.columnName, "last")) {
			stringValue = generateLastName(fieldMapping, i, context);
		}
		
		return stringValue;
	}
	
	private String generateStreet(FieldMapping fieldMapping, int i, GeneratorContext context) {
		return Integer.toString(i)+ " "+ (WordUtils.capitalizeFully(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(fieldMapping.columnName), " ")));
	}

	private String generateFirstName(FieldMapping fieldMapping, int i, GeneratorContext context) {
		//From http://en.wikipedia.org/wiki/List_of_most_popular_given_names
		return generateNameFromList(i, new String[] {"William", "Lucas", "Victor", "Noah", "Sofia", "Ida", "Isabella", "Emma", "Freja"});
	}

	private String generateLastName(FieldMapping fieldMapping, int i, GeneratorContext context) {
		//From http://en.wikipedia.org/wiki/List_of_most_common_surnames_in_Europe
		return generateNameFromList(i, new String[] {"Jensen", "Nielsen", "Hansen", "Pedersen", "Andersen", "Christensen", "Larsen", "Sørensen", "Rasmussen"})
				+ appendUniquenessHint(i);
	}

	private String appendUniquenessHint(int hint) {
		return appendUniquenessHint(hint, " ");
	}
	
	private String appendUniquenessHint(int hint, String separator) {
		return hint > 0 ? separator+hint : "";
	}

	private String generateNameFromList(int i, String[] options) {
		return options[i%options.length];
	}

	private String generateTelephoneNumber(FieldMapping fieldMapping, int i, GeneratorContext context) {
		return "+1 416 "+StringUtils.leftPad(Integer.toString(fieldMapping.columnName.hashCode()%1000), 3, '0')+" "+StringUtils.leftPad(Integer.toString(i), 4, '0');
	}

	private String generateEmail(FieldMapping fieldMapping, int i, GeneratorContext context) {
		return fieldMapping.columnName+appendUniquenessHint(i, "+")+"@email.com";
	}


	public static class GeneratorContext {
		private int rows = 1;
		private String baseKey = "AFFILIATE-";
		
		public int getRows() {
			return rows;
		}
		public void setRows(int rows) {
			this.rows = rows;
		}
		public String getBaseKey() {
			return baseKey;
		}
		public void setBaseKey(String baseKey) {
			this.baseKey = baseKey;
		}
	}
}
