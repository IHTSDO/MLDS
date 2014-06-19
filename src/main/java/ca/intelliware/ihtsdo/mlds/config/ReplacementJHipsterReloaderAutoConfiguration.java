package ca.intelliware.ihtsdo.mlds.config;

import io.github.jhipster.loaded.JHipsterPluginManagerReloadPlugin;
import io.github.jhipster.loaded.JHipsterReloaderAutoConfiguration;
import io.github.jhipster.loaded.condition.ConditionalOnSpringLoaded;
import io.github.jhipster.loaded.reloader.LiquibaseReloader;
import io.github.jhipster.loaded.reloader.Reloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springsource.loaded.agent.SpringLoadedAgent;

/**
 * A dumb clone of the real JHipsterReloaderAutoConfiguration that excludes the liquibase trouble
 * @author buckleym
 */
@Configuration
@ConditionalOnSpringLoaded
@ComponentScan(value="io.github.jhipster",excludeFilters={
		@Filter(value=LiquibaseReloader.class,type=FilterType.ASSIGNABLE_TYPE), // Exclude the liquibase generator
		@Filter(value=JHipsterReloaderAutoConfiguration.class,type=FilterType.ASSIGNABLE_TYPE) // Exclude the default reloader since it brings in the liquibase reloader
		})
public class ReplacementJHipsterReloaderAutoConfiguration implements ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(ReplacementJHipsterReloaderAutoConfiguration.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            Environment env = applicationContext.getEnvironment();
            final ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

            if (env.getProperty("hotReload.enabled", Boolean.class, false)) {
                SpringLoadedAgent.getInstrumentation();
                log.info("Spring Loaded is running, registering hot reloading features");

                final Map<String, Reloader> allReloaderClasses = applicationContext.getBeansOfType(Reloader.class);

                for (Reloader reloader : allReloaderClasses.values()) {
                    reloader.init(configurableApplicationContext);
                }

                List<Reloader> orderedReloaders = new ArrayList<>();
                orderedReloaders.addAll(allReloaderClasses.values());
                Collections.sort(orderedReloaders, new AnnotationAwareOrderComparator());

                JHipsterPluginManagerReloadPlugin.register(configurableApplicationContext, orderedReloaders,
                    JHipsterReloaderAutoConfiguration.class.getClassLoader());

            }
        } catch (UnsupportedOperationException uoe) {
            log.info("Spring Loaded is not running, hot reloading is not enabled");
        }
    }
}
