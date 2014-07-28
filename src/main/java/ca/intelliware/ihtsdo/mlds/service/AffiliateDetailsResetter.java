package ca.intelliware.ihtsdo.mlds.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;

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
