package ca.intelliware.ihtsdo.mlds.developer;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class HibernateSearchIndexer {
	@Resource
	EntityManager entityManager;
	
	@Test
	public void recreateIndex() throws Exception {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		
		//fullTextEntityManager.createIndexer().startAndWait();
		fullTextEntityManager.toString();
	}

}
