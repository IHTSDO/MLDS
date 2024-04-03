package ca.intelliware.ihtsdo.mlds.config;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration  extends MetricsConfigurerAdapter implements EnvironmentAware {

    private static final String ENV_METRICS = "metrics.";
    private static final String ENV_METRICS_GRAPHITE = "metrics.graphite.";
    private static final String PROP_JMX_ENABLED = "jmx.enabled";
    private static final String PROP_GRAPHITE_ENABLED = "enabled";
    private static final String PROP_PORT = "port";
    private static final String PROP_HOST = "host";
    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
    private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
    private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
    private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

    private final Logger logger = LoggerFactory.getLogger(MetricsConfiguration.class);

    //Metric registry no longer static
    //See https://github.com/jhipster/generator-jhipster/issues/980
    private final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    private final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();



    private Binder binder;

    @Override
    public void setEnvironment(Environment environment) {

        binder = Binder.get(environment);
    }

//    @Bean
    public MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }


//    @Bean
    public HealthCheckRegistry getHealthCheckRegistry() {
        logger.debug("Creating metrics health check registry");
        return HEALTH_CHECK_REGISTRY;
    }

    @PostConstruct
    public void init() {
        logger.debug("Registring JVM gauges");
        METRIC_REGISTRY.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        METRIC_REGISTRY.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        METRIC_REGISTRY.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        METRIC_REGISTRY.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        METRIC_REGISTRY.register(PROP_METRIC_REG_JVM_BUFFERS, new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        Boolean jmxEnabled = binder.bind(ENV_METRICS + PROP_JMX_ENABLED, Boolean.class).orElse(false);

        if(jmxEnabled){
            logger.info("Initializing Metrics JMX reporting");
            final JmxReporter jmxReporter = JmxReporter.forRegistry(METRIC_REGISTRY).build();
            jmxReporter.start();
        }
    }

    @Configuration
    @ConditionalOnClass(Graphite.class)
    public static class GraphiteRegistry implements EnvironmentAware {

        private final Logger log = LoggerFactory.getLogger(GraphiteRegistry.class);

        @Autowired
        private MetricRegistry metricRegistry;



        private Binder binder;

        @Override
        public void setEnvironment(Environment environment) {

            binder = Binder.get(environment);
        }

        @PostConstruct
        protected void init() {

            Boolean graphiteEnabled = binder.bind(ENV_METRICS_GRAPHITE + PROP_GRAPHITE_ENABLED, Boolean.class).orElse(false);
            if (graphiteEnabled) {
                log.info("Initializing Metrics Graphite reporting");


                String graphiteHost = binder.bind(ENV_METRICS_GRAPHITE + PROP_HOST, String.class).orElseThrow(() -> new IllegalArgumentException("Graphite host not configured"));
                Integer graphitePort = binder.bind(ENV_METRICS_GRAPHITE + PROP_PORT, Integer.class).orElseThrow(() -> new IllegalArgumentException("Graphite port not configured"));
                Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
                GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(graphite);
                graphiteReporter.start(1, TimeUnit.MINUTES);
            }
        }
    }
}
