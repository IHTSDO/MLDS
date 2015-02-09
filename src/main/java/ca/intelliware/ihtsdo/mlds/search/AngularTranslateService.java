package ca.intelliware.ihtsdo.mlds.search;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Maps;

/**
 * Make the angular-translate bundles available server-side. 
 */
@Service
public class AngularTranslateService {

	final Logger log = LoggerFactory.getLogger(AngularTranslateService.class);

	private static AngularTranslateService instance;

	@Resource
	ServletContext servletContext;
	
	@Resource
	ObjectMapper objectMapper = new ObjectMapper();
	
	Map<String,TreeNode> loaded = Maps.newHashMap();

	public static AngularTranslateService getInstance() {
		return instance;
	}
	
	public AngularTranslateService() {
		setSingletonInstance(this);
	}

	/** 
	 * record ourselves as a singleton instance for non-spring beans to use.
	 * @see TranslatedEnumFieldBridge
	 * */ 
	private static void setSingletonInstance(AngularTranslateService angularTranslateService) {
		instance = angularTranslateService;
	}

	public String lookup(Locale locale, String path) {
		TreeNode bundle = lookupBundle(locale);
		if (bundle == null && !"en".equals(locale.getLanguage())) {
			// unsupported language.  Fall back to English.
			bundle = lookupBundle(Locale.ENGLISH);
		}
		if (bundle == null) {
			throw new IllegalStateException("No bundle found for " + locale.getLanguage());
		}
		TreeNode cursor = bundle;
		String[] parts = path.split("\\.");
		for (String pathPart : parts) {
			cursor = cursor.path(pathPart);
		}
		return ((TextNode)cursor).textValue();
	}

	private TreeNode lookupBundle(Locale locale) {
		TreeNode bundle = loaded.get(locale.getLanguage());
		if (bundle == null) {
			bundle = loadBundle(locale.getLanguage());
			loaded.put(locale.getLanguage(), bundle);
		}
		return bundle;
	}

	private TreeNode loadBundle(String language) {
		URL resource;
		try {
			String resourcePath = "i18n/" + language + ".json";
			resource = servletContext.getResource(resourcePath);

			// Where are we looking, for info?
			// String contextPath = servletContext.getRealPath(File.separator); -- gave null
			String contextPath = servletContext.getContextPath();
			if (resource == null) {
				log.warn("Failed to find " + resourcePath + " while searching " + contextPath);
				return null;
			} else {
				log.info("Successfully located Hibernate Translations at " + contextPath + File.separator + resourcePath);
			}
			return objectMapper.readTree(resource);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Misconfigured", e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Misconfigured", e);
		} catch (IOException e) {
			throw new RuntimeException("Misconfigured or unsupported language", e);
		}
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
