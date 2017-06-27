package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.search.AngularTranslateServiceSetup;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Transactional
public class UserNotifier_Load_Test {

	@Resource EntityManager entityManager;
	
	@Resource AffiliateRepository affiliateRepository;
	@Resource UserRepository userRepository;
	
	@Resource MemberRepository memberRepository;
	
	@Resource CountryRepository countryRepository;
	
	@Resource UserNotifier userNotifier;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	
	Random random = new Random();
	
	List<Affiliate> affiliates = Lists.newArrayList();
	Member ihtsdo;
	Member sweden;
	Member belgium;

	private String uniqueKey;
	
	@Before
	public void setUp() {
		ihtsdo = memberRepository.findOneByKey(Member.KEY_IHTSDO);
		sweden = memberRepository.findOneByKey("SE");
		belgium = memberRepository.findOneByKey("BE");
		
		uniqueKey = ""+System.currentTimeMillis();
	}

	@Before
	public void setupTranslations() {
		new AngularTranslateServiceSetup().setup();
	}

	@Test
	@Ignore("Slow running")
	public void notifyReleasePackageUpdated() throws Exception {
		securityContextSetup.asSwedenStaff();
		
		int NUMBER_OF_AFFILIATES = 5000;
		System.out.print("Loading "+NUMBER_OF_AFFILIATES+" affiliates....");
		for (int i = 0; i < NUMBER_OF_AFFILIATES; i++) {
			Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, ihtsdo, "test"+uniqueKey+"x"+i+"@email.com");
			withExtensionApplication(affiliate, sweden, ApprovalState.APPROVED);
			flush();
			if (i % 1000 == 0) {
				System.out.print("m");
			} else if (i % 500 == 0) {
				System.out.print("d");
			} else if (i % 100 == 0) {
				System.out.print("c");
			}
		}
		System.out.println(" done");
		
		System.out.println("Start email processing....");
		long start = System.currentTimeMillis();
				
		ReleasePackage releasePackage = new ReleasePackage(1L);
		releasePackage.setMember(sweden);
		ReleaseVersion releaseVersion = new ReleaseVersion(2L);
		releasePackage.addReleaseVersion(releaseVersion);

		userNotifier.notifyReleasePackageUpdated(releaseVersion);
		System.out.println("*** DONE **** "+ ((System.currentTimeMillis() - start) / 1000.0)+"s");
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

		User user = new User();
		user.setEmail(email);
		user.setLogin(email);
		user.setLangKey("en");
		
		userRepository.save(user);

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

	private String randomString(String prefix) {
		return prefix + random.nextLong();
	}

}
