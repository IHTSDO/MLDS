package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

@Service
@Transactional
public class AffiliateDeleter {

	@Resource AffiliateRepository affiliateRepository;
	@Resource AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource ApplicationRepository applicationRepository;
	@Resource UserRepository userRepository;
	@Resource CommercialUsageRepository commercialUsageRepository;
	@Resource CommercialUsageCountryRepository commercialUsageCountryRepository;
	@Resource CommercialUsageEntryRepository commercialUsageEntryRepository;

	public void deleteAffiliate(Affiliate affiliate) {
		deleteUser(affiliate);
		deleteCommercialUsage(affiliate);
		deleteApplications(affiliate);
		deleteAffiliateDetails(affiliate);
		affiliateRepository.delete(affiliate);
	}

	private void deleteAffiliateDetails(Affiliate affiliate) {
		if (affiliate.getAffiliateDetails() != null) {
			affiliateDetailsRepository.delete(affiliate.getAffiliateDetails());
		}
		
	}

	private void deleteApplications(Affiliate affiliate) {
		for (Application application : affiliate.getApplications()) {
			if (application.getAffiliateDetails() != null) {
				affiliateDetailsRepository.delete(application.getAffiliateDetails());
			}
			applicationRepository.delete(application);
		}
	}

	private void deleteCommercialUsage(Affiliate affiliate) {
		for (CommercialUsage commercialUsage : affiliate.getCommercialUsages()) {
			commercialUsageCountryRepository.delete(commercialUsage.getCountries());
			commercialUsageEntryRepository.delete(commercialUsage.getEntries());
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
