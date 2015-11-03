package ca.intelliware.ihtsdo.mlds.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Random;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ca.intelliware.ihtsdo.mlds.Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class AffiliateDeleterTest {
	@Resource EntityManager entityManager;
	
	@Resource AffiliateRepository affiliateRepository;
	@Resource ApplicationRepository applicationRepository;
	@Resource UserRepository userRepository;
	@Resource CommercialUsageRepository commercialUsageRepository;

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
		Application primaryApplication = withPrimaryApplication(affiliate, ihtsdo, ApprovalState.APPROVED);
		Application extensionApplication = withExtensionApplication(affiliate, sweden, ApprovalState.APPROVED);
		CommercialUsage commercialUsage = withCommercialUsage(affiliate);

		assertThat(affiliateRepository.findOne(affiliate.getAffiliateId()), notNullValue(Affiliate.class));
		assertThat(applicationRepository.findOne(primaryApplication.getApplicationId()), notNullValue(Application.class));
		assertThat(applicationRepository.findOne(extensionApplication.getApplicationId()), notNullValue(Application.class));
		assertThat(userRepository.findByLoginIgnoreCase(userEmail), notNullValue(User.class));
		assertThat(commercialUsageRepository.findOne(commercialUsage.getCommercialUsageId()), notNullValue(CommercialUsage.class));
		
		affiliateDeleter.deleteAffiliate(affiliate);
		
		// JPA should no longer match entities
		assertThat(affiliateRepository.findOne(affiliate.getAffiliateId()), nullValue(Affiliate.class));
		assertThat(applicationRepository.findOne(primaryApplication.getApplicationId()), nullValue(Application.class));
		assertThat(applicationRepository.findOne(extensionApplication.getApplicationId()), nullValue(Application.class));
		assertThat(userRepository.findByLoginIgnoreCase(userEmail), nullValue(User.class));
		assertThat(commercialUsageRepository.findOne(commercialUsage.getCommercialUsageId()), nullValue(CommercialUsage.class));
		
		// Records should still be present in the database
		assertThat(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), is(1));
		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+primaryApplication.getApplicationId()), is(1));
		assertThat(matchingNativeRecords("SELECT application_id FROM application WHERE application_id="+extensionApplication.getApplicationId()), is(1));
		assertThat(matchingNativeRecords("SELECT user_id FROM T_USER WHERE user_id="+user.getUserId()), is(1));
		assertThat(matchingNativeRecords("SELECT commercial_usage_id FROM commercial_usage WHERE commercial_usage_id="+commercialUsage.getCommercialUsageId()), is(1));
	}

	@Test
	public void deleteAffiliateWithMinimalData() throws Exception {
		
		securityContextSetup.asSwedenStaff();
		
		String userEmail = "test"+uniqueKey+"@email.com";
		Affiliate affiliate = withAffiliateUser(StandingState.APPLYING, ihtsdo, userEmail);

		assertThat(affiliateRepository.findOne(affiliate.getAffiliateId()), notNullValue(Affiliate.class));
		
		affiliateDeleter.deleteAffiliate(affiliate);
		
		// JPA should no longer match entities
		assertThat(affiliateRepository.findOne(affiliate.getAffiliateId()), nullValue(Affiliate.class));
		
		// Records should still be present in the database
		assertThat(matchingNativeRecords("SELECT affiliate_id FROM affiliate WHERE affiliate_id="+affiliate.getAffiliateId()), is(1));
	}

	private CommercialUsage withCommercialUsage(Affiliate affiliate) {
		CommercialUsage commercialUsage = new CommercialUsage();
		commercialUsage.setType(AffiliateType.ACADEMIC);
		
		entityManager.persist(commercialUsage);
		
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
		entityManager.persist(application);
		affiliate.addApplication(application);
		affiliate.setApplication(application);
		
		affiliateRepository.save(affiliate);	
		return application;
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
		entityManager.persist(application);
		affiliate.addApplication(application);
		
		affiliateRepository.save(affiliate);	
		return application;
	}

	private String randomString(String prefix) {
		return prefix + random.nextLong();
	}


}
