package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Maps an individual CSV column to an entity field
 */
public class FieldMapping {
	int columnIndex;
	String columnName;
	Class rootClazz;
	Accessor accessor;
	ValueConverter valueConverter;
	boolean required = false;
	public FieldMapping(int columnIndex, String columnName, Class rootClazz, Accessor accessor, ValueConverter valueConverter) {
		Validate.notNull(rootClazz);
		Validate.notNull(accessor);
		Validate.notNull(valueConverter);
		this.columnIndex = columnIndex;
		this.columnName = columnName;
		this.rootClazz = rootClazz;
		this.accessor = accessor;
		this.valueConverter = valueConverter;
	}
	public FieldMapping required() {
		required = true;
		return this;
	}
	
	public void validateDataValue(String valueString, LineRecord lineRecord, ImportResult result) {
		if (required && StringUtils.isBlank(valueString)) {
			result.addError(lineRecord, this, "Missing required field");
		} else if (! StringUtils.isBlank(valueString)) {
			valueConverter.validate(valueString, lineRecord, this, result);
		}
	}

	public void setValue(Object rootObject, String valueString) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Object value = valueConverter.toObject(valueString);
		accessor.setValue(rootObject, value);
	}

}