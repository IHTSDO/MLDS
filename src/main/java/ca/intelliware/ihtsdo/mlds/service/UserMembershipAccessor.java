package ca.intelliware.ihtsdo.mlds.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

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
		List<Affiliate> affiliates = affiliateRepository.findByCreator(currentSecurityContext.getCurrentUserName());
		if (affiliates.size() >= 0) {
			return affiliates.get(0).getHomeMember();
		}
		return null;
	}

}
