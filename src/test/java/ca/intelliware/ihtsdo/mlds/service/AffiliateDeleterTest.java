package ca.intelliware.ihtsdo.mlds.service;




import java.util.Optional;
import java.util.Random;


import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.application.properties")
@ActiveProfiles("dev")
@Transactional
public class AffiliateDeleterTest extends MySqlTestContainerTest {
	@Resource
	EntityManager entityManager;

	@Resource AffiliateRepository affiliateRepository;
	@Resource AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource ApplicationRepository applicationRepository;
	@Resource UserRepository userRepository;
	@Resource CommercialUsageRepository commercialUsageRepository;
	@Resource CommercialUsageCountryRepository commercialUsageCountryRepository;
	@Resource CommercialUsageEntryRepository commercialUsageEntryRepository;

	@Resource MemberRepository memberRepository;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

	@Resource AffiliateDeleter affiliateDeleter;

	Random random = new Random();
	private String uniqueKey;

	private Member sweden;
	private Member ihtsdo;

	@Before
	public void setUp() {
		ihtsdo = memberRepository.findOneByKey(Member.KEY_IHTSDO);
		sweden = memberRepository.findOneByKey("SE");

		uniqueKey = ""+System.currentTimeMillis();

		securityContextSetup.asAdmin();
	}

	@Test
	public void deleteAffiliateWithAllData() throws Exception {

		securityContextSetup.asSwedenStaff();

		String userEmail = "test"+uniqueKey+"@email.com";
		User user = withUser(userEmail);
		Affiliate affiliate = withAffiliateUser(StandingState.APPLYING, ihtsdo, userEmail);
		AffiliateDetails affiliateDetails = affiliate.getAffiliateDetails();

		Application primaryApplication = withPrimaryApplication(affiliate, ihtsdo, ApprovalState.APPROVED);
		AffiliateDetails affiliatePrimaryDetails = primaryApplication.getAffiliateDetails();

		Application extensionApplication = withExtensionApplication(affiliate, sweden, ApprovalState.APPROVED);
		AffiliateDetails affiliateExtensionDetails = extensionApplication.getAffiliateDetails();

		CommercialUsage commercialUsage = withCommercialUsage(affiliate);
		CommercialUsageCountry commercialUsageCountry = commercialUsage.getCountries().iterator().next();
		CommercialUsageEntry commercialUsageEntry = commercialUsage.getEntries().iterator().next();

        assertNotEquals(affiliateDetails.getAffiliateDetailsId(),affiliatePrimaryDetails.getAffiliateDetailsId());
        assertNotEquals(affiliateDetails.getAffiliateDetailsId(),affiliateExtensionDetails.getAffiliateDetailsId());
        assertNotEquals(affiliatePrimaryDetails.getAffiliateDetailsId(),affiliateExtensionDetails.getAffiliateDetailsId());

		// Confirm data model accessible from JPA
        assertNotNull(affiliateRepository.findById(affiliate.getAffiliateId()));
        assertNotNull(applicationRepository.findById(primaryApplication.getApplicationId()));
        assertNotNull(applicationRepository.findById(extensionApplication.getApplicationId()));
        assertNotNull(userRepository.findByLoginIgnoreCase(userEmail));
        assertNotNull(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()));
        assertNotNull(affiliateDetailsRepository.findById(affiliateDetails.getAffiliateDetailsId()));
        assertNotNull(affiliateDetailsRepository.findById(affiliatePrimaryDetails.getAffiliateDetailsId()));
        assertNotNull(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()));
        assertNotNull(commercialUsageCountryRepository.findById(commercialUsageCountry.getCommercialUsageCountId()));
        assertNotNull(commercialUsageEntryRepository.findById(commercialUsageEntry.getCommercialUsageEntryId()));
        assertEquals(applicationRepository.findByUsernameIgnoreCase(userEmail).size(),2);


