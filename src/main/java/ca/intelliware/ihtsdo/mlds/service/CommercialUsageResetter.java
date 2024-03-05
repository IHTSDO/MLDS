package ca.intelliware.ihtsdo.mlds.service;


import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Reset a persisted CommercialUsageReport and its CommercialUsageEntries
 * back to the detached state and set initial values suitable for saving
 * as fresh entities.
 */

@Service
@Transactional
public class CommercialUsageResetter {

	@PersistenceContext
    private EntityManager em;

	public void detachAndReset(CommercialUsage commercialUsage, LocalDate startDate, LocalDate endDate) {
		detach(commercialUsage);
		
    	commercialUsage.setStartDate(startDate);
    	commercialUsage.setEndDate(endDate);
		commercialUsage.setState(UsageReportState.NOT_SUBMITTED);
    	commercialUsage.setSubmitted(null);
    	commercialUsage.setEffectiveTo(null);
	}
	
	public void detach(CommercialUsage commercialUsage) {

		//INFRA-2075 Create new collections to hold these objects and reassign
		Set<CommercialUsageEntry> usageEntryCollection = new HashSet<>();
		for (CommercialUsageEntry entry : commercialUsage.getEntries()) {
			em.detach(entry);
			entry.setCommercialUsageEntryId(null);
			usageEntryCollection.add(entry);
		}
		commercialUsage.setUsage(usageEntryCollection);
		
		Set<CommercialUsageCountry> countryCollection = new HashSet<>();
		for (CommercialUsageCountry country : commercialUsage.getCountries()) {
			em.detach(country);
			country.setCommercialUsageCountId(null);
			countryCollection.add(country);
		}
		commercialUsage.setCountries(countryCollection);

		em.detach(commercialUsage);
		commercialUsage.setCommercialUsageId(null);
	}
}
