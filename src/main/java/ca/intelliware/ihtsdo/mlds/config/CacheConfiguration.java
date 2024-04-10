package ca.intelliware.ihtsdo.mlds.config;

import com.codahale.metrics.MetricRegistry;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
//@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Autowired
    private MetricRegistry metricRegistry;

    private static CaffeineCacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Remove Cache Manager metrics");
        SortedSet<String> names = metricRegistry.getNames();
        for (String name : names) {
            metricRegistry.remove(name);
        }
    }

    @Bean
    public CacheManager cacheManager() {
        if (cacheManager != null) {
            log.debug("Skipping creation of Caffeine cache manager - already exists");
        } else {
            log.debug("Starting Caffeine cache manager");
            cacheManager = new CaffeineCacheManager();

            // Customize CaffeineCacheManager settings here if needed
            cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600), TimeUnit.SECONDS));

            Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
            for (EntityType<?> entity : entities) {
                String name = entity.getName();
                if (name == null || entity.getJavaType() != null) {
                    name = entity.getJavaType().getName();
                }
                Assert.notNull(name, "entity cannot exist without an identifier");

                // Register caches for entities
                cacheManager.registerCustomCache(name,
                    new CaffeineCache(name,
                        Caffeine.newBuilder()
                            .expireAfterWrite(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600), TimeUnit.SECONDS)
                            // Add more configuration options as needed
                            .build()).getNativeCache());
            }
        }
        log.info("Cache Manager Build Completed");
        return cacheManager;
    }
}
