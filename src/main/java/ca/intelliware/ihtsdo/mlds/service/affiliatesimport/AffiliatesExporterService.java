package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.StringWriter;

import javax.annotation.Resource;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportSpec.ColumnSpec;

import com.google.common.base.Objects;

@Service
public class AffiliatesExporterService extends BaseAffiliatesGenerator {

	@Resource AffiliateRepository affiliateRepository;
	@Resource AffiliatesImportGenerator affiliatesImportGenerator;
	
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
				stringValue = generatePlaceHolderImportKey(affiliate.getAffiliateId());
			}
			writer.append(stringValue);
		}
		appendLineEnding(writer);
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

	public AffiliatesImportSpec exportSpec() {
		AffiliatesImportSpec spec = new AffiliatesImportSpec();
		spec.example = affiliatesImportGenerator.generateFile(2);
		for (FieldMapping fieldMapping : affiliatesMapper.getMappings()) {
			ColumnSpec columnSpec = new AffiliatesImportSpec.ColumnSpec();
			columnSpec.attributeClass = ClassUtils.getShortClassName(fieldMapping.accessor.getAttributeClass());
			columnSpec.columnName = fieldMapping.columnName;
			columnSpec.required = fieldMapping.required;
			columnSpec.example = getExampleValue(fieldMapping);
			columnSpec.options = fieldMapping.getOptions();
			spec.getColumns().add(columnSpec);
		}
		return spec;
	}

	private String getExampleValue(FieldMapping fieldMapping) {
		return affiliatesImportGenerator.generateValue(fieldMapping, 0, new AffiliatesImportGenerator.GeneratorContext());
	}
}
