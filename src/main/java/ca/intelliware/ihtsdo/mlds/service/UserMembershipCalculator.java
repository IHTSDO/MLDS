package ca.intelliware.ihtsdo.mlds.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
	
	public Iterable<User> acceptedUsers(Member member) {
		Set<User> users = new HashSet<User>();
		for (Affiliate affiliate : affiliateRepository.findByStandingStateIn(Arrays.asList(StandingState.IN_GOOD_STANDING, StandingState.DEACTIVATION_PENDING))) {
			Set<Member> acceptedMemberships = affiliateMembershipCalculator.acceptedMemberships(affiliate);
			if (acceptedMemberships.contains(member)) {
				User user = userRepository.findByLoginIgnoreCase(affiliate.getCreator());
				if (user != null) {
					users.add(user);
				}	
			}
		}
//		for (Affiliate affiliate : affiliateRepository.findActiveByMember(member)) {
//			User user = userRepository.findByLoginIgnoreCase(affiliate.getCreator());
//			if (user != null) {
//				users.add(user);
//			}
//		}
		return users;
	}
}
