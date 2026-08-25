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
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MetricRegistry metricRegistry;

    @PreDestroy
    public void destroy() {
        log.info("Remove Cache Manager metrics");

        SortedSet<String> names = metricRegistry.getNames();
        for (String name : names) {
            metricRegistry.remove(name);
        }
    }

    @Bean
    public CacheManager cacheManager(Environment env) {
        log.debug("Starting Caffeine cache manager");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        int cacheTimeToLiveSeconds =
            env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600);

        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(cacheTimeToLiveSeconds, TimeUnit.SECONDS)
        );

        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        for (EntityType<?> entity : entities) {
            String name = entity.getName();

            if (name == null && entity.getJavaType() != null) {
                name = entity.getJavaType().getName();
            }

            Assert.notNull(name, "entity cannot exist without an identifier");

            CaffeineCache caffeineCache = new CaffeineCache(
                name,
                Caffeine.newBuilder()
                    .expireAfterWrite(
                        cacheTimeToLiveSeconds,
                        TimeUnit.SECONDS
                    )
                    .build()
            );

            cacheManager.registerCustomCache(
                name,
                caffeineCache.getNativeCache()
            );
        }

        log.info("Cache Manager Build Completed");

        return cacheManager;
    }
}
