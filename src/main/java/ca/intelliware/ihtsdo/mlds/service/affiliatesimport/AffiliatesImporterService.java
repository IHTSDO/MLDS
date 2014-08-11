package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsagePeriod;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.UsageContext;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;

@Service
@Transactional
public class AffiliatesImporterService {

	@Resource ApplicationRepository applicationRepository;
	@Resource AffiliateRepository affiliateRepository;
	@Resource AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource CommercialUsageRepository commercialUsageRepository;
	
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
		List<LineRecord> lines = parseFile(contents);
		result.readRecords = lines.size();
		validateLines(lines, result);
		if (result.success) {
			createAffiliateRecords(lines, result);
		}
		
	}

	private void createAffiliateRecords(List<LineRecord> lines, ImportResult result) {
		result.importedRecords = 0;
		for (int i = 0; i < lines.size(); i++) {
			LineRecord lineRecord = lines.get(i);
			if (!lineRecord.header && !lineRecord.isBlank) {
				try {
					createApprovedAffiliate(lineRecord, result);
					result.importedRecords += 1;
				} catch (Exception e) {
					result.addError(lineRecord, "Failed to populate record: "+e);
				}
			}
		}
	}

	private void createApprovedAffiliate(LineRecord record, ImportResult result) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		
		//FIXME use services for much of this where possible... with a create and approve steps
		
		PrimaryApplication application = createApprovedPrimaryApplication(record);
		application = applicationRepository.save(application);
		
		CommercialUsage commercialUsage = createCommercialUsage(record, application.getType());
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		
		application.setCommercialUsage(commercialUsage);
		application = applicationRepository.save(application);

		AffiliateDetails affiliateDetails = createPopulateAffiliateDetails(record);
		affiliateDetails = affiliateDetailsRepository.save(affiliateDetails);
		
		Affiliate affiliate = createAffiliate(record, application, affiliateDetails);
		affiliate = affiliateRepository.save(affiliate);
		
		result.sourceMemberKey = affiliate.getHomeMember().getKey();
	}

	private Affiliate createAffiliate(LineRecord record, PrimaryApplication application, AffiliateDetails affiliateDetails) throws IllegalAccessException, InstantiationException {
		Affiliate affiliate = new Affiliate();
		
		affiliate.setAffiliateDetails(affiliateDetails);
		populateWithAll(affiliate, record, Affiliate.class);
		
		affiliate.addApplication(application);
		affiliate.setApplication(application);
		
		affiliate.setHomeMember(application.getMember());
		affiliate.setType(application.getType());
		affiliate.addCommercialUsage(application.getCommercialUsage());
		return affiliate;
	}

	private CommercialUsage createCommercialUsage(LineRecord record, AffiliateType affiliateType) throws IllegalAccessException, InstantiationException {
		CommercialUsage commercialUsage = new CommercialUsage();
		CommercialUsagePeriod usagePeriod = createCurrentCommercialUsagePeriod();
		commercialUsage.setStartDate(usagePeriod.getStartDate());
		commercialUsage.setEndDate(usagePeriod.getEndDate());
		UsageContext usageContext = new UsageContext();
		populateWithAll(usageContext, record, UsageContext.class);
		commercialUsage.setContext(usageContext);
		commercialUsage.setApprovalState(ApprovalState.APPROVED);
		commercialUsage.setType(affiliateType);
		return commercialUsage;
	}

	private AffiliateDetails createPopulateAffiliateDetails(LineRecord record) throws IllegalAccessException, InstantiationException {
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		populateWithAll(affiliateDetails, record, AffiliateDetails.class);
		return affiliateDetails;
	}

	private PrimaryApplication createApprovedPrimaryApplication(LineRecord record) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		PrimaryApplication application = (PrimaryApplication) Application.create(ApplicationType.PRIMARY);
		populateWithAll(application, record, Application.class);
		application.setApprovalState(ApprovalState.APPROVED);
		populateWithAll(application, record, PrimaryApplication.class);
		application.setAffiliateDetails(createPopulateAffiliateDetails(record));
		return application;
	}

	private void populateWithAll(Object rootObject, LineRecord record, Class<?> matchingClazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		record.setValuesOfMatchingClass(rootObject, matchingClazz, affiliatesMapper);
	}

	private void validateLines(List<LineRecord> lines, ImportResult result) {
		for (LineRecord lineRecord : lines) {
			lineRecord.validate(result, affiliatesMapper);
		}
	}

	private List<LineRecord> parseFile(String contents) throws IOException {
		List<LineRecord> records = new ArrayList<LineRecord>();
		List<String> lines = IOUtils.readLines(new StringReader(contents));
		boolean headerFound = false;
		for (String line : lines) {
			String[] fields = parseLine(line);
			LineRecord record = new LineRecord(records.size() + 1, fields, StringUtils.isBlank(line));
			if (!headerFound && !record.isBlank) {
				record.header = true;
				headerFound = true;
			}
			records.add(record);
		}
		return records;
	}

	private String[] parseLine(String line) {
		String[] elements = line.split("\\^", -1);
		for (int i = 0; i < elements.length; i++) {
			elements[i] = StringUtils.trimToEmpty(elements[i]);
		}
		return elements;
	}
	

	//FIXME move this code to a more appropriate service...
	private CommercialUsagePeriod createCurrentCommercialUsagePeriod() {
		CommercialUsagePeriod period = new CommercialUsagePeriod();
		if (LocalDate.now().getMonthOfYear() <= 6) {
			period.setStartDate(LocalDate.now().dayOfYear().withMinimumValue());
			period.setEndDate(LocalDate.now().monthOfYear().setCopy(6).dayOfMonth().withMaximumValue());
		} else {
			period.setStartDate(LocalDate.now().monthOfYear().setCopy(7).dayOfMonth().withMinimumValue());
			period.setEndDate(LocalDate.now().monthOfYear().setCopy(12).dayOfMonth().withMaximumValue());
		}
		return period;
	}
}
