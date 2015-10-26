package ca.intelliware.ihtsdo.mlds.repository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.search.AngularTranslateServiceSetup;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ca.intelliware.ihtsdo.mlds.Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class AffiliateRepositoryTest {
	@Resource EntityManager entityManager;
	
	@Resource AffiliateRepository affiliateRepository;
	
	@Resource MemberRepository memberRepository;
	
	@Resource CountryRepository countryRepository;

	Random random = new Random();
	
	List<Affiliate> affiliates = Lists.newArrayList();
	Member ihtsdo;
	Member sweden;
	Member belgium;

	private Pageable pageable;

	private String uniqueKey;
	
	@Before
	public void setUp() {
		ihtsdo = memberRepository.findOneByKey(Member.KEY_IHTSDO);
		sweden = memberRepository.findOneByKey("SE");
		belgium = memberRepository.findOneByKey("BE");
		
		pageable = new PageRequest(0, 10);
		
		uniqueKey = ""+System.currentTimeMillis();
	}

	@Before
	public void setupTranslations() {
		new AngularTranslateServiceSetup().setup();
	}
	
	@Test
	public void findByCheckByIdShouldMatchExtension() throws Exception {
		Affiliate affiliate = withAffiliate(StandingState.IN_GOOD_STANDING, ihtsdo);
		affiliate.getAffiliateDetails().setEmail("test"+uniqueKey+"@email.com");
		
		withExtensionApplication(affiliate, sweden, ApprovalState.APPROVED);
		
		persistAffiliate(affiliate);
				
		flush();

		// test no-match based on email
		Page<Affiliate> result = affiliateRepository.findForCheck(AffiliateRepository.AFFILIATE_ID_OPTIONAL_VALUE, sweden, "UNKNOWN"+uniqueKey, pageable);
		assertThat(result.getNumberOfElements(), is(0));


		// test match based on email
		result = affiliateRepository.findForCheck(AffiliateRepository.AFFILIATE_ID_OPTIONAL_VALUE, sweden, uniqueKey, pageable);
		assertThat(result.getNumberOfElements(), is(1));
	}

	
	private void persistAffiliate(Affiliate affiliate) {
		affiliateRepository.save(affiliate);		
	}

	private Affiliate withAffiliate(StandingState standingState, Member homeMember) {
		Affiliate affiliate = new Affiliate();
		affiliate.setHomeMember(homeMember);
		affiliate.setAffiliateDetails(new AffiliateDetails());
		
		affiliate.setStandingState(standingState);
		
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

	private String randomString(String prefix) {
		return prefix + random.nextLong();
	}

}
