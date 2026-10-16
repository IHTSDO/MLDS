package ca.intelliware.ihtsdo.mlds.search;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AngularTranslateService {

    private static final Logger log =
        LoggerFactory.getLogger(AngularTranslateService.class);

    private static AngularTranslateService instance;

    @Resource
    ServletContext servletContext;

    @Resource
    ObjectMapper objectMapper;

    private final Map<String, TreeNode> loaded = new HashMap<>();

    public static AngularTranslateService getInstance() {
        return instance;
    }

    public AngularTranslateService() {
        setSingletonInstance(this);
    }

    private static void setSingletonInstance(
        AngularTranslateService angularTranslateService) {
        instance = angularTranslateService;
    }

    public String lookup(Locale locale, String path) {
        TreeNode bundle = lookupBundle(locale);

        if (bundle == null && !"en".equals(locale.getLanguage())) {
            // Unsupported language. Fall back to English.
            bundle = lookupBundle(Locale.ENGLISH);
        }

        if (bundle == null) {
            throw new IllegalStateException(
                "No bundle found for "
                    + locale.getLanguage()
                    + " with path '"
                    + path
                    + "'"
            );
        }

        TreeNode cursor = bundle;
        String[] parts = path.split("\\.");

        for (String pathPart : parts) {
            cursor = cursor.path(pathPart);
        }

        if (!(cursor instanceof JsonNode jsonNode)
            || !jsonNode.isString()) {
            throw new IllegalStateException(
                "Translation not found or is not a string for path '"
                    + path
                    + "'"
            );
        }

        return jsonNode.asString();
    }

    private TreeNode lookupBundle(Locale locale) {
        String language = locale.getLanguage();

        if (loaded.containsKey(language)) {
            return loaded.get(language);
        }

        TreeNode bundle = loadBundle(language);
        loaded.put(language, bundle);

        return bundle;
    }

    private TreeNode loadBundle(String language) {
        try {
            String resourcePath = "i18n/" + language + ".json";
            URL resource = servletContext.getResource(resourcePath);
            String contextPath = servletContext.getContextPath();

            if (resource == null) {
                log.warn(
                    "Failed to find {} while searching {}",
                    resourcePath,
                    contextPath
                );
                return null;
            }

            log.info(
                "Successfully located Hibernate Translations at {}{}{}",
                contextPath,
                File.separator,
                resourcePath
            );

            try (var inputStream = resource.openStream()) {
                return objectMapper.readTree(inputStream);
            }

        } catch (MalformedURLException e) {
            throw new IllegalStateException("Misconfigured URL", e);
        } catch (JacksonException e) {
            throw new IllegalStateException("Misconfigured JSON", e);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Misconfigured or unsupported language",
                e
            );
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
