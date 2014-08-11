package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
	boolean success = true;
	long readRecords = -1;
	long importedRecords = -1;
	List<String> errors = new ArrayList<String>();

	public boolean isSuccess() {
		return success;
	}
	public long getReadRecords() {
		return readRecords;
	}
	public long getImportedRecords() {
		return importedRecords;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void addError(LineRecord lineRecord, FieldMapping mapping, String error) {
		addError("Line:"+lineRecord.lineNumber+" Col:"+mapping.columnIndex+":"+mapping.columnName+" "+error);
	}
	public void addError(LineRecord lineRecord, String error) {
		addError("Line:"+lineRecord.lineNumber+" "+error);
	}
	public void addException(Exception e) {
		addError("Exception "+e);
	}
	private void addError(String message) {
		errors.add(message);
		success = false;
	}
}