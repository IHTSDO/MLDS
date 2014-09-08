package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Holds the field values read from a single line of the CSV file
 */
public class LineRecord {
	boolean header = false;
	long lineNumber = 0;
	List<String> fields;
	boolean isBlank = false;
	public LineRecord(long lineNumber, List<String> fields, boolean isBlank) {
		this.lineNumber = lineNumber;
		this.fields = fields;
		this.isBlank = isBlank;
	}
	
	private String fieldValue(int fieldIndex) {
		return fields.get(fieldIndex);
	}
	
	public void validate(ImportResult result, AffiliatesMapper affiliatesMapper) {
		if (isBlank) {
			return;
		}
		if (fields.size() != affiliatesMapper.getMappings().size()) {
			result.addError(this, "Incorrect number of fields: found="+ fields.size()+" required="+affiliatesMapper.getMappings().size());
		} else if (header) {
			validateHeaderRecord(result, affiliatesMapper);
		} else {
			validateDataRecord(result, affiliatesMapper);
		}
	}

	private void validateDataRecord(ImportResult result, AffiliatesMapper affiliatesMapper) {
		for (FieldMapping mapping : affiliatesMapper.getMappings()) {
			String valueString = fieldValue(mapping.columnIndex);
			mapping.validateDataValue(valueString, this, result);
		}
	}
	
	private void validateHeaderRecord(ImportResult result, AffiliatesMapper affiliatesMapper) {
		for (FieldMapping mapping : affiliatesMapper.getMappings()) {
			String valueString = fieldValue(mapping.columnIndex);
			if (!StringUtils.equalsIgnoreCase(mapping.columnName, valueString)) {
				result.addError(this, mapping, "Header value="+valueString+" does not match title="+mapping.columnName);
			}
		}
	}
	
	public void setValuesOfMatchingClass(Object rootObject, Class<?> matchingClazz, AffiliatesMapper affiliatesMapper) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		for (FieldMapping mapping : affiliatesMapper.getMappings()) {
			if (matchingClazz.equals(mapping.rootClazz)) {
				String valueString = fieldValue(mapping.columnIndex);
				mapping.setValue(rootObject, valueString);
			}
		}
	}
}