
package ca.intelliware.ihtsdo.mlds.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.search.AngularTranslateService;

@Configuration
public class SearchConfiguration {

    private final Logger log = LoggerFactory.getLogger(SearchConfiguration.class);

    public static final String COMPLETION_MARKER = ".index-complete";
    private static final String REINDEX_STRATEGY_CONDITIONAL = "conditional";
    private static final String REINDEX_STRATEGY_ALWAYS = "always";
    private static final String REINDEX_STRATEGY_DISABLED = "false";
    @Resource
    EntityManagerFactory entityManagerFactory;

    /**
     * Make sure our db migrations are up-to-date before we try to use Hibernate to rebuild the index.
     */
    @Resource
    SpringLiquibase liqubaseDependency;

    /**
     * Used by FieldBridges, so must be configured before the index run.
     */
    @Resource
    AngularTranslateService angularTranslateService;

    @Value("${mlds.search.reindex-on-startup:conditional}")
    private String reindexOnStartup;

    @Value("${spring.jpa.properties.hibernate.search.backend.directory.root}")
    private String indexDirectoryPath;

    @Value("${mlds.search.mass-indexer.threads-to-load:2}")
    private int threadsToLoad;

    @Value("${mlds.search.mass-indexer.batch-size:50}")
    private int batchSize;

