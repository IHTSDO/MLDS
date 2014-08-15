package ca.intelliware.ihtsdo.mlds.developer;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.EntityContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermTermination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.Application;
import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class HibernateSearchTest {
	@Resource
	EntityManager entityManager;
	
	@Test
	public void findAffiliate() throws Exception {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Affiliate.class).get();
		Query query = queryBuilder.keyword().onField("ALL").matching("intelliware michael").createQuery();
		
		FullTextQuery ftQuery = fullTextEntityManager.createFullTextQuery(query, Affiliate.class);
		List resultList = ftQuery.getResultList();
		
		System.out.println("matching affiliate" + resultList);
		
	}

}
