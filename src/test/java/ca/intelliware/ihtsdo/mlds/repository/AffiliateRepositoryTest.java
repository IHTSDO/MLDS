package ca.intelliware.ihtsdo.mlds.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.search.AngularTranslateServiceSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.application.properties")
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class AffiliateRepositoryTest extends MySqlTestContainerTest {

	@Resource
	EntityManager entityManager;

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

		pageable = PageRequest.of(0, 10);

		uniqueKey = ""+System.currentTimeMillis();
	}

	@Before
	public void setupTranslations() {
		new AngularTranslateServiceSetup().setup();
	}

	@Test
	public void findByCheckByIdShouldMatchExtension() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, ihtsdo, "test"+uniqueKey+"@email.com");

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

	@Test
	public void findByUsersAndStandingStateInAndApprovedPrimaryApplication() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.APPROVED);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(Arrays.asList(StandingState.IN_GOOD_STANDING));
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, hasItem(affiliate));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedPrimaryApplicationShouldFailForNotStandingStateMatch() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.INVOICE_SENT, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.APPROVED);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(Arrays.asList(StandingState.IN_GOOD_STANDING));
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, not(hasItem(affiliate)));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedPrimaryApplicationShouldFailForNonApproved() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.REVIEW_REQUESTED);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedPrimaryApplication(Arrays.asList(StandingState.IN_GOOD_STANDING));
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, not(hasItem(affiliate)));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedHomeMembership() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.APPROVED);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(Arrays.asList(StandingState.IN_GOOD_STANDING), sweden);
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, hasItem(affiliate));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedHomeMembershipShouldIgnoreExtension() throws Exception {
		Affiliate affiliateIhtsdo = withAffiliateUser(StandingState.IN_GOOD_STANDING, ihtsdo, "test"+uniqueKey+"@email.com");

		withExtensionApplication(affiliateIhtsdo, sweden, ApprovalState.APPROVED);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(Arrays.asList(StandingState.IN_GOOD_STANDING), sweden);
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, not(hasItem(affiliateIhtsdo)));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedHomeMembershipShouldMatchStandingState() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.DEACTIVATION_PENDING, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.APPROVED);

		persistAffiliate(affiliate);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(Arrays.asList(StandingState.IN_GOOD_STANDING), sweden);
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, not(hasItem(affiliate)));
	}

	@Test
	public void findByUsersAndStandingStateInAndApprovedHomeMembershipShouldMatchApprovalState() throws Exception {
		Affiliate affiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, sweden, "test"+uniqueKey+"@email.com");

		withPrimaryApplication(affiliate, sweden, ApprovalState.REJECTED);

		persistAffiliate(affiliate);

		flush();

		Iterable<Affiliate> elements = affiliateRepository.findByUsersAndStandingStateInAndApprovedHomeMembership(Arrays.asList(StandingState.IN_GOOD_STANDING), sweden);
		HashSet<Affiliate> matches = Sets.newHashSet(elements);

		assertThat(matches, not(hasItem(affiliate)));
	}

	@Test
	public void findByHomeMemberShouldReturnCorrectCountAndPagingWithoutDuplicatesWhenAffiliateHasMultipleApplications() throws Exception {
		Affiliate affiliateWithMultipleApps = withAffiliateUser(StandingState.IN_GOOD_STANDING, sweden, "multi"+uniqueKey+"@email.com");
		withPrimaryApplication(affiliateWithMultipleApps, sweden, ApprovalState.APPROVED);
		withExtensionApplication(affiliateWithMultipleApps, sweden, ApprovalState.APPROVED);
		withExtensionApplication(affiliateWithMultipleApps, sweden, ApprovalState.APPROVED);

		Affiliate belgiumAffiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, belgium, "be"+uniqueKey+"@email.com");
		withExtensionApplication(belgiumAffiliate, sweden, ApprovalState.APPROVED);

		flush();

		Pageable page0 = PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("affiliateId"));
		Page<Affiliate> resultPage0 = affiliateRepository.findByHomeMember(sweden, page0);

		assertThat(resultPage0.getNumberOfElements(), is(1));
		assertThat(resultPage0.getTotalElements() >= 2, is(true));

		Pageable page1 = PageRequest.of(1, 1, org.springframework.data.domain.Sort.by("affiliateId"));
		Page<Affiliate> resultPage1 = affiliateRepository.findByHomeMember(sweden, page1);
		assertThat(resultPage1.getNumberOfElements(), is(1));

		assertThat(resultPage0.getContent().get(0).getAffiliateId(), not(is(resultPage1.getContent().get(0).getAffiliateId())));
	}

	@Test
	public void findByHomeMemberShouldExcludeForeignAffiliatesWithRejectedOrNotSubmittedApplications() throws Exception {
		Affiliate rejectedForeignAffiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, belgium, "rejected"+uniqueKey+"@email.com");
		withExtensionApplication(rejectedForeignAffiliate, sweden, ApprovalState.REJECTED);

		Affiliate notSubmittedForeignAffiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, belgium, "notsubmitted"+uniqueKey+"@email.com");
		withExtensionApplication(notSubmittedForeignAffiliate, sweden, ApprovalState.NOT_SUBMITTED);
        
		Affiliate approvedForeignAffiliate = withAffiliateUser(StandingState.IN_GOOD_STANDING, belgium, "approved"+uniqueKey+"@email.com");
		withExtensionApplication(approvedForeignAffiliate, sweden, ApprovalState.APPROVED);

		flush();

		Pageable allPage = PageRequest.of(0, 100);
		Page<Affiliate> result = affiliateRepository.findByHomeMember(sweden, allPage);

		List<Long> resultIds = result.getContent().stream().map(Affiliate::getAffiliateId).toList();
		assertThat(resultIds, not(hasItem(rejectedForeignAffiliate.getAffiliateId())));
		assertThat(resultIds, not(hasItem(notSubmittedForeignAffiliate.getAffiliateId())));
		assertThat(resultIds, hasItem(approvedForeignAffiliate.getAffiliateId()));
	}

	private void persistAffiliate(Affiliate affiliate) {
		affiliateRepository.save(affiliate);
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

	private Application withExtensionApplication(Affiliate affiliate, Member member, ApprovalState approvalState) {
		ExtensionApplication application = new ExtensionApplication();
		application.setMember(member);
		application.setApprovalState(approvalState);
		entityManager.persist(application);
		affiliate.addApplication(application);

		affiliateRepository.save(affiliate);
		return application;
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

	private String randomString(String prefix) {
		return prefix + random.nextLong();
	}

}
