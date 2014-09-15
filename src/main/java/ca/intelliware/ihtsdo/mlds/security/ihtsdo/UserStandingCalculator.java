package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

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

	public boolean isLoggedInUserAffiliateDeactivated() {
		return Objects.equal(getLoggedInUserAffiliateStanding(), StandingState.DEACTIVATED);
	}

	public boolean isLoggedInUserAffiliateDeregistered() {
		return Objects.equal(getLoggedInUserAffiliateStanding(), StandingState.DEREGISTERED);
	}

	public StandingState getLoggedInUserAffiliateStanding() {
		return getUserAffiliateStanding(currentSecurityContext.getCurrentUserName());
	}

	public StandingState getUserAffiliateStanding(String currentUserName) {
		Affiliate affiliate = getAffiliate(currentUserName);
		if (affiliate != null) {
			return affiliate.getStandingState();
		} else {
			return null;
		}
	}
	
	private Affiliate getAffiliate(String currentUserName) {
		List<Affiliate> affiliates = affiliateRepository.findByCreator(currentUserName);
		if (affiliates.size() > 0) {
			return affiliates.get(0);
		}
		return null;
	}

}
