package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserStandingCalculator {

	@Autowired
	protected CurrentSecurityContext currentSecurityContext;

	@Autowired
	AffiliateRepository affiliateRepository;

	public boolean isLoggedInUserAffiliateDeactivated() {
		return Objects.equal(getLoggedInUserAffiliateStanding(), StandingState.DEACTIVATED);
	}

	public boolean isLoggedInUserAffiliateDeregistered() {
		return Objects.equal(getLoggedInUserAffiliateStanding(), StandingState.DEREGISTERED);
	}

	public boolean isLoggedInUserAffiliatePendingInvoice() {
		return Objects.equal(getLoggedInUserAffiliateStanding(), StandingState.PENDING_INVOICE);
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
		List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(currentUserName);
		if (affiliates.size() > 0) {
			return affiliates.get(0);
		}
		return null;
	}

}
