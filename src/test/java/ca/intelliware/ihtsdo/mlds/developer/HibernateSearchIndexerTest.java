package ca.intelliware.ihtsdo.mlds.developer;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class HibernateSearchIndexerTest {
    @Resource
    EntityManager entityManager;

    @Test
    public void recreateIndex() throws Exception {
        SearchSession fullTextEntityManager = Search.session(entityManager);
        fullTextEntityManager.toString();
        assertNotNull(fullTextEntityManager);
        }

}
