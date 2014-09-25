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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.Application;
import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.search.AngularTranslateServiceSetup;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class AffiliateFullTextSearchTest {
	@Resource EntityManager entityManager;
	
	@Resource AffiliateRepository affiliateRepository;
	
	@Resource AffiliateSearchRepository affiliateSearchRepository;
	
	@Resource MemberRepository memberRepository;
	
	@Resource CountryRepository countryRepository;

	Random random = new Random();
	
	List<Affiliate> affiliates = Lists.newArrayList();
	Member ihtsdo;
	
	@Before
	public void setUp() {
		ihtsdo = memberRepository.findOneByKey(Member.KEY_IHTSDO);
		for (int i = 0; i < 10; i++) {
			affiliates.add(makeAffiliate());
		}
	}

	@Before
	public void setupTranslations() {
		new AngularTranslateServiceSetup().setup();
	}
	
	@Test
	public void findAffiliateByFirstName() throws Exception {
		long ourInstance = System.currentTimeMillis();
		
		String targetFirstName = "FirstNameTestValue" + ourInstance;
		
		affiliates.get(3).getAffiliateDetails().setFirstName(targetFirstName);
		affiliates.get(5).getAffiliateDetails().setFirstName(targetFirstName);
		
		flush();
		
		List<Affiliate> resultList = search(targetFirstName);
		
		assertThat(resultList.size(), is(2));
		assertThat(resultList, containsInAnyOrder(affiliates.get(3), affiliates.get(5)));
	}

	/**
	 * Make sure that our "ALL" fields is being populated so we can search across multiple fields.
	 * @throws Exception
	 */
	@Test
	public void findAffiliateByFirstAndLastNameBestMatchFirst() throws Exception {
		long ourInstance = System.currentTimeMillis();
		
		String targetFirstName = "FirstNameTestValue" + ourInstance;
		String targetLastName = "LastNameTestValue" + ourInstance;
		
		affiliates.get(3).getAffiliateDetails().setFirstName(targetFirstName);
		affiliates.get(3).getAffiliateDetails().setLastName(targetLastName);
		
		affiliates.get(5).getAffiliateDetails().setFirstName(targetFirstName);
		
		flush();

		List<Affiliate> resultList = search(targetFirstName + " " + targetLastName);
		
		assertThat(resultList.size(), is(2));
		assertThat(resultList, contains(affiliates.get(3),affiliates.get(5)));
	}

	@Test
	public void findAffiliateByPartialName() throws Exception {
		long ourInstance = System.currentTimeMillis();
		
		String targetFirstName = "ZZFirstNameTestValue" + ourInstance;
		
		affiliates.get(4).getAffiliateDetails().setFirstName(targetFirstName);
		
		flush();
		
		List<Affiliate> resultList = search("ZZFirst");
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}
	
	@Test
	public void findAffiliateByMultiplePartialMatches() throws Exception {
		long ourInstance = System.currentTimeMillis();
		
		String targetFirstName = "ZZFirstNameTestValue" + ourInstance;
		String targetLastName = "ZZLastNameTestValue" + ourInstance;
		
		affiliates.get(4).getAffiliateDetails().setFirstName(targetFirstName);
		affiliates.get(4).getAffiliateDetails().setLastName(targetLastName);
		
		flush();
		
		List<Affiliate> resultList = search("ZZFirst ZZLast");
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}
	
	@Test
	public void findAffiliateByCountryCommonName() throws Exception {
		Country country = new Country("ZZ","ZZZ","ZZZimba");
		countryRepository.save(country);
		
		affiliates.get(4).getAffiliateDetails().getAddress().setCountry(country);
		flush();
		
		List<Affiliate> resultList = search("ZZZimba");
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}

	@Test
	public void findAffiliateByEmailDomain() throws Exception {
		
		affiliates.get(4).getAffiliateDetails().setEmail("fred22@exampleZZZ.com");
		flush();
		
		List<Affiliate> resultList = search("exampleZZZ.com");
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}
	
	@Test
	public void findAffiliateByEmailAddress() throws Exception {
		
		affiliates.get(4).getAffiliateDetails().setEmail("fred22@exampleZZZ.com");
		flush();
		
		List<Affiliate> resultList = search("fred22@exampleZZZ.com");
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}
	
	@Test
	public void findByNameWithinMatchingMemberOnlyShowsNameMatches() throws Exception {
		
		affiliates.get(4).getAffiliateDetails().setEmail("fred22@exampleZZZ.com");
		flush();
		
		List<Affiliate> resultList = search("fred22@exampleZZZ.com", ihtsdo);
		
		assertThat(resultList.size(), is(1));
		assertThat(resultList, contains(affiliates.get(4)));
	}
	
	/**
	 * flush to JPA + Lucene.  JPA queries trigger a JPA flush, but Hibernate Search
	 * only flushes on TX commit by default.  But are using a TX rollback test strategy, so we
	 * need to flush Lucene manually.
	 */
	private void flush() {
		entityManager.flush();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.flushToIndexes();
	}
	
	private List<Affiliate> search(String query) {
		return search(query, null);
	}

	private List<Affiliate> search(String query, Member member) {
		List<Affiliate> resultList = affiliateSearchRepository.findFullTextAndMember(query.toLowerCase(),member,new PageRequest(0, 50)).getContent();
		return resultList;
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
