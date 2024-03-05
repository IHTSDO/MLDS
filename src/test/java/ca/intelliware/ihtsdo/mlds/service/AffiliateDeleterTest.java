package ca.intelliware.ihtsdo.mlds.service;




import java.util.Random;


import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import ca.intelliware.ihtsdo.mlds.config.PostgresTestContainerTest;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
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
 // TODO Need to change assertThat
//		// Confirm unique affiliate details
//		assertThat(affiliateDetails.getAffiliateDetailsId(), not(equalTo(affiliatePrimaryDetails.getAffiliateDetailsId())));
//		assertThat(affiliateDetails.getAffiliateDetailsId(), not(equalTo(affiliateExtensionDetails.getAffiliateDetailsId())));
//		assertThat(affiliatePrimaryDetails.getAffiliateDetailsId(), not(equalTo(affiliateExtensionDetails.getAffiliateDetailsId())));
//
//		// Confirm data model accessible from JPA
//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), notNullValue(Affiliate.class));
//		assertThat(applicationRepository.findById(primaryApplication.getApplicationId()), notNullValue(Application.class));
//		assertThat(applicationRepository.findById(extensionApplication.getApplicationId()), notNullValue(Application.class));
//		assertThat(userRepository.findByLoginIgnoreCase(userEmail), notNullValue(User.class));
//		assertThat(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()), notNullValue(CommercialUsage.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateDetails.getAffiliateDetailsId()), notNullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliatePrimaryDetails.getAffiliateDetailsId()), notNullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), notNullValue(AffiliateDetails.class));
//		assertThat(commercialUsageCountryRepository.findById(commercialUsageCountry.getCommercialUsageCountId()), notNullValue(CommercialUsageCountry.class));
//		assertThat(commercialUsageEntryRepository.findById(commercialUsageEntry.getCommercialUsageEntryId()), notNullValue(CommercialUsageEntry.class));
//
//		assertThat(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), is(2));
//
//		// Test
//		affiliateDeleter.deleteAffiliate(affiliate);
//
//		// JPA should no longer match entities
//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), nullValue(Affiliate.class));
//		assertThat(applicationRepository.findById(primaryApplication.getApplicationId()), nullValue(Application.class));
//		assertThat(applicationRepository.findById(extensionApplication.getApplicationId()), nullValue(Application.class));
//		assertThat(userRepository.findByLoginIgnoreCase(userEmail), nullValue(User.class));
//		assertThat(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()), nullValue(CommercialUsage.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateDetails.getAffiliateDetailsId()), nullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliatePrimaryDetails.getAffiliateDetailsId()), nullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), nullValue(AffiliateDetails.class));
//		assertThat(commercialUsageCountryRepository.findById(commercialUsageCountry.getCommercialUsageCountId()), nullValue(CommercialUsageCountry.class));
//		assertThat(commercialUsageEntryRepository.findById(commercialUsageEntry.getCommercialUsageEntryId()), nullValue(CommercialUsageEntry.class));
//
//		// JPA should no longer find through custom repository
//		assertThat(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), is(0));
//
//		// Records should still be present in the database
//		assertThat(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), is(1));
//		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+primaryApplication.getApplicationId()), is(1));
//		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+extensionApplication.getApplicationId()), is(1));
//		assertThat(matchingNativeRecords("SELECT user_id FROM T_USER WHERE user_id="+user.getUserId()), is(1));
//		assertThat(matchingNativeRecords("SELECT commercial_usage_id FROM commercial_usage WHERE commercial_usage_id="+commercialUsage.getCommercialUsageId()), is(1));
//		assertThat(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateDetails.getAffiliateDetailsId()), is(1));
//		assertThat(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliatePrimaryDetails.getAffiliateDetailsId()), is(1));
//		assertThat(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateExtensionDetails.getAffiliateDetailsId()), is(1));
//		assertThat(matchingNativeRecords("SELECT commercial_usage_count_id FROM commercial_usage_count WHERE commercial_usage_count_id="+commercialUsageCountry.getCommercialUsageCountId()), is(1));
//		assertThat(matchingNativeRecords("SELECT commercial_usage_entry_id FROM commercial_usage_entry WHERE commercial_usage_entry_id="+commercialUsageEntry.getCommercialUsageEntryId()), is(1));
	}

	@Test
	public void deleteAffiliateWithMinimalData() throws Exception {

		securityContextSetup.asSwedenStaff();

		String userEmail = "test"+uniqueKey+"@email.com";
		Affiliate affiliate = withAffiliateUser(StandingState.APPLYING, ihtsdo, userEmail);

//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), notNullValue(Affiliate.class));
//
//		affiliateDeleter.deleteAffiliate(affiliate);
//
//		// JPA should no longer match entities
//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), nullValue(Affiliate.class));
//
//		// Records should still be present in the database
//		assertThat(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), is(1));
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

		// Confirm data model accessible from JPA
//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), notNullValue(Affiliate.class));
//		assertThat(applicationRepository.findById(primaryApplication.getApplicationId()), notNullValue(Application.class));
//		assertThat(applicationRepository.findById(extensionApplication.getApplicationId()), notNullValue(Application.class));
//		assertThat(userRepository.findByLoginIgnoreCase(userEmail), notNullValue(User.class));
//		assertThat(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()), notNullValue(CommercialUsage.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateSharedDetails.getAffiliateDetailsId()), notNullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), notNullValue(AffiliateDetails.class));
//
//		assertThat(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), is(2));
//
//		// Test
//		affiliateDeleter.deleteAffiliate(affiliate);
//
//		// JPA should no longer match entities
//		assertThat(affiliateRepository.findById(affiliate.getAffiliateId()), nullValue(Affiliate.class));
//		assertThat(applicationRepository.findById(primaryApplication.getApplicationId()), nullValue(Application.class));
//		assertThat(applicationRepository.findById(extensionApplication.getApplicationId()), nullValue(Application.class));
//		assertThat(userRepository.findByLoginIgnoreCase(userEmail), nullValue(User.class));
//		assertThat(commercialUsageRepository.findById(commercialUsage.getCommercialUsageId()), nullValue(CommercialUsage.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateSharedDetails.getAffiliateDetailsId()), nullValue(AffiliateDetails.class));
//		assertThat(affiliateDetailsRepository.findById(affiliateExtensionDetails.getAffiliateDetailsId()), nullValue(AffiliateDetails.class));
//
//		// JPA should no longer find through custom repository
//		assertThat(applicationRepository.findByUsernameIgnoreCase(userEmail).size(), is(0));
//
//		// Records should still be present in the database
//		assertThat(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), is(1));
//		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+primaryApplication.getApplicationId()), is(1));
//		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+extensionApplication.getApplicationId()), is(1));
//		assertThat(matchingNativeRecords("SELECT user_id FROM T_USER WHERE user_id="+user.getUserId()), is(1));
//		assertThat(matchingNativeRecords("SELECT commercial_usage_id FROM commercial_usage WHERE commercial_usage_id="+commercialUsage.getCommercialUsageId()), is(1));
//		assertThat(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateSharedDetails.getAffiliateDetailsId()), is(1));
//		assertThat(matchingNativeRecords("SELECT affiliate_details_id FROM affiliate_details WHERE affiliate_details_id="+affiliateExtensionDetails.getAffiliateDetailsId()), is(1));
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

