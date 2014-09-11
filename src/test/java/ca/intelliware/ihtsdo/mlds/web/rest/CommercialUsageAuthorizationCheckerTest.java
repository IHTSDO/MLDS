package ca.intelliware.ihtsdo.mlds.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

@RunWith(MockitoJUnitRunner.class)
public class CommercialUsageAuthorizationCheckerTest {

	@Mock
    private CommercialUsageRepository commercialUsageRepository;

	@Mock
	private CommercialUsageEntryRepository commercialUsageEntryRepository;
	
	@Mock
	private CommercialUsageCountryRepository commercialUsageCountryRepository;
	
	@Mock
	private CurrentSecurityContext currentSecurityContext;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

	private CommercialUsageAuthorizationChecker authorizationChecker;
    
	private static final long COMMERCIAL_USAGE_ID = 1L;
	private static final long COMMERCIAL_USAGE_ENTRY_ID = 2L;
	private static final long COMMERCIAL_USAGE_COUNTRY_ID = 3L;
	
    @Before
    public void setup() {
    	authorizationChecker = new CommercialUsageAuthorizationChecker();
        
    	authorizationChecker.commercialUsageRepository = commercialUsageRepository;
    	authorizationChecker.commercialUsageEntryRepository = commercialUsageEntryRepository;
    	authorizationChecker.commercialUsageCountRepository = commercialUsageCountryRepository;
        
    	authorizationChecker.currentSecurityContext = new CurrentSecurityContext();
    }

    /////////////////////////////////////////////////////////////////////////////
    // Usage Reports
    
    @Test
    public void adminCanAccessUsageReports() {
    	securityContextSetup.asAdmin();

    	authorizationChecker.checkCanAccessUsageReport(COMMERCIAL_USAGE_ID);
    }
    
    @Test
    public void staffCanAccessUsageReports() {
    	securityContextSetup.asIHTSDOStaff();

    	authorizationChecker.checkCanAccessUsageReport(COMMERCIAL_USAGE_ID);
    }

    @Test
    public void affiliateUserCanAccessOwnUsageReports() {
    	withCommercialUsageForAffiliate(affiliateOfCurrentUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessUsageReport(COMMERCIAL_USAGE_ID);
    }

    @Test(expected=IllegalStateException.class)
    public void affiliateUserNotCanAccessAnotherUsageReports() {
    	withCommercialUsageForAffiliate(affiliateOfAnotherUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessUsageReport(COMMERCIAL_USAGE_ID);
    }

    /////////////////////////////////////////////////////////////////////////////
    // Usage Entry
    
    @Test
    public void adminCanAccessUsageEntry() {
    	securityContextSetup.asAdmin();

    	authorizationChecker.checkCanAccessCommercialUsageEntry(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_ENTRY_ID);
    }
    
    @Test
    public void staffCanAccessUsageEntry() {
    	securityContextSetup.asIHTSDOStaff();

    	authorizationChecker.checkCanAccessCommercialUsageEntry(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_ENTRY_ID);
    }

    @Test
    public void affiliateUserCanAccessOwnUsageEntry() {
    	withCommercialEntryForAffiliate(affiliateOfCurrentUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessCommercialUsageEntry(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_ENTRY_ID);
    }

    @Test(expected=IllegalStateException.class)
    public void affiliateUserNotCanAccessAnotherUsageEntry() {
    	withCommercialEntryForAffiliate(affiliateOfAnotherUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessCommercialUsageEntry(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_ENTRY_ID);
    }

    /////////////////////////////////////////////////////////////////////////////
    // Usage Count
    
    @Test
    public void adminCanAccessUsageCount() {
    	securityContextSetup.asAdmin();

    	authorizationChecker.checkCanAccessCommercialUsageCount(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_COUNTRY_ID);
    }
    
    @Test
    public void staffCanAccessUsageCount() {
    	securityContextSetup.asIHTSDOStaff();

    	authorizationChecker.checkCanAccessCommercialUsageCount(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_COUNTRY_ID);
    }

    @Test
    public void affiliateUserCanAccessOwnUsageCount() {
    	withCommercialCountForAffiliate(affiliateOfCurrentUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessCommercialUsageCount(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_COUNTRY_ID);
    }

    @Test(expected=IllegalStateException.class)
    public void affiliateUserNotCanAccessAnotherUsagecount() {
    	withCommercialCountForAffiliate(affiliateOfAnotherUser());

    	securityContextSetup.asAffiliateUser();
    	authorizationChecker.checkCanAccessCommercialUsageCount(COMMERCIAL_USAGE_ID, COMMERCIAL_USAGE_COUNTRY_ID);
    }

    //////////////////////////////////////////////////////////////////////////////

    private CommercialUsageCountry withCommercialCountForAffiliate(Affiliate affiliate) {
    	CommercialUsage commercialUsage = withCommercialUsageForAffiliate(affiliate);
    	CommercialUsageCountry commercialUsageCountry = new CommercialUsageCountry(COMMERCIAL_USAGE_COUNTRY_ID, commercialUsage);
    	Mockito.when(commercialUsageCountryRepository.findOne(COMMERCIAL_USAGE_COUNTRY_ID)).thenReturn(commercialUsageCountry);
    	return commercialUsageCountry;
    }

    private CommercialUsageEntry withCommercialEntryForAffiliate(Affiliate affiliate) {
    	CommercialUsage commercialUsage = withCommercialUsageForAffiliate(affiliate);
    	CommercialUsageEntry commercialUsageEntry = new CommercialUsageEntry(COMMERCIAL_USAGE_ENTRY_ID, commercialUsage);
    	Mockito.when(commercialUsageEntryRepository.findOne(COMMERCIAL_USAGE_ENTRY_ID)).thenReturn(commercialUsageEntry);
    	return commercialUsageEntry;
    }

    private CommercialUsage withCommercialUsageForAffiliate(Affiliate affiliate) {
    	CommercialUsage commercialUsage = new CommercialUsage(COMMERCIAL_USAGE_ID, affiliate);
    	Mockito.when(commercialUsageRepository.findOne(COMMERCIAL_USAGE_ID)).thenReturn(commercialUsage);
    	return commercialUsage;
    }
    
    private Affiliate affiliateOfCurrentUser() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setCreator(SecurityContextSetup.USERNAME);
    	return affiliate;
    }

    private Affiliate affiliateOfAnotherUser() {
    	Affiliate affiliate = new Affiliate();
    	affiliate.setCreator("OTHER-USER");
    	return affiliate;
    }

}
