package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AffiliateDetailsResetter {
	
	@PersistenceContext
    private EntityManager entityManager;

	public void detach(AffiliateDetails affiliateDetails) {
		entityManager.detach(affiliateDetails);
		
		affiliateDetails.setAffiliateDetailsId(null);
		
		entityManager.persist(affiliateDetails);
	}

}
