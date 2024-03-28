package ca.intelliware.ihtsdo.mlds.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


import ca.intelliware.ihtsdo.mlds.web.filter.CachingHttpHeadersFilter;
import ca.intelliware.ihtsdo.mlds.web.filter.StaticResourcesProductionFilter;
import ca.intelliware.ihtsdo.mlds.web.filter.gzip.GZipServletFilter;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.metrics.servlet.InstrumentedFilter;
import io.dropwizard.metrics.servlets.MetricsServlet;
import jakarta.servlet.*;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
//import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;




import com.fasterxml.jackson.databind.Module;


/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
@AutoConfigureAfter(CacheConfiguration.class)
public class WebConfigurer implements ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    @Autowired
    private Environment env;

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    ApplicationContext applicationContext;


    /**
     * Wire up the Jackson serialization for Jodatime
     * @return
     */
//    @Bean
//    public Module jodaModule() {
//        return new JodaModule();
//    }


    // you can run this with SSL/TLS. For example, build the application (`mvn clean install`) in the `oauth` directory, then run:
    //   java -Dspring.profiles.active=production -Dserver.keystore.file=file:///`pwd`/src/main/resources/keystore.p12 -jar target/oauth-1.0.0.BUILD-SNAPSHOT.jar
    @Bean
    @Profile("ssl")
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer(
            @Value("${keystore.file}") Resource keystoreFile,
            @Value("${keystore.pass}") final String keystorePass) throws Exception {
        final String absoluteKeystoreFile = keystoreFile.getFile().getAbsolutePath();

        log.info("Web application configuration, SSL keystore: {}", absoluteKeystoreFile);

        return (TomcatServletWebServerFactory factory) -> {
            factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
                connector.setPort(8443);
                connector.setSecure(true);
                connector.setScheme("https");

                Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                proto.setSSLEnabled(true);
//             proto.setKeystoreFile(absoluteKeystoreFile);
//             proto.setKeystorePass(keystorePass);
                //proto.setKeystoreType("jks");
                //proto.setKeyAlias("tomcat");
            });
        };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        initMetrics(servletContext, disps);
//        if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT))) {
            initStaticResourcesProductionFilter(servletContext, disps);
            initCachingHttpHeadersFilter(servletContext, disps);
        }
        initGzipFilter(servletContext, disps);

        log.info("Web application fully configured");
    }

    /**
     * Initializes the GZip filter.
     */
    private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering GZip Filter");

        FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
        Map<String, String> parameters = new HashMap<>();

        compressingFilter.setInitParameters(parameters);

        compressingFilter.addMappingForUrlPatterns(disps, true, "*.css");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.json");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.html");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.js");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/api/*");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/metrics/*");

        compressingFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the static resources production Filter.
     */
    private void initStaticResourcesProductionFilter(ServletContext servletContext,
                                                     EnumSet<DispatcherType> disps) {

        log.debug("Registering static resources production Filter");
        FilterRegistration.Dynamic staticResourcesProductionFilter =
                servletContext.addFilter("staticResourcesProductionFilter",
                        new StaticResourcesProductionFilter().toString());

        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/index.html");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/tos.html");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/privacyPolicy.html");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/images/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/fonts/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/styles/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/views/*");
        staticResourcesProductionFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the cachig HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> disps) {
        log.debug("Registering Cachig HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
                servletContext.addFilter("cachingHttpHeadersFilter",
                        new CachingHttpHeadersFilter().toString());

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/images/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/fonts/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/styles/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
                metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
                metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
               new InstrumentedFilter());
        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        log.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet =
                servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/metrics/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }

    @Bean
    public SimpleUrlHandlerMapping myFaviconHandlerMapping()
    {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico",
                myFaviconRequestHandler()));
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler myFaviconRequestHandler()
    {
        ResourceHttpRequestHandler requestHandler =
                new ResourceHttpRequestHandler();
        requestHandler.setLocations(Arrays
                .<Resource> asList(applicationContext.getResource("/")));
        requestHandler.setCacheSeconds(0);
        return requestHandler;
    }
}
