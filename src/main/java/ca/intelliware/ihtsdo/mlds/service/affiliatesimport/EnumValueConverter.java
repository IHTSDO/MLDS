package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class EnumValueConverter extends ValueConverter {
	private Class<?> enumClazz;

	public EnumValueConverter(Class<?> enumClazz) {
		this.enumClazz = enumClazz;
	}

	@Override
	public Object toObject(String valueString) {
		for (Object enumConstant : enumClazz.getEnumConstants()) {
			if (StringUtils.equals(enumConstant.toString(), valueString)) {
				return enumConstant;
			}
		}
		return null;
	}

	@Override
	public void validate(String valueString, LineRecord lineRecord, FieldMapping mapping, ImportResult result) {
		Object[] enumConstants = enumClazz.getEnumConstants();
		for (Object enumConstant : enumConstants) {
			if (StringUtils.equals(enumConstant.toString(), valueString)) {
				return;
			}
		}
		String enumOptions = enumConstantsToString(enumConstants);
		result.addError(lineRecord, mapping, "Field value="+valueString+" not one of options: "+enumOptions);
	}

	private String enumConstantsToString(Object[] enumConstants) {
		//FIXME is there not a standard way of printing out enum constants?
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < enumConstants.length; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(enumConstants[i].toString());
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public String getExampleValue(String columnName) {
		return enumClazz.getEnumConstants()[0].toString();
	}
	
	@Override
	public List<String> getOptions() {
		List<String> options = new ArrayList<String>();
		for (Object enumConstant : enumClazz.getEnumConstants()) {
			options.add(enumConstant.toString());
		}
		Collections.sort(options);
		return options;
	}

}