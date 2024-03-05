package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AffiliatesImporterService {

	@Autowired
	ApplicationRepository applicationRepository;
	@Autowired AffiliateRepository affiliateRepository;
	@Autowired AffiliateDetailsRepository affiliateDetailsRepository;
	@Autowired CommercialUsageRepository commercialUsageRepository;
	@Autowired AffiliateAuditEvents affiliateAuditEvents;


	@Autowired AffiliatesMapper affiliatesMapper;

	@PersistenceContext
    private EntityManager entityManager;

	public ImportResult importFromCSV(String contents) throws IOException {
		ImportResult result = new ImportResult();
		long start = System.currentTimeMillis();
		try {
			importContents(contents, result);
		} catch (Exception e) {
			result.addException(e);
		}
		result.durationMillis = System.currentTimeMillis() - start;
		return result;
	}

	private void importContents(String contents, ImportResult result) throws IOException {
		List<LineRecord> lines = parseFile(contents);
		result.readRows = lines.size();
		validateLines(lines, result);
		if (result.success) {
			processAffiliateRecords(lines, result);
		}

	}

	private void processAffiliateRecords(List<LineRecord> lines, ImportResult result) {
		result.importedRecords = 0;
		result.setNewRecords(0);
		result.updatedRecords = 0;
		for (int i = 0; i < lines.size(); i++) {
			LineRecord lineRecord = lines.get(i);
			if (!lineRecord.header && !lineRecord.isBlank) {
				try {
					processLineRecord(lineRecord, result);
					result.importedRecords += 1;
				} catch (Exception e) {
					result.addError(lineRecord, "Failed to populate record: "+e);
				}
			}
		}
	}

	void processLineRecord(LineRecord record, ImportResult result) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Affiliate affiliate = findExistingAffiliateForUpdate(record);
		Application application;
		if (affiliate == null) {
			application = createApprovedAffiliate(record);
			result.setNewRecords(result.getNewRecords() + 1);
		} else {
			application = updateAffiliate(affiliate,record);
			result.updatedRecords += 1;
		}
		result.setSourceMemberKey(application.getMember().getKey());

		clearSession();
	}

	/** For bulk performance clear pending changes from hibernate session */
	private void clearSession() {
		entityManager.flush();
		entityManager.clear();
	}

	private Affiliate findExistingAffiliateForUpdate(LineRecord record) throws IllegalAccessException, InstantiationException {
		Affiliate tmpAffiliate = new Affiliate();
		PrimaryApplication tmpApplication = new PrimaryApplication();
		populateWithAll(tmpAffiliate, record, Affiliate.class);
		populateWithAll(tmpApplication, record, Application.class);
		Affiliate affiliate = affiliateRepository.findByImportKeyAndHomeMember(tmpAffiliate.getImportKey(),tmpApplication.getMember());
		return affiliate;
	}

	private Application updateAffiliate(Affiliate affiliate, LineRecord record) throws IllegalAccessException, InstantiationException {
		populateWithAll(affiliate, record, Affiliate.class);
		populateWithAll(affiliate.getAffiliateDetails(), record, AffiliateDetails.class);

		PrimaryApplication importApplication = createApprovedPrimaryApplication(record, ApplicationType.IMPORT);
		applicationRepository.save(importApplication);
		affiliate.addApplication(importApplication);

		affiliateAuditEvents.logUpdateByImport(affiliate);

		return importApplication;
	}

	private Application createApprovedAffiliate(LineRecord record) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		//FIXME use services for much of this where possible... with a create and approve steps

		PrimaryApplication application = createApprovedPrimaryApplication(record, ApplicationType.PRIMARY);
		application = applicationRepository.save(application);

		CommercialUsage commercialUsage = createCommercialUsage(record, application.getType());
		commercialUsage = commercialUsageRepository.save(commercialUsage);

		application.setCommercialUsage(commercialUsage);
		application = applicationRepository.save(application);

		AffiliateDetails affiliateDetails = createPopulateAffiliateDetails(record);
		affiliateDetails = affiliateDetailsRepository.save(affiliateDetails);

		Affiliate affiliate = createAffiliate(record, application, affiliateDetails);
		affiliate = affiliateRepository.save(affiliate);

		affiliateAuditEvents.logCreationByImport(affiliate);

		return application;
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
		commercialUsage.setState(UsageReportState.NOT_SUBMITTED);
		commercialUsage.setType(affiliateType);
		return commercialUsage;
	}

	private AffiliateDetails createPopulateAffiliateDetails(LineRecord record) throws IllegalAccessException, InstantiationException {
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		populateWithAll(affiliateDetails, record, AffiliateDetails.class);
		return affiliateDetails;
	}

	/**
	 * Create an application from the record
	 * @param record
	 * @param applicationType assumed to be one of Primary or Import (since Import extends Primary)
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private PrimaryApplication createApprovedPrimaryApplication(LineRecord record, ApplicationType applicationType) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		PrimaryApplication application = (PrimaryApplication) Application.create(applicationType);
		populateWithAll(application, record, Application.class);
		application.setCompletedAt(Instant.now());
		application.setSubmittedAt(Instant.now());
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
			List<String> fields = parseLine(line);
			LineRecord record = new LineRecord(records.size() + 1, fields, StringUtils.isBlank(line));
			if (!headerFound && !record.isBlank) {
				record.header = true;
				headerFound = true;
			}
			records.add(record);
		}
		return records;
	}

	private List<String> parseLine(String line) {
		String[] elements = line.split(AffiliateFileFormat.COLUMN_SEPARATOR_REGEX, -1);
		for (int i = 0; i < elements.length; i++) {
			elements[i] = StringUtils.trimToEmpty(elements[i]);
		}
		return Arrays.asList(elements);
	}


	//FIXME move this code to a more appropriate service...
    private CommercialUsagePeriod createCurrentCommercialUsagePeriod() {
        CommercialUsagePeriod period = new CommercialUsagePeriod();
        LocalDate currentDate = LocalDate.now();

        if (currentDate.getMonthValue() <= 6) {
            period.setStartDate(LocalDate.of(currentDate.getYear(), 1, 1));
            period.setEndDate(LocalDate.of(currentDate.getYear(), 6, 30));
        } else {
            period.setStartDate(LocalDate.of(currentDate.getYear(), 7, 1));
            period.setEndDate(LocalDate.of(currentDate.getYear(), 12, 31));
        }

        return period;
    }
}
