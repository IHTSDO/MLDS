package ca.intelliware.ihtsdo.mlds.service;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;

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
		Set<CommercialUsageEntry> usageEntryCollection = Sets.newHashSet();
		for (CommercialUsageEntry entry : commercialUsage.getEntries()) {
			em.detach(entry);
			entry.setCommercialUsageEntryId(null);
			usageEntryCollection.add(entry);
		}
		commercialUsage.setUsage(usageEntryCollection);
		
		Set<CommercialUsageCountry> countryCollection = Sets.newHashSet();
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
