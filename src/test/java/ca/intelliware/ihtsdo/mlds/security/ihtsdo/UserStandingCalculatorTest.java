package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;

import com.google.common.collect.Lists;

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
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("user")).thenReturn(Lists.newArrayList(affiliate));

    	securityContextSetup.asAffiliateUser();
    	
    	StandingState standingState = userStandingCalculator.getLoggedInUserAffiliateStanding();
    	
    	Assert.assertThat(standingState, Matchers.equalTo(StandingState.IN_GOOD_STANDING));
    }

    @Test
    public void shouldReturnNullStandingForStaff() {
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("IHTSDO")).thenReturn(Lists.<Affiliate>newArrayList());

    	securityContextSetup.asIHTSDOStaff();
    	
    	StandingState standingState = userStandingCalculator.getLoggedInUserAffiliateStanding();
    	
    	Assert.assertThat(standingState, Matchers.nullValue(StandingState.class));
    }

    @Test
    public void shouldReturnNotDeactivatedForAffiliate() {
    	withAffiliateInStandingState(StandingState.IN_GOOD_STANDING);
    	
    	boolean isDeactivated = userStandingCalculator.isLoggedInUserAffiliateDeactivated();
    	
    	Assert.assertThat(isDeactivated, Matchers.equalTo(false));
    }

	private void withAffiliateInStandingState(StandingState standingState) {
		Affiliate affiliate = new Affiliate();
		affiliate.setStandingState(standingState);
    	Mockito.when(affiliateRepository.findByCreatorIgnoreCase("user")).thenReturn(Lists.newArrayList(affiliate));

    	securityContextSetup.asAffiliateUser();
	}

    @Test
    public void shouldReturnDeactivatedForAffiliate() {
    	withAffiliateInStandingState(StandingState.DEACTIVATED);
    	
    	boolean isDeactivated = userStandingCalculator.isLoggedInUserAffiliateDeactivated();
    	
    	Assert.assertThat(isDeactivated, Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnPendingInvocieForAffiliate() {
    	withAffiliateInStandingState(StandingState.PENDING_INVOICE);
    	
    	boolean isPendingInvoice = userStandingCalculator.isLoggedInUserAffiliatePendingInvoice();
    	
    	Assert.assertThat(isPendingInvoice, Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnDeregisteredForAffiliate() {
    	withAffiliateInStandingState(StandingState.DEREGISTERED);
    	
    	boolean isDerigestered = userStandingCalculator.isLoggedInUserAffiliateDeregistered();
    	
    	Assert.assertThat(isDerigestered, Matchers.equalTo(true));
    }
}