//		// Test
		affiliateDeleter.deleteAffiliate(affiliate);
        assertEquals(affiliateRepository.findById(affiliate.getAffiliateId()), Optional.empty());
        assertEquals(applicationRepository.findById(primaryApplication.getApplicationId()), Optional.empty());
        assertEquals(applicationRepository.findById(extensionApplication.getApplicationId()), Optional.empty());
        assertNull(userRepository.findByLoginIgnoreCase(userEmail));
        assertEquals(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()), Optional.empty());
        assertEquals(affiliateDetailsRepository.findById(affiliateDetails.getAffiliateDetailsId()), Optional.empty());
        assertEquals(affiliateDetailsRepository.findById(affiliatePrimaryDetails.getAffiliateDetailsId()), Optional.empty());
        assertEquals(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), Optional.empty());
        assertEquals(commercialUsageCountryRepository.findById(commercialUsageCountry.getCommercialUsageCountId()), Optional.empty());
        assertEquals(commercialUsageEntryRepository.findById(commercialUsageEntry.getCommercialUsageEntryId()), Optional.empty());

        assertEquals(applicationRepository.findByUsernameIgnoreCase(userEmail).size(),0);

		// JPA should no longer find through custom repository
        assertEquals(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), 1);
        assertEquals(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+primaryApplication.getApplicationId()), 1);
        assertEquals(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+extensionApplication.getApplicationId()), 1);
        assertEquals(matchingNativeRecords("SELECT user_id FROM T_USER WHERE user_id="+user.getUserId()), 1);
        assertEquals(matchingNativeRecords("SELECT commercial_usage_id FROM commercial_usage WHERE commercial_usage_id="+commercialUsage.getCommercialUsageId()), 1);
        assertEquals(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateDetails.getAffiliateDetailsId()), 1);
        assertEquals(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateExtensionDetails.getAffiliateDetailsId()), 1);
        assertEquals(matchingNativeRecords("SELECT commercial_usage_count_id FROM commercial_usage_count WHERE commercial_usage_count_id="+commercialUsageCountry.getCommercialUsageCountId()), 1);
        assertEquals(matchingNativeRecords("SELECT commercial_usage_entry_id FROM commercial_usage_entry WHERE commercial_usage_entry_id="+commercialUsageEntry.getCommercialUsageEntryId()), 1);
        assertEquals(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliatePrimaryDetails.getAffiliateDetailsId()), 1);

	}

	@Test
	public void deleteAffiliateWithMinimalData() throws Exception {

		securityContextSetup.asSwedenStaff();

		String userEmail = "test"+uniqueKey+"@email.com";
		Affiliate affiliate = withAffiliateUser(StandingState.APPLYING, ihtsdo, userEmail);

        assertNotNull(affiliateRepository.findById(affiliate.getAffiliateId()));

		affiliateDeleter.deleteAffiliate(affiliate);

		// JPA should no longer match entities

        assertEquals(affiliateRepository.findById(affiliate.getAffiliateId()), Optional.empty());

        assertEquals(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), 1);
	}

	@Test
	public void deleteAffiliateWithSharedAffiliateDetails() throws Exception {

        securityContextSetup.asSwedenStaff();

        String userEmail = "test"+uniqueKey+"@email.com";
        User user = withUser(userEmail);
        Affiliate affiliate = withAffiliateUser(StandingState.APPLYING, ihtsdo, userEmail);
        AffiliateDetails affiliateSharedDetails = affiliate.getAffiliateDetails();
        Application primaryApplication = withPrimaryApplication(affiliate, ihtsdo, ApprovalState.APPROVED);
        primaryApplication.setAffiliateDetails(affiliate.getAffiliateDetails());
        applicationRepository.save(primaryApplication);
        Application extensionApplication = withExtensionApplication(affiliate, sweden, ApprovalState.APPROVED);
        AffiliateDetails affiliateExtensionDetails = extensionApplication.getAffiliateDetails();
        CommercialUsage commercialUsage = withCommercialUsage(affiliate);

//       Confirm data model accessible from JPA
        assertNotNull(affiliateRepository.findById(affiliate.getAffiliateId()));
        assertNotNull(applicationRepository.findById(primaryApplication.getApplicationId()));
        assertNotNull(applicationRepository.findById(extensionApplication.getApplicationId()));
        assertNotNull(userRepository.findByLoginIgnoreCase(userEmail));
        assertNotNull(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()));
        assertNotNull(affiliateDetailsRepository.findById(affiliateSharedDetails.getAffiliateDetailsId()));
        assertNotNull(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()));

        assertEquals(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), 2);

        // Test
        affiliateDeleter.deleteAffiliate(affiliate);

        // JPA should no longer match entities
        assertEquals(affiliateRepository.findById(affiliate.getAffiliateId()),Optional.empty());
        assertEquals(applicationRepository.findById(primaryApplication.getApplicationId()), Optional.empty());
        assertEquals(applicationRepository.findById(extensionApplication.getApplicationId()), Optional.empty());
        assertNull(userRepository.findByLoginIgnoreCase(userEmail));
        assertEquals(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()),Optional.empty());
        assertEquals(affiliateDetailsRepository.findById(affiliateSharedDetails.getAffiliateDetailsId()),Optional.empty());
        assertEquals(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), Optional.empty());

        // JPA should no longer find through custom repository
        assertEquals(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), 0);

        // Records should still be present in the database
        assertEquals(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), 1);
        assertEquals(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+primaryApplication.getApplicationId()), 1);
        assertEquals(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+extensionApplication.getApplicationId()), 1);
        assertEquals(matchingNativeRecords("SELECT user_id FROM T_USER WHERE user_id="+user.getUserId()), 1);
        assertEquals(matchingNativeRecords("SELECT commercial_usage_id FROM commercial_usage WHERE commercial_usage_id="+commercialUsage.getCommercialUsageId()), 1);
        assertEquals(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateSharedDetails.getAffiliateDetailsId()), 1);
        assertEquals(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateExtensionDetails.getAffiliateDetailsId()), 1);
	}

	private CommercialUsage withCommercialUsage(Affiliate affiliate) {
		CommercialUsage commercialUsage = new CommercialUsage();
		commercialUsage.setType(AffiliateType.COMMERCIAL);

		entityManager.persist(commercialUsage);

		CommercialUsageCountry count = new CommercialUsageCountry();
		entityManager.persist(count);
		commercialUsage.addCount(count);

		CommercialUsageEntry entry = new CommercialUsageEntry();
		entityManager.persist(entry);
		commercialUsage.addEntry(entry);

		affiliate.addCommercialUsage(commercialUsage);
		affiliateRepository.save(affiliate);

		return commercialUsage;
	}

	private int matchingNativeRecords(String query) {
		return entityManager.createNativeQuery(query).getResultList().size();
	}

	private Affiliate withAffiliateUser(StandingState standingState, Member homeMember, String email) {
		Affiliate affiliate = new Affiliate();
		affiliate.setHomeMember(homeMember);
		affiliate.setCreator(email);
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		affiliateDetails.setEmail(email);
		affiliate.setAffiliateDetails(affiliateDetails);

		affiliate.setStandingState(standingState);

		entityManager.persist(affiliate.getAffiliateDetails());
		affiliateRepository.save(affiliate);

		return affiliate;
	}

	private User withUser(String email) {
		User user = new User();
		user.setEmail(email);
		user.setLogin(email);
        user.setCreatedBy("anonymousUser");
		userRepository.save(user);

		return user;
	}

	private Application withPrimaryApplication(Affiliate affiliate, Member member, ApprovalState approvalState) {
		PrimaryApplication application = new PrimaryApplication();
		application.setMember(member);
		application.setApprovalState(approvalState);
		application.setUsername(affiliate.getCreator());
		AffiliateDetails applicationAffiliateDetails = createStandaloneAffiliateDetails();
		application.setAffiliateDetails(applicationAffiliateDetails);
		entityManager.persist(application);
		affiliate.setApplication(application);

		affiliateRepository.save(affiliate);
		return application;
	}

	private AffiliateDetails createStandaloneAffiliateDetails() {
		AffiliateDetails applicationAffiliateDetails = new AffiliateDetails();
		applicationAffiliateDetails.setFirstName(randomString("firstName"));
		applicationAffiliateDetails.setLastName(randomString("lastName"));;
		applicationAffiliateDetails.setAddress(new MailingAddress());
		return applicationAffiliateDetails;
	}

	/**
	 * flush to JPA + Lucene.  JPA queries trigger a JPA flush, but Hibernate Search
	 * only flushes on TX commit by default.  But are using a TX rollback test strategy, so we
	 * need to flush Lucene manually.
	 */
	private void flush() {
		entityManager.flush();
	}

	Affiliate makeAffiliate() {
		Affiliate affiliate = new Affiliate();
		affiliate.setAffiliateDetails(new AffiliateDetails());
		affiliate.getAffiliateDetails().setFirstName(randomString("firstName"));;
		affiliate.getAffiliateDetails().setLastName(randomString("lastName"));;
		affiliate.getAffiliateDetails().setAddress(new MailingAddress());
		affiliate.setHomeMember(ihtsdo);

		entityManager.persist(affiliate.getAffiliateDetails());
		affiliateRepository.save(affiliate);

		return affiliate;
	}

	private Application withExtensionApplication(Affiliate affiliate, Member member, ApprovalState approvalState) {
		ExtensionApplication application = new ExtensionApplication();
		application.setMember(member);
		application.setApprovalState(approvalState);
		application.setUsername(affiliate.getCreator());
		AffiliateDetails affiliateDetails = createStandaloneAffiliateDetails();
		application.setAffiliateDetails(affiliateDetails);
		entityManager.persist(application);
		affiliate.addApplication(application);

		affiliateRepository.save(affiliate);
		return application;
	}

	private String randomString(String prefix) {
		return prefix + random.nextLong();
	}


}

