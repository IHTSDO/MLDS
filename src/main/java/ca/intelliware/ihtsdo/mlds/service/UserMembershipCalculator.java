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
	
	public Iterable<User> approvedReleaseUsers(Member member) {
		return approvedMembership(member, releaseStandingStates());
	}

	public Iterable<User> approvedActiveUsers(Member member) {
		return approvedMembership(member, activeStandingStates());
	}

	private Iterable<User> approvedMembership(Member member, List<StandingState> releaseStandingState) {
		Set<User> users = new HashSet<User>();
		for (Affiliate affiliate : membership(member, releaseStandingState)) {
			User user = userRepository.findByLoginIgnoreCase(affiliate.getCreator());
			if (user != null) {
				users.add(user);
			}	
		}
		return users;
	}

	private Iterable<Affiliate> membership(Member member, List<StandingState> releaseStandingState) {
		if (Objects.equal(member.getKey(), Member.KEY_IHTSDO)) {
			return affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(releaseStandingState);
		} else {
			return affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(releaseStandingState,  member);
		}
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
