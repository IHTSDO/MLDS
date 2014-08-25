package ca.intelliware.ihtsdo.mlds.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.collect.Lists;

/**
 * Get the primary member for the current user.
 */
@Service
public class UserMembershipAccessor {

	@Resource
	protected CurrentSecurityContext currentSecurityContext;
	
	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	MemberRepository memberRepository;
	
	@Resource
	AffiliateMembershipCalculator affiliateMembershipCalculator;

	public Member getMemberAssociatedWithUser() {
		if (currentSecurityContext.isStaffOrAdmin()) {
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

	private Affiliate getAffiliate() {
		List<Affiliate> affiliates = affiliateRepository.findByCreator(currentSecurityContext.getCurrentUserName());
		if (affiliates.size() > 0) {
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
