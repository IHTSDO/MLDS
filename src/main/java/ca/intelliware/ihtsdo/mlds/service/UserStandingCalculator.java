package ca.intelliware.ihtsdo.mlds.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;

@Service
public class UserStandingCalculator {

	@Resource
	protected CurrentSecurityContext currentSecurityContext;
	
	@Resource
	AffiliateRepository affiliateRepository;

	public boolean isAffiliateDeactivated() {
		return Objects.equal(getAffiliateStanding(), StandingState.DEACTIVATED);
	}

	public boolean isAffiliateDeregistered() {
		return Objects.equal(getAffiliateStanding(), StandingState.DEREGISTERED);
	}

	public StandingState getAffiliateStanding() {
		Affiliate affiliate = getAffiliate();
		if (affiliate != null) {
			return affiliate.getStandingState();
		} else {
			return null;
		}
	}
	
	private Affiliate getAffiliate() {
		List<Affiliate> affiliates = affiliateRepository.findByCreator(currentSecurityContext.getCurrentUserName());
		if (affiliates.size() > 0) {
			return affiliates.get(0);
		}
		return null;
	}

}
