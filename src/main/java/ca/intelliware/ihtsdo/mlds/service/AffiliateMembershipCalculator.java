package ca.intelliware.ihtsdo.mlds.service;

import java.util.Arrays;
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
import com.google.common.collect.Iterables;

@Service
public class AffiliateMembershipCalculator {

	@Resource
	MemberRepository memberRepository;
	
	public Set<Member> acceptedMemberships(Affiliate affiliate) {
		Set<Member> members = new HashSet<Member>();
		for (Application application : affiliate.getApplications()) {
			if (Objects.equal(application.getApprovalState(), ApprovalState.APPROVED)) {
				Iterables.addAll(members, actingMemberships(application));
			}
		}
		return members;
	}

	private Iterable<Member> actingMemberships(Application application) {
		if (Objects.equal(application.getApplicationType(), Application.ApplicationType.PRIMARY)) {
			return Arrays.asList(memberRepository.findOneByKey(Member.KEY_IHTSDO),application.getMember());
		} else {
			return Arrays.asList(application.getMember());
		}
	}
}
