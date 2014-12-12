package ca.intelliware.ihtsdo.mlds.service;

import java.util.Collection;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class UserMembershipAccessorTest {
    
	@Mock
    private AffiliateRepository affiliateRepository;

	@Mock
	private MemberRepository memberRespository;
	
	@Mock
	private CurrentSecurityContext currentSecurityContext;
	
	@Mock
	private AffiliateMembershipCalculator affiliateMembershipCalculator;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

	Member ihtsdo;
	Member sweden;
	
	private UserMembershipAccessor userMembershipAccessor;
    
    @Before
    public void setup() {
        userMembershipAccessor = new UserMembershipAccessor();
        
        userMembershipAccessor.affiliateRepository = affiliateRepository;
        userMembershipAccessor.currentSecurityContext = currentSecurityContext;
        userMembershipAccessor.memberRepository = memberRespository;
        userMembershipAccessor.affiliateMembershipCalculator = affiliateMembershipCalculator;
        
        userMembershipAccessor.currentSecurityContext = new CurrentSecurityContext();
        
		sweden = new Member("SE", 1);
		ihtsdo = new Member("IHTSDO", 2);
    }

    @Test
    public void shouldReturnMemberForAdminBasedOnRole() {
    	Mockito.when(memberRespository.findOneByKey("IHTSDO")).thenReturn(ihtsdo);
    	
    	securityContextSetup.asAdmin();
    	
    	Member member = userMembershipAccessor.getMemberAssociatedWithUser();
    	
    	Assert.assertThat(member, Matchers.equalTo(ihtsdo));
    }

    @Test
    public void shouldReturnMemberForStaffBasedOnRole() {
    	Mockito.when(memberRespository.findOneByKey("IHTSDO")).thenReturn(ihtsdo);
    	
    	securityContextSetup.asIHTSDOStaff();
    	
    	Member member = userMembershipAccessor.getMemberAssociatedWithUser();
    	
    	Assert.assertThat(member, Matchers.equalTo(ihtsdo));
    }

    @Test
    public void shouldReturnMemberForAffiliateBasedOnAffiliateHomeMember() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setHomeMember(sweden);
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("user")).thenReturn(Lists.newArrayList(affiliate));
    	
    	securityContextSetup.asAffiliateUser();
    	
    	Member member = userMembershipAccessor.getMemberAssociatedWithUser();
    	
    	Assert.assertThat(member, Matchers.equalTo(sweden));
    }

    @Test
    public void shouldReturnNullForAnonymousUser() {
    	securityContextSetup.asAnonymous();
    	
    	Member member = userMembershipAccessor.getMemberAssociatedWithUser();
    	
    	Assert.assertThat(member, Matchers.is((Member)null));
    }

    @Test
    public void getAcceptedAffiliateMembershipsOfUserShouldContainAcceptedApplicationsForAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	//Note that in this scenario the sweeden extension has not been accepted yet...
    	affiliate.setHomeMember(sweden);
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("user")).thenReturn(Lists.newArrayList(affiliate));
    	
    	Mockito.when(affiliateMembershipCalculator.acceptedMemberships(affiliate)).thenReturn(Sets.newHashSet(ihtsdo));
    	
    	securityContextSetup.asAffiliateUser();
    	
    	Collection<Member> affiliateMemberships = userMembershipAccessor.getAcceptedAffiliateMembershipsOfUser();
    	
    	Assert.assertThat(affiliateMemberships, Matchers.contains(ihtsdo));
    }

    @Test
    public void isAffiliateMemberApplicationAcceptedShouldContainAcceptedApplicationsForAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	//Note that in this scenario the sweeden extension has not been accepted yet...
    	affiliate.setHomeMember(sweden);
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("user")).thenReturn(Lists.newArrayList(affiliate));
    	
    	Mockito.when(affiliateMembershipCalculator.acceptedMemberships(affiliate)).thenReturn(Sets.newHashSet(ihtsdo));
    	
    	securityContextSetup.asAffiliateUser();
    	
    	Assert.assertThat(userMembershipAccessor.isAffiliateMemberApplicationAccepted(ihtsdo), Matchers.equalTo(true));
    	Assert.assertThat(userMembershipAccessor.isAffiliateMemberApplicationAccepted(sweden), Matchers.equalTo(false));
    }

    @Test
    public void getAcceptedAffiliateMembershipsOfUserShouldBeEmptyForStaff() {
    	securityContextSetup.asIHTSDOStaff();
    	
    	Collection<Member> affiliateMemberships = userMembershipAccessor.getAcceptedAffiliateMembershipsOfUser();
    	
    	Assert.assertThat(affiliateMemberships, Matchers.emptyCollectionOf(Member.class));
    }

    @Test
    public void getAcceptedAffiliateMembershipsOfUserShouldBeEmptyForAnonymous() {
    	securityContextSetup.asAnonymous();
    	
    	Collection<Member> affiliateMemberships = userMembershipAccessor.getAcceptedAffiliateMembershipsOfUser();
    	
    	Assert.assertThat(affiliateMemberships, Matchers.emptyCollectionOf(Member.class));
    	
    }
}
