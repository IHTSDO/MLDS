package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import org.apache.commons.lang.Validate;

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
}