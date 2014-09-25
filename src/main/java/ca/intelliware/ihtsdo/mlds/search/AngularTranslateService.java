package ca.intelliware.ihtsdo.mlds.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

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
	private static AngularTranslateService instance;

	@Resource
	ServletContext servletContext;
	
	@Resource
	ObjectMapper objectMapper;
	
	Map<String,TreeNode> loaded = Maps.newHashMap();

	public static AngularTranslateService getInstance() {
		return instance;
	}
	
	public AngularTranslateService() {
		// record ourselves as a singleton instance for non-spring beans to use
		instance = this;
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
			resource = servletContext.getResource("i18n/" + language + ".json");
			if (resource == null) {
				return null;
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

}
