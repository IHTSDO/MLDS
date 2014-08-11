package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

public class StringValueConverter extends ValueConverter {

	@Override
	public Object toObject(String value) {
		return value;
	}

	@Override
	public void validate(String value, LineRecord record, FieldMapping mapping, ImportResult result) {
		// OK
	}
}