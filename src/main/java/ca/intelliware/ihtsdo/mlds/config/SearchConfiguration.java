package ca.intelliware.ihtsdo.mlds.config;

import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;

import liquibase.integration.spring.SpringLiquibase;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import ca.intelliware.ihtsdo.mlds.search.AngularTranslateService;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;

@Configuration
public class SearchConfiguration {
    private final Logger log = LoggerFactory.getLogger(SearchConfiguration.class);
    
	@Resource
	EntityManager entityManager;
	
	/** Make sure our db migrations are up-to-date before we try to use Hibernate to rebuild the index */
	@Resource SpringLiquibase liqubaseDependency;
	
	/** Used by FieldBridges, so must be configured before the index run */
	@Resource AngularTranslateService angularTranslateService;
	
	@PostConstruct
	public void recreateIndex() throws Exception {
		log.debug("SearchConfiguration - creating full-text index");
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		
		Future<?> indexResult = fullTextEntityManager.createIndexer().start();
		
		Futures.addCallback(JdkFutureAdapters.listenInPoolThread(indexResult), new FutureCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				log.debug("SearchConfiguration - finished creating full-text index");
			}

			@Override
			public void onFailure(Throwable t) {
				log.error("SearchConfiguration - Failed creating full-text index", t);
			}
		});
	}

}
