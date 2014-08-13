package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.StringWriter;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;

import com.google.common.base.Objects;

@Service
public class AffiliatesExporterService {

	@Resource AffiliateRepository affiliateRepository;
	
	@Resource AffiliatesMapper affiliatesMapper;

	public String exportToCSV() {
		StringWriter writer = new StringWriter();
		writeHeader(writer);
		writeAffiliates(writer);
		return writer.toString();
	}

	private void writeAffiliates(StringWriter writer) {
		for (Affiliate affiliate : affiliateRepository.findAll()) {
			writeAffiliate(writer, affiliate);
		}
	}

	private void writeAffiliate(StringWriter writer, Affiliate affiliate) {
		SeparatorWriter separatorWriter = new SeparatorWriter();
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			separatorWriter.append(writer);
			Object rootObject = findRootObject(affiliate, fieldMapping);
			String stringValue = toStringValue(fieldMapping, rootObject);
			// Must have a value to allow re-import for affiliate added by users in our system.
			if (Objects.equal(fieldMapping.columnName, "importKey") && StringUtils.isBlank(stringValue)) {
				stringValue = generatePlaceHolderImportKey(affiliate);
			}
			writer.append(stringValue);
		}
		appendLineEnding(writer);
	}

	private String generatePlaceHolderImportKey(Affiliate affiliate) {
		return "AFFILIATE-"+affiliate.getAffiliateId();
	}

	private String toStringValue(FieldMapping fieldMapping, Object rootObject) {
		Object value = fieldMapping.accessor.getValue(rootObject);
		return fieldMapping.valueConverter.toString(value);
	}

	private Object findRootObject(Affiliate affiliate, FieldMapping fieldMapping) {
		if (fieldMapping.rootClazz.equals(Application.class) || fieldMapping.rootClazz.equals(PrimaryApplication.class)) {
			return affiliate.getApplication();
		} else if (fieldMapping.rootClazz.equals(Affiliate.class)) {
			return affiliate;
		} else if (fieldMapping.rootClazz.equals(AffiliateDetails.class)) {
			return affiliate.getAffiliateDetails();
		}
		return new IllegalStateException("Unsupported root class field mapping");
	}

	private void writeHeader(StringWriter writer) {
		SeparatorWriter separatorWriter = new SeparatorWriter();
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			separatorWriter.append(writer);
			writer.append(fieldMapping.columnName);
		}
		appendLineEnding(writer);
	}

	private void appendLineEnding(StringWriter writer) {
		writer.append(AffiliateFileFormat.LINE_ENDING);
	}

	private void appendColumnSeparator(StringWriter writer) {
		writer.append(AffiliateFileFormat.COLUMN_SEPARATOR);
	}
	
	class SeparatorWriter {
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
