package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import org.apache.commons.lang.StringUtils;

public class LineRecord {
	boolean header;
	long lineNumber = 0;
	String[] fields = {};
	String line = "";
	boolean isBlank = false;
	public LineRecord(long lineNumber, String line) {
		this.lineNumber = lineNumber;
		this.line = line;
		isBlank = StringUtils.isBlank(line);
		fields = line.split("\\^", -1);
	}
}