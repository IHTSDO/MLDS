package ca.intelliware.ihtsdo.mlds.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import liquibase.util.csv.CSVReader;

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

	public void importFromCSV(String contents) throws IOException {
		List<LineRecord> lines = split(contents);
		validateLines(lines);
		//TODO Create affiliate records
	}

	private void validateLines(List<LineRecord> lines) {
		StringWriter errors = new StringWriter();
		for (LineRecord lineRecord : lines) {
			if (lineRecord.isBlank) {
				continue;
			}
		}
		
	}

	private List<LineRecord> split(String contents) throws IOException {
		List<LineRecord> records = new ArrayList<LineRecord>();
		long lineNumber = 1;
		List<String> lines = IOUtils.readLines(new StringReader(contents));
		for (String line : lines) {
			records.add(new LineRecord(lineNumber, line));
		}
		return records;
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
