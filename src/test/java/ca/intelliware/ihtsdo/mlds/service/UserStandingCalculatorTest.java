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
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.SecurityContextSetup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class UserStandingCalculatorTest {
    
	@Mock
    private AffiliateRepository affiliateRepository;

	@Mock
	private CurrentSecurityContext currentSecurityContext;
	
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

	private UserStandingCalculator userStandingCalculator;
    
    @Before
    public void setup() {
        userStandingCalculator = new UserStandingCalculator();
        
        userStandingCalculator.affiliateRepository = affiliateRepository;
        userStandingCalculator.currentSecurityContext = currentSecurityContext;
        
        userStandingCalculator.currentSecurityContext = new CurrentSecurityContext();
    }

    @Test
    public void shouldReturnStandingForAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
    	Mockito.when(affiliateRepository.findByCreator("user")).thenReturn(Lists.newArrayList(affiliate));

    	securityContextSetup.asAffiliateUser();
    	
    	StandingState standingState = userStandingCalculator.getAffiliateStanding();
    	
    	Assert.assertThat(standingState, Matchers.equalTo(StandingState.IN_GOOD_STANDING));
    }

    @Test
    public void shouldReturnNullStandingForStaff() {
    	Mockito.when(affiliateRepository.findByCreator("IHTSDO")).thenReturn(Lists.<Affiliate>newArrayList());

    	securityContextSetup.asIHTSDOStaff();
    	
    	StandingState standingState = userStandingCalculator.getAffiliateStanding();
    	
    	Assert.assertThat(standingState, Matchers.nullValue(StandingState.class));
    }

    @Test
    public void shouldReturnNotDeactivatedForAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
    	Mockito.when(affiliateRepository.findByCreator("user")).thenReturn(Lists.newArrayList(affiliate));

    	securityContextSetup.asAffiliateUser();
    	
    	boolean isDeactivated = userStandingCalculator.isAffiliateDeactivated();
    	
    	Assert.assertThat(isDeactivated, Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnDeactivatedForAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setStandingState(StandingState.DEACTIVATED);
    	Mockito.when(affiliateRepository.findByCreator("user")).thenReturn(Lists.newArrayList(affiliate));

    	securityContextSetup.asAffiliateUser();
    	
    	boolean isDeactivated = userStandingCalculator.isAffiliateDeactivated();
    	
    	Assert.assertThat(isDeactivated, Matchers.equalTo(true));
    }

}
