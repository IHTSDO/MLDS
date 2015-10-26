package ca.intelliware.ihtsdo.mlds.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

@Service
public class UserMembershipCalculator {

	@Resource AffiliateRepository affiliateRepository;
	@Resource UserRepository userRepository;
	@Resource AffiliateMembershipCalculator affiliateMembershipCalculator;
	
	public Iterable<User> approvedReleaseUsersWithAnyMembership(Member member) {
		return findMatchingUsers(anyApprovedMembership(member, releaseStandingStates()));
	}

	public Iterable<User> approvedActiveUsersWithHomeMembership(Member member) {
		return findMatchingUsers(affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(activeStandingStates(), member));
	}

	public Iterable<User> approvedActiveUsers() {
		return findMatchingUsers(affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(activeStandingStates()));
	}

	private Iterable<Affiliate> anyApprovedMembership(Member member, List<StandingState> standingStates) {
		if (Objects.equal(member.getKey(), Member.KEY_IHTSDO)) {
			return affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(standingStates);
		} else {
			return affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(standingStates,  member);
		}
	}

	private Iterable<User> findMatchingUsers(Iterable<Affiliate> affiliates) {
		Set<User> users = new HashSet<User>();
		for (Affiliate affiliate : affiliates) {
			User user = userRepository.findByLoginIgnoreCase(affiliate.getCreator());
			if (user != null) {
				users.add(user);
			}	
		}
		return users;
	}


	
	private List<StandingState> releaseStandingStates() {
		return Arrays.asList(
				StandingState.IN_GOOD_STANDING, 
				StandingState.DEACTIVATION_PENDING
				);
	}
	
	private List<StandingState> activeStandingStates() {
		return Arrays.asList(
				StandingState.IN_GOOD_STANDING, 
				StandingState.PENDING_INVOICE,
				StandingState.INVOICE_SENT,
				StandingState.DEACTIVATION_PENDING
				);
	}
}
