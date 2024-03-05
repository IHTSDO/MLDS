package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AffiliateDeleter {

	@Autowired
	AffiliateRepository affiliateRepository;
	@Autowired AffiliateDetailsRepository affiliateDetailsRepository;
	@Autowired ApplicationRepository applicationRepository;
	@Autowired UserRepository userRepository;
	@Autowired CommercialUsageRepository commercialUsageRepository;
	@Autowired CommercialUsageCountryRepository commercialUsageCountryRepository;
	@Autowired CommercialUsageEntryRepository commercialUsageEntryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public void deleteAffiliate(Affiliate affiliate) {
		deleteUser(affiliate);
		deleteCommercialUsage(affiliate);
		deleteApplications(affiliate);
		deleteAffiliateDetails(affiliate);
		affiliateRepository.delete(affiliate);
	}

	private void deleteAffiliateDetails(Affiliate affiliate) {
		deleteAffiliateDetails(affiliate.getAffiliateDetails());
	}

	private void deleteAffiliateDetails(AffiliateDetails affiliateDetails) {
		if (affiliateDetails != null) {
			if (entityManager.contains(affiliateDetails)) {
				affiliateDetailsRepository.delete(affiliateDetails);
			}
		}
	}

	private void deleteApplications(Affiliate affiliate) {
		for (Application application : affiliate.getApplications()) {
			deleteAffiliateDetails(application.getAffiliateDetails());
			applicationRepository.delete(application);
		}
	}

	private void deleteCommercialUsage(Affiliate affiliate) {
		for (CommercialUsage commercialUsage : affiliate.getCommercialUsages()) {
			commercialUsageCountryRepository.deleteAll(commercialUsage.getCountries());
			commercialUsageEntryRepository.deleteAll(commercialUsage.getEntries());
			commercialUsageRepository.delete(commercialUsage);
		}
	}

	private void deleteUser(Affiliate affiliate) {
		if (StringUtils.isNotBlank(affiliate.getCreator())) {
			User user = userRepository.getUserByEmailIgnoreCase(affiliate.getCreator());
			if (user != null) {
				userRepository.delete(user);
			}
		}
	}

}
