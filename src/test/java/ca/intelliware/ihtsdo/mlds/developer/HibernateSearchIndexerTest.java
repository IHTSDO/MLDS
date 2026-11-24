package ca.intelliware.ihtsdo.mlds.developer;

import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import ca.intelliware.ihtsdo.mlds.config.SearchConfiguration;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class HibernateSearchIndexerTest extends MySqlTestContainerTest {
    @Resource
    EntityManager entityManager;

    @Resource
    SearchConfiguration searchConfiguration;

    @Value("${spring.jpa.properties.hibernate.search.backend.directory.root:${MLDS_SEARCH_INDEX_DIR:./search-index}}")
    private String indexDirectoryPath;

    @Test
    public void recreateIndex() throws Exception {
        SearchSession fullTextEntityManager = Search.session(entityManager);
        assertNotNull(fullTextEntityManager);

        // Trigger indexing and wait for completion
        searchConfiguration.triggerManualReindex().get(60, TimeUnit.SECONDS);

        // Verify index files and completion marker exist on disk
        File indexDir = new File(indexDirectoryPath);
        assertTrue("Index directory should exist on disk", indexDir.exists());
        assertTrue("Index directory should be a directory", indexDir.isDirectory());

        File markerFile = new File(indexDir, SearchConfiguration.COMPLETION_MARKER);
        assertTrue("Completion marker file should exist after successful reindex", markerFile.exists());

        String[] files = indexDir.list();
        assertNotNull("Index directory should contain files", files);
        assertTrue("Index directory should contain at least one Lucene file", files.length > 1);
    }
}


