package ca.intelliware.ihtsdo.mlds.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserMembershipCalculationTest {

	@Mock AffiliateRepository affiliateRepository;
	@Mock UserRepository userRepository;
	@Mock AffiliateMembershipCalculator affiliateMembershipCalculator;
	
	private UserMembershipCalculator userMembershipCalculator;
	private ArrayList<Affiliate> matchingAffiliates;
	private Member sweden;
	private Member ihtsdo;
	
	@Before
	public void setup() {
		userMembershipCalculator = new UserMembershipCalculator();
		
		userMembershipCalculator.affiliateMembershipCalculator = affiliateMembershipCalculator;
		userMembershipCalculator.affiliateRepository = affiliateRepository;
		userMembershipCalculator.userRepository = userRepository;
		
		sweden = new Member("SE", 1L);
		ihtsdo = new Member("IHTSDO", 2L);
		
		matchingAffiliates = new ArrayList<Affiliate>();
	}
	
	@Test
	public void approvedReleaseUsersShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(sweden));
		User user0 = withUser(affiliate0);

		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);

		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsers(sweden);
		
		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	@Test
	public void approvedReleaseUsersShouldReturnIgnoreAffiliatesWithoutRelatedUser() {
		withAffiliate(10L, Sets.newHashSet(sweden));
		// do not specify user related to affiliate
		
		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);
		
		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsers(sweden);
		
		Assert.assertThat(result.iterator().hasNext(), CoreMatchers.is(false));
	}

	@Test
	public void approvedReleaseUsersForIhtsdoMemberShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(ihtsdo));
		User user0 = withUser(affiliate0);

		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(Matchers.anyListOf(StandingState.class))).thenReturn(matchingAffiliates);

		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsers(ihtsdo);
		
		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	@Test
	public void approvedActiveUsersShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(sweden));
		User user0 = withUser(affiliate0);

		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);

		// test
		Iterable<User> result = userMembershipCalculator.approvedActiveUsers(sweden);
		
		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	private User withUser(Affiliate affiliate0) {
		User user = new User();
		user.setUserId(affiliate0.getAffiliateId());
		Mockito.when(userRepository.findByLoginIgnoreCase(affiliate0.getCreator())).thenReturn(user);
		return user;
		
	}

	private Affiliate withAffiliate(long id, Set<Member> members) {
		Affiliate affiliate = new Affiliate(id);
		affiliate.setCreator("affiliate"+id+"@test.com");
		matchingAffiliates.add(affiliate);
		Mockito.when(affiliateMembershipCalculator.acceptedMemberships(affiliate)).thenReturn(members);
		return affiliate;
	}
	
}
