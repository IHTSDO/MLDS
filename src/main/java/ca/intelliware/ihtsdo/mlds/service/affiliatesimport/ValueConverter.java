package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;


public class ValueConverter {
	public Object toObject(String value) {
		return value;
	}
	
	public void validate(String value, LineRecord record, FieldMapping mapping, ImportResult result) {
		// Ok!
	}
	
	public String toString(Object value) {
		return (value == null) ? "" : StringUtils.trim(value.toString());
	}

	public String getExampleValue(int hint, String columnName) {
		return WordUtils.capitalizeFully("Example "+StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(columnName), " "))+" "+hint;
	}

	public List<String> getOptions() {
		return new ArrayList<String>();
	}
}