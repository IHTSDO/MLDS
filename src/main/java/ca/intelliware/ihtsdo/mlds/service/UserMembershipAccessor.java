package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.List;

/**
 * Get the primary member for the current user.
 */
@Service
public class UserMembershipAccessor {

	protected CurrentSecurityContext currentSecurityContext;

	private final AffiliateRepository affiliateRepository;

    private final MemberRepository memberRepository;

	AffiliateMembershipCalculator affiliateMembershipCalculator;

    public UserMembershipAccessor(CurrentSecurityContext currentSecurityContext, AffiliateRepository affiliateRepository, MemberRepository memberRepository, AffiliateMembershipCalculator affiliateMembershipCalculator) {
        this.currentSecurityContext = currentSecurityContext;
        this.affiliateRepository = affiliateRepository;
        this.memberRepository = memberRepository;
        this.affiliateMembershipCalculator = affiliateMembershipCalculator;
    }

    public Member getMemberAssociatedWithUser() {
		if (currentSecurityContext.isMemberOrStaffOrAdmin()) {
			return getStaffOrAdminMembership();
		} else if (currentSecurityContext.isUser()) {
			return getAffiliateHomeMember();
		} else {
			return null;
		}
	}

	private Member getStaffOrAdminMembership() {
		String memberKey = currentSecurityContext.getStaffMemberKey();
		return memberRepository.findOneByKey(memberKey);
	}

	private Member getAffiliateHomeMember() {
		Affiliate affiliate = getAffiliate();
		if (affiliate != null) {
			return affiliate.getHomeMember();
		} else {
			return null;
		}
	}

	public Affiliate getAffiliate() {
		List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(currentSecurityContext.getCurrentUserName());
		if (!affiliates.isEmpty()) {
			return affiliates.get(0);
		}
		return null;
	}

	/**
	 * Collection of accepted membership applications. Note that this
	 * might not include the "home member" if the extension application
	 * for a non-ihtsdo member has not been accepted yet.
	 */
	public Collection<Member> getAcceptedAffiliateMembershipsOfUser() {
		if (currentSecurityContext.isUser()) {
			return getAffiliateMemberships();
		} else {
			return Lists.newArrayList();
		}
	}

	public boolean isAffiliateMemberApplicationAccepted(Member member) {
		return getAcceptedAffiliateMembershipsOfUser().contains(member);
	}

	private Collection<Member> getAffiliateMemberships() {
		Affiliate affiliate = getAffiliate();
		if (affiliate != null) {
			return affiliateMembershipCalculator.acceptedMemberships(affiliate);
		} else {
			return Lists.newArrayList();
		}
	}

}