    @Value("${mlds.search.mass-indexer.id-fetch-size:100}")
    private int idFetchSize;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        checkAndRebuildIndex();
    }

    public synchronized void checkAndRebuildIndex() {
        String strategy = getReindexStrategy();

        if (REINDEX_STRATEGY_DISABLED.equals(strategy)) {
            log.info("Hibernate Search startup reindexing is disabled ('mlds.search.reindex-on-startup=false').");
            return;
        }

        File markerFile = new File(indexDirectoryPath, COMPLETION_MARKER);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        boolean asyncCleanupRegistered = false;

        try {
            SearchSession searchSession = Search.session(entityManager);

            if (shouldReindex(strategy, searchSession, entityManager, markerFile)) {
                executeReindex(searchSession, entityManager, markerFile);
                asyncCleanupRegistered = true;
            }
        } catch (Exception e) {
            log.error("SearchConfiguration - Error during startup reindex check", e);
        } finally {
            if (!asyncCleanupRegistered) {
                closeEntityManager(entityManager);
            }
        }
    }

    private String getReindexStrategy() {
        String strategy = reindexOnStartup != null
            ? reindexOnStartup.trim().toLowerCase(Locale.ROOT)
            : REINDEX_STRATEGY_CONDITIONAL;

        if (REINDEX_STRATEGY_CONDITIONAL.equals(strategy)
            || REINDEX_STRATEGY_ALWAYS.equals(strategy)
            || REINDEX_STRATEGY_DISABLED.equals(strategy)) {
            return strategy;
        }

        log.warn(
            "Unknown mlds.search.reindex-on-startup value '{}'. Falling back to '{}'.",
            reindexOnStartup,
            REINDEX_STRATEGY_CONDITIONAL
        );

        return REINDEX_STRATEGY_CONDITIONAL;
    }

    private boolean shouldReindex(
        String strategy,
        SearchSession searchSession,
        EntityManager entityManager,
        File markerFile) {

        if (REINDEX_STRATEGY_ALWAYS.equals(strategy)) {
            log.info("Hibernate Search reindexing triggered with strategy: 'always'");
            return true;
        }

        return isIndexIncomplete(searchSession, entityManager, markerFile);
    }

    private boolean isIndexIncomplete(
        SearchSession searchSession,
        EntityManager entityManager,
        File markerFile) {

        if (!Files.exists(markerFile.toPath())) {
            log.info(
                "Hibernate Search completion marker ({}) not found (initial deployment or previous run was interrupted). Triggering full reindex.",
                COMPLETION_MARKER
            );
            return true;
        }

        return isIndexCountMismatch(searchSession, entityManager);
    }

    private boolean isIndexCountMismatch(
        SearchSession searchSession,
        EntityManager entityManager) {

        try {
            long indexCount = searchSession.search(Affiliate.class)
                .where(f -> f.matchAll())
                .fetchTotalHitCount();

            long dbCount = entityManager.createQuery(
                    "select count(a) from Affiliate a", Long.class)
                .getSingleResult();

            return validateIndexCounts(indexCount, dbCount);

        } catch (Exception e) {
            log.warn(
                "Hibernate Search index query check failed ({}). Triggering full reindex.",
                e.getMessage()
            );
            return true;
        }
    }

    private boolean validateIndexCounts(long indexCount, long dbCount) {
        if (indexCount == 0 && dbCount > 0) {
            log.info(
                "Completion marker exists but Lucene index is empty (index=0, db={}). Triggering full reindex.",
                dbCount
            );
            return true;
        }

        if (indexCount != dbCount) {
            log.warn(
                "Hibernate Search index count ({}) does not match active database count ({}). Possible DB restore or data drift. Triggering reindex.",
                indexCount,
                dbCount
            );
            return true;
        }

        log.info(
            "Hibernate Search index is complete (marker verified) and matches DB count ({} documents). Skipping startup reindex.",
            indexCount
        );

        return false;
    }

    /**
     * Executes manual reindexing asynchronously.
     */
    public CompletableFuture<Void> triggerManualReindex() throws IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            File markerFile = new File(indexDirectoryPath, COMPLETION_MARKER);
            SearchSession searchSession = Search.session(entityManager);

            return executeReindex(searchSession, entityManager, markerFile);

        } catch (IOException | RuntimeException e) {
            closeEntityManager(entityManager);
            throw e;
        }
    }

    /**
     * Executes mass indexing asynchronously with resource limits and writes
     * the completion marker upon success.
     */
    private CompletableFuture<Void> executeReindex(
        SearchSession searchSession,
        EntityManager entityManager,
        File markerFile) throws IOException {

        Files.deleteIfExists(markerFile.toPath());

        log.info(
            "SearchConfiguration - creating full-text index for Affiliate (threads={}, batchSize={}, idFetchSize={})",
            threadsToLoad,
            batchSize,
            idFetchSize
        );

        CompletableFuture<Void> indexResult = searchSession.massIndexer(Affiliate.class)
            .threadsToLoadObjects(threadsToLoad)
            .batchSizeToLoadObjects(batchSize)
            .idFetchSize(idFetchSize)
            .start()
            .thenAccept(result -> {
                // Indexing completed successfully.
            })
            .toCompletableFuture();
        return indexResult.whenComplete((result, throwable) ->
            handleIndexCompletion(entityManager, markerFile, throwable)
        );
    }

    private void handleIndexCompletion(
        EntityManager entityManager,
        File markerFile,
        Throwable throwable) {

        closeEntityManager(entityManager);

        if (throwable == null) {
            createCompletionMarker(markerFile);
            return;
        }

        deleteCompletionMarker(markerFile);
        log.error("SearchConfiguration - Failed creating full-text index", throwable);
    }

    private void closeEntityManager(EntityManager entityManager) {
        try {
            entityManager.close();
        } catch (Exception e) {
            log.warn("SearchConfiguration - Failed to close EntityManager", e);
        }
    }

    private void createCompletionMarker(File markerFile) {
        try {
            Path markerPath = markerFile.toPath();
            Path parent = markerPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.createFile(markerPath);

            log.info(
                "SearchConfiguration - finished creating full-text index. Created completion marker: {}",
                markerFile.getAbsolutePath()
            );

        } catch (IOException e) {
            log.error(
                "SearchConfiguration - index finished but failed to write completion marker",
                e
            );
        }
    }

    private void deleteCompletionMarker(File markerFile) {
        try {
            Files.deleteIfExists(markerFile.toPath());
        } catch (IOException e) {
            log.error(
                "SearchConfiguration - Failed to delete completion marker: {}",
                markerFile.getAbsolutePath(),
                e
            );
        }
    }
}
