package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


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

	public List<String> getOptions() {
		return new ArrayList<String>();
	}
}