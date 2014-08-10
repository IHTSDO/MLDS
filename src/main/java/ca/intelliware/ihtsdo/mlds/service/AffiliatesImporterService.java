package ca.intelliware.ihtsdo.mlds.service;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

@Service
@Transactional
public class AffiliatesImporterService {

	@Resource ApplicationRepository applicationRepository;
	@Resource SessionService sessionService;
	@Resource AffiliateRepository affiliateRepository;
	@Resource MemberRepository memberRepository;

	private static final List<FieldMapping> MAPPINGS = new ArrayList<FieldMapping>();
	static {
		MAPPINGS.add(new FieldMapping("member", Affiliate.class, "homeMember", true));
		MAPPINGS.add(new FieldMapping("key", Affiliate.class, "sourceKey", true));
		MAPPINGS.add(new FieldMapping("type", Affiliate.class, "type", true));
	}
	private static final String[] FIELDS = {"affiliate.member", "source.key", "affiliate.affiliateType"};
	
	public ImportResult importFromCSV(String contents) throws IOException {
		ImportResult result = new ImportResult();
		try {
			importContents(contents, result);
		} catch (Exception e) {
			result.addException(e);
		}
		return result;
	}

	private void importContents(String contents, ImportResult result) throws IOException {
		List<LineRecord> lines = split(contents);
		result.readRecords = lines.size();
		validateLines(lines, result);
		if (result.success) {
			//	TODO Create affiliate records
		}
		
	}

	private void validateLines(List<LineRecord> lines, ImportResult result) {
		for (LineRecord lineRecord : lines) {
			if (lineRecord.isBlank) {
				continue;
			}
			if (lineRecord.fields.length != MAPPINGS.size()) {
				result.addError(lineRecord, "Incorrect number of fields: found="+ lineRecord.fields.length+" required="+FIELDS.length);
			} else if (lineRecord.header) {
				for (int i = 0; i < MAPPINGS.size(); i++) {
					FieldMapping mapping = MAPPINGS.get(i);
					String valueString = lineRecord.fields[i];
					if (!StringUtils.equalsIgnoreCase(mapping.columnName, valueString)) {
						result.addError(lineRecord, i, "Header value="+valueString+" does not match title="+mapping.columnName);
					}
				}
			} else {
				for (int i = 0; i < MAPPINGS.size(); i++) {
					FieldMapping mapping = MAPPINGS.get(i);
					String valueString = lineRecord.fields[i];
					if (mapping.required && StringUtils.isBlank(valueString)) {
						result.addError(lineRecord, i, "Missing required field");
					} else {
						validateFieldValue(result, lineRecord, i , mapping, valueString);
					}
				}
			}
		}
		
	}

	private void validateFieldValue(ImportResult result, LineRecord lineRecord, int fieldIndex, FieldMapping mapping, String valueString) {
		//FIXME remove this initial check once all fields added to entities
		if (mapping.attributeField == null) {
			return;
		}
		
		Class fieldClazz = mapping.attributeField.getType();
		if (fieldClazz.isEnum()) {
			validateEnumValue(result, lineRecord, fieldIndex, valueString, fieldClazz);
		}
	}

	private void validateEnumValue(ImportResult result, LineRecord lineRecord, int fieldIndex, String valueString, Class fieldClazz) {
		Object[] enumConstants = fieldClazz.getEnumConstants();
		for (int i = 0; i < enumConstants.length; i++) {
			if (StringUtils.equals(enumConstants[i].toString(), valueString)) {
				return;
			}
		}
		String enumOptions = enumConstantsToString(enumConstants);
		result.addError(lineRecord, fieldIndex, "Field value="+valueString+" not one of options: "+enumOptions);
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

	private List<LineRecord> split(String contents) throws IOException {
		List<LineRecord> records = new ArrayList<LineRecord>();
		List<String> lines = IOUtils.readLines(new StringReader(contents));
		boolean headerFound = false;
		for (String line : lines) {
			LineRecord record = new LineRecord(records.size() + 1, line);
			if (!headerFound && !record.isBlank) {
				record.header = true;
				headerFound = true;
			}
			records.add(record);
		}
		return records;
	}
	
	public static class ImportResult {
		boolean success = true;
		long readRecords = -1;
		long importedRecords = -1;
		List<String> errors = new ArrayList<String>();

		public boolean isSuccess() {
			return success;
		}
		public void addError(LineRecord lineRecord, int fieldIndex, String error) {
			addError("Line:"+lineRecord.lineNumber+" Col:"+fieldIndex+":"+MAPPINGS.get(fieldIndex).columnName+" "+error);
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
	
	public static class LineRecord {
		boolean header;
		long lineNumber = 0;
		String[] fields = {};
		String line = "";
		boolean isBlank = false;
		public LineRecord(long lineNumber, String line) {
			this.lineNumber = lineNumber;
			this.line = line;
			isBlank = StringUtils.isBlank(line);
			fields = line.split("\\^");
		}
	}
	
	public static class FieldMapping {
		String columnName;
		Class clazz;
		String attribute;
		Field attributeField;
		boolean required = false;
		public FieldMapping(String columnName, Class clazz, String attribute) {
			this.columnName = columnName;
			this.clazz = clazz;
			this.attribute = attribute;
			attributeField = ReflectionUtils.findField(clazz, attribute);
		}
		public FieldMapping(String columnName, Class clazz, String attribute, boolean required) {
			this(columnName, clazz, attribute);
			this.required = required;
		}
		
	}
}
