package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.StringWriter;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class BaseAffiliatesGenerator {

	@Resource
	protected AffiliatesMapper affiliatesMapper;

	public BaseAffiliatesGenerator() {
		super();
	}

	protected String generatePlaceHolderImportKey(long affiliateId) {
		return "AFFILIATE-"+affiliateId;
	}

	protected void writeHeader(StringWriter writer) {
		SeparatorWriter separatorWriter = new SeparatorWriter();
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			separatorWriter.append(writer);
			writer.append(fieldMapping.columnName);
		}
		appendLineEnding(writer);
	}

	protected void appendLineEnding(StringWriter writer) {
		writer.append(AffiliateFileFormat.LINE_ENDING);
	}

	protected void appendColumnSeparator(StringWriter writer) {
		writer.append(AffiliateFileFormat.COLUMN_SEPARATOR);
	}

	public class SeparatorWriter {
		boolean first = true;
		public void append(StringWriter writer) {
			if (!first) {
				appendColumnSeparator(writer);
			} else {
				first = false;
			}			
		}
	}
}