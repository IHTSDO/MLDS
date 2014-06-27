package ca.intelliware.ihtsdo.mlds.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;

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
    	commercialUsage.setApprovalState(ApprovalState.NOT_SUBMITTED);
	}
	
	public void detach(CommercialUsage commercialUsage) {
		for (CommercialUsageEntry entry : commercialUsage.getEntries()) {
			em.detach(entry);
			entry.setCommercialUsageEntryId(null);
		}
		
		em.detach(commercialUsage);
		commercialUsage.setCommercialUsageId(null);
	}
}
