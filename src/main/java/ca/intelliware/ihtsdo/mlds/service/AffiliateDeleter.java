package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

@Service
public class AffiliateDeleter {

	@Resource AffiliateRepository affiliateRepository;
	@Resource ApplicationRepository applicationRepository;
	@Resource UserRepository userRepository;
	@Resource CommercialUsageRepository commercialUsageRepository;

	public void deleteAffiliate(Affiliate affiliate) {
		deleteUser(affiliate);
		commercialUsageRepository.delete(affiliate.getCommercialUsages());
		applicationRepository.delete(affiliate.getApplications());
		affiliateRepository.delete(affiliate);
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
