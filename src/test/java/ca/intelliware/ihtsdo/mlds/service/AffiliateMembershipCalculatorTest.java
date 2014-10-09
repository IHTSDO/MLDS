package ca.intelliware.ihtsdo.mlds.service;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

@RunWith(MockitoJUnitRunner.class)
public class AffiliateMembershipCalculatorTest {

	@Mock
	private MemberRepository memberRespository;

	Member ihtsdo;
	Member sweden;
	Member france;
	
	Affiliate affiliate;
	
	private AffiliateMembershipCalculator affiliateMembershipCalculator;
    
    @Before
    public void setup() {
    	affiliateMembershipCalculator = new AffiliateMembershipCalculator();
        
    	affiliateMembershipCalculator.memberRepository = memberRespository;
        
		sweden = new Member("SE", 1);
		france = new Member("FR", 2);
		ihtsdo = new Member("IHTSDO", 3);
		
		affiliate = new Affiliate();
		
		Mockito.when(memberRespository.findOneByKey("IHTSDO")).thenReturn(ihtsdo);
    }

    @Test
    public void acceptedMembershipsShouldReturnEmptyForNewAffiliate() {
    	Set<Member> result = affiliateMembershipCalculator.acceptedMemberships(affiliate);
    	
    	Assert.assertThat(result, Matchers.emptyCollectionOf(Member.class));
    }

    @Test
    public void acceptedMembershipsShouldReturnOnlyAcceptedMemberships() {
    	withExtensionApplication(sweden, ApprovalState.NOT_SUBMITTED);
    	withExtensionApplication(france, ApprovalState.APPROVED);
    	
    	Set<Member> result = affiliateMembershipCalculator.acceptedMemberships(affiliate);
    	
    	Assert.assertThat(result, Matchers.contains(france));
    }

    @Test
    public void acceptedMembershipsShouldSupportMultipleMemberships() {
    	withExtensionApplication(sweden, ApprovalState.APPROVED);
    	withExtensionApplication(france, ApprovalState.APPROVED);
    	
    	Set<Member> result = affiliateMembershipCalculator.acceptedMemberships(affiliate);
    	
    	Assert.assertThat(result, Matchers.containsInAnyOrder(france, sweden));
    }

    @Test
    public void acceptedMembershipsShouldSupportManyApplicationsForSameMember() {
    	withExtensionApplication(sweden, ApprovalState.REJECTED);
    	withExtensionApplication(sweden, ApprovalState.APPROVED);
    	
    	Set<Member> result = affiliateMembershipCalculator.acceptedMemberships(affiliate);
    	
    	Assert.assertThat(result, Matchers.contains(sweden));
    }

    @Test
    public void acceptedMembershipsShouldIncludeIhtsdoForPrimary() {
    	withPrimaryApplication(france, ApprovalState.APPROVED);
    	
    	Set<Member> result = affiliateMembershipCalculator.acceptedMemberships(affiliate);
    	
    	Assert.assertThat(result, Matchers.contains(ihtsdo, france));
    }

    private Application withPrimaryApplication(Member member, ApprovalState approvalState) {
    	Application application = new PrimaryApplication(affiliate.getApplications().size() + 1);
    	return withApplication(application, member, approvalState);

    }

    private Application withExtensionApplication(Member member, ApprovalState approvalState) {
    	Application application = new ExtensionApplication(affiliate.getApplications().size() + 1);
    	return withApplication(application, member, approvalState);

    }
    
    private Application withApplication(Application application, Member member, ApprovalState approvalState) {
    	application.setMember(member);
    	application.setApprovalState(approvalState);
    	affiliate.addApplication(application);
    	return application;
    }
}
