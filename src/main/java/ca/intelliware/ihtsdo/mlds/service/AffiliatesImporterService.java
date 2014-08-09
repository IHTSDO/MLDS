package ca.intelliware.ihtsdo.mlds.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

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
			if (lineRecord.fields.length != FIELDS.length) {
				result.addError(lineRecord, "Incorrect number of fields: found="+ lineRecord.fields.length+" required="+FIELDS.length);
			}
		}
		
	}

	private List<LineRecord> split(String contents) throws IOException {
		List<LineRecord> records = new ArrayList<LineRecord>();
		List<String> lines = IOUtils.readLines(new StringReader(contents));
		for (String line : lines) {
			records.add(new LineRecord(records.size() + 1, line));
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
}
