package ca.intelliware.ihtsdo.mlds.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.base.Objects;

@Service
public class AffiliateMembershipCalculator {

	@Resource
	MemberRepository memberRepository;
	
	public Set<Member> acceptedMemberships(Affiliate affiliate) {
		Set<Member> members = new HashSet<Member>();
		for (Application application : affiliate.getApplications()) {
			if (Objects.equal(application.getApprovalState(), ApprovalState.APPROVED)) {
				members.add(actingMembership(application));
			}
		}
		return members;
	}

	private Member actingMembership(Application application) {
		if (Objects.equal(application.getApplicationType(), Application.ApplicationType.PRIMARY)) {
			return memberRepository.findOneByKey(Member.KEY_IHTSDO);
		} else {
			return application.getMember();
		}
	}
}
