package ca.intelliware.ihtsdo.mlds.config;

import java.util.Set;
import java.util.SortedSet;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;

@Configuration
@EnableCaching
//@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Environment env;

    @Inject
    private MetricRegistry metricRegistry;

    private static net.sf.ehcache.CacheManager cacheManager;
    private static EhCacheCacheManager ehCacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Remove Cache Manager metrics");
        SortedSet<String> names = metricRegistry.getNames();
        for (String name : names) {
            metricRegistry.remove(name);
        }
        //log.info("Closing Cache Manager");
        //cacheManager.shutdown();
    }

    @Bean
    public CacheManager cacheManager() {
    	if (cacheManager != null) {
    		log.debug("Skipping creation of EHcache manager - already exists");
    	} else {
	        log.debug("Starting Ehcache");
	        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
	        net.sf.ehcache.config.CacheConfiguration cacheConfiguration = new net.sf.ehcache.config.CacheConfiguration().maxElementsInMemory(1600);
	        config.setDefaultCacheConfiguration(cacheConfiguration);
	        cacheManager = net.sf.ehcache.CacheManager.create(config);
	        log.debug("Registring Ehcache Metrics gauges");
	        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
	        for (EntityType<?> entity : entities) {
	            
	            String name = entity.getName();
	            if (name == null || entity.getJavaType() != null) {
	                name = entity.getJavaType().getName();
	            }
	            Assert.notNull(name, "entity cannot exist without a identifier");
	            
	            net.sf.ehcache.Cache cache = cacheManager.getCache(name);
	            if (cache != null) {
	                cache.getCacheConfiguration().setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600));
	                net.sf.ehcache.Ehcache decoratedCache = InstrumentedEhcache.instrument(metricRegistry, cache);
	                cacheManager.replaceCacheWithDecoratedCache(cache, decoratedCache);
	            }
	        }
	        ehCacheManager = new EhCacheCacheManager();
	        ehCacheManager.setCacheManager(cacheManager);
    	}
        return ehCacheManager;
    }
}
