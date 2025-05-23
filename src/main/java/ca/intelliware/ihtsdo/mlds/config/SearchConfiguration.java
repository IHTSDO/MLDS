package ca.intelliware.ihtsdo.mlds.config;

import java.util.concurrent.Future;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import ca.intelliware.ihtsdo.mlds.search.AngularTranslateService;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.MoreExecutors;

@Configuration
public class SearchConfiguration {
    private final Logger log = LoggerFactory.getLogger(SearchConfiguration.class);

    @Resource
    EntityManagerFactory entityManagerFactory;

    /** Make sure our db migrations are up-to-date before we try to use Hibernate to rebuild the index */
    @Resource SpringLiquibase liqubaseDependency;

    /** Used by FieldBridges, so must be configured before the index run */
    @Resource AngularTranslateService angularTranslateService;

    @PostConstruct
    public void recreateIndex() {
        log.debug("SearchConfiguration - creating full-text index");

        SearchSession fullTextEntityManager = Search.session(entityManagerFactory.createEntityManager());

        Future<?> indexResult = fullTextEntityManager.massIndexer().start().toCompletableFuture();

        Futures.addCallback(JdkFutureAdapters.listenInPoolThread(indexResult), new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                log.debug("SearchConfiguration - finished creating full-text index");
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("SearchConfiguration - Failed creating full-text index", t);
            }
        }, MoreExecutors.directExecutor());
    }
}
