package ca.intelliware.ihtsdo.mlds.developer;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
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
public class HibernateSearchIndexer {
	@Resource
	EntityManager entityManager;
	
	@Test
	public void recreateIndex() throws Exception {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		/*
		Query query = entityManager.createQuery("select a from Affiliate a");
		List<Affiliate> affiliates = query.getResultList();
		for (Affiliate affiliate : affiliates) {
			System.out.println(affiliate.getAffiliateDetails().getOrganizationName());
			fullTextEntityManager.index(affiliate);
		}*/
		fullTextEntityManager.createIndexer().startAndWait();
	}

}
