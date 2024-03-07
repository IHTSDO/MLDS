package ca.intelliware.ihtsdo.mlds.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class UserMembershipCalculationTest {

	@Mock AffiliateRepository affiliateRepository;
	@Mock UserRepository userRepository;
	@Mock AffiliateMembershipCalculator affiliateMembershipCalculator;

	private UserMembershipCalculator userMembershipCalculator;
	private ArrayList<Affiliate> matchingAffiliates;
	private ArrayList<User> matchingUsers;
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
		matchingUsers = new ArrayList<User>();
	}

	@Test
	public void approvedReleaseUsersShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(sweden));
		User user0 = withUser(affiliate0);

//		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);
//		Mockito.when(userRepository.findByLoginIgnoreCaseIn(Matchers.anyListOf(String.class))).thenReturn(matchingUsers);
        Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(anyList(), eq(sweden)))
            .thenReturn(matchingAffiliates);
        Mockito.when(userRepository.findByLoginIgnoreCaseIn(anyList()))
            .thenReturn(matchingUsers);
		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsersWithAnyMembership(sweden);

		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	@Test
	public void approvedReleaseUsersShouldReturnIgnoreAffiliatesWithoutRelatedUser() {
		withAffiliate(10L, Sets.newHashSet(sweden));
		// do not specify user related to affiliate

//		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);
//		Mockito.when(userRepository.findByLoginIgnoreCaseIn(Matchers.anyListOf(String.class))).thenReturn(matchingUsers);
//
		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsersWithAnyMembership(sweden);

		Assert.assertThat(result.iterator().hasNext(), CoreMatchers.is(false));
	}

	@Test
	public void approvedReleaseUsersForIhtsdoMemberShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(ihtsdo));
		User user0 = withUser(affiliate0);

//		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(Matchers.anyListOf(StandingState.class))).thenReturn(matchingAffiliates);
//		Mockito.when(userRepository.findByLoginIgnoreCaseIn(Matchers.anyListOf(String.class))).thenReturn(matchingUsers);
//
        Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(anyList()))
            .thenReturn(matchingAffiliates);
        Mockito.when(userRepository.findByLoginIgnoreCaseIn(anyList()))
            .thenReturn(matchingUsers);
		// test
		Iterable<User> result = userMembershipCalculator.approvedReleaseUsersWithAnyMembership(ihtsdo);

		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	@Test
	public void approvedActiveUsersShouldReturnMatchingUsers() {
		Affiliate affiliate0 = withAffiliate(10L, Sets.newHashSet(sweden));
		User user0 = withUser(affiliate0);

//		Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(Matchers.anyListOf(StandingState.class), Matchers.eq(sweden))).thenReturn(matchingAffiliates);
//		Mockito.when(userRepository.findByLoginIgnoreCaseIn(Matchers.anyListOf(String.class))).thenReturn(matchingUsers);

        Mockito.when(affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(anyList(), eq(sweden)))
            .thenReturn(matchingAffiliates);
        Mockito.when(userRepository.findByLoginIgnoreCaseIn(anyList()))
            .thenReturn(matchingUsers);
		// test
		Iterable<User> result = userMembershipCalculator.approvedActiveUsersWithHomeMembership(sweden);

		Assert.assertThat(Lists.newArrayList(result), CoreMatchers.equalTo(Arrays.asList(user0)));
	}

	private User withUser(Affiliate affiliate0) {
		User user = new User();
		user.setUserId(affiliate0.getAffiliateId());
		matchingUsers.add(user);
		return user;

	}

	private Affiliate withAffiliate(long id, Set<Member> members) {
		Affiliate affiliate = new Affiliate(id);
		affiliate.setCreator("affiliate"+id+"@test.com");
		matchingAffiliates.add(affiliate);
//		Mockito.when(affiliateMembershipCalculator.acceptedMemberships(affiliate)).thenReturn(members);
		return affiliate;
	}

}
