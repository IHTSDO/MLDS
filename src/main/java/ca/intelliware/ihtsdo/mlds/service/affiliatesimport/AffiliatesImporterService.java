package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;

@Service
@Transactional
public class AffiliatesImporterService {

	@Resource ApplicationRepository applicationRepository;
	@Resource AffiliateRepository affiliateRepository;
	
	@Resource AffiliatesMapper affiliatesMapper;
	
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
			createAffiliateRecords(lines, result);
		}
		
	}

	private void createAffiliateRecords(List<LineRecord> lines, ImportResult result) {
		for (int i = 0; i < lines.size(); i++) {
			LineRecord lineRecord = lines.get(i);
			if (!lineRecord.header && !lineRecord.isBlank) {
				try {
					createAffiliateRecords(lineRecord, result);
				} catch (Exception e) {
					result.addError(lineRecord, "Failed to populate record: "+e);
				}
			}
		}
		
	}

	private void createAffiliateRecords(LineRecord record, ImportResult result) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		
		//FIXME use services for much of this where possible...
		
		PrimaryApplication application = (PrimaryApplication) Application.create(ApplicationType.PRIMARY);
		populateWithAll(application, record, Application.class);
		application.setApprovalState(ApprovalState.APPROVED);
		populateWithAll(application, record, PrimaryApplication.class);
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		application.setAffiliateDetails(affiliateDetails);
		populateWithAll(affiliateDetails, record, AffiliateDetails.class);
		applicationRepository.save(application);
		
		Affiliate affiliate = new Affiliate();
		affiliate.setAffiliateDetails(affiliateDetails);
		affiliate.addApplication(application);
		affiliate.setApplication(application);
		populateWithAll(affiliate, record, Affiliate.class);
		affiliate.setHomeMember(application.getMember());
		affiliate.setType(application.getType());
		
		affiliateRepository.save(affiliate);
	}

	private void populateWithAll(Object object, LineRecord record, Class clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		for (FieldMapping mapping : affiliatesMapper.getMappings()) {
			if (clazz.equals(mapping.rootClazz)) {
				populateWith(object, record, mapping);
			}
		}
	}

	private void populateWith(Object rootObject, LineRecord record, FieldMapping mapping) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String sourceValue = record.fields[mapping.columnIndex];
		Object value = mapping.valueConverter.toObject(sourceValue);
		mapping.accessor.setValue(rootObject, value);
	}

	private void validateLines(List<LineRecord> lines, ImportResult result) {
		for (LineRecord lineRecord : lines) {
			if (lineRecord.isBlank) {
				continue;
			}
			if (lineRecord.fields.length != affiliatesMapper.getMappings().size()) {
				result.addError(lineRecord, "Incorrect number of fields: found="+ lineRecord.fields.length+" required="+affiliatesMapper.getMappings().size());
			} else if (lineRecord.header) {
				for (FieldMapping mapping : affiliatesMapper.getMappings()) {
					String valueString = lineRecord.fields[mapping.columnIndex];
					if (!StringUtils.equalsIgnoreCase(mapping.columnName, valueString)) {
						result.addError(lineRecord, mapping, "Header value="+valueString+" does not match title="+mapping.columnName);
					}
				}
			} else {
				for (FieldMapping mapping : affiliatesMapper.getMappings()) {
					String valueString = lineRecord.fields[mapping.columnIndex];
					if (mapping.required && StringUtils.isBlank(valueString)) {
						result.addError(lineRecord, mapping, "Missing required field");
					} else {
						validateFieldValue(result, lineRecord, mapping, valueString);
					}
				}
			}
		}
		
	}

	private void validateFieldValue(ImportResult result, LineRecord lineRecord, FieldMapping mapping, String valueString) {
		if (StringUtils.isBlank(valueString)) {
			return;
		}
		
		String sourceValue = lineRecord.fields[mapping.columnIndex];
		mapping.valueConverter.validate(sourceValue, lineRecord, mapping, result);
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
	
	
}
