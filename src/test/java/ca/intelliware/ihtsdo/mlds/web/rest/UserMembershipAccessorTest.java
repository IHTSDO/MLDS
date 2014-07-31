package ca.intelliware.ihtsdo.mlds.web.rest;

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
import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class UserMembershipAccessorTest {
    
	@Mock
    private AffiliateRepository affiliateRepository;

	@Mock
	private MemberRepository memberRespository;
	
	@Mock
	private CurrentSecurityContext currentSecurityContext;

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
        
        userMembershipAccessor.currentSecurityContext = new CurrentSecurityContext();
        
		sweden = new Member("SE");
		ihtsdo = new Member("IHTSDO");
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
    	Mockito.when(affiliateRepository.findByCreator("user")).thenReturn(Lists.newArrayList(affiliate));
    	
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
}
