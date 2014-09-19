package ca.intelliware.ihtsdo.mlds.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup our webapp maven metadata to include on pages for debugging.
 */
public class MavenWebappMetadataLookup implements ServletContextListener{
    final Logger log = LoggerFactory.getLogger(UserService.class);
    
	ServletContext servletContext;
	String artifactId;
	String groupId;
	String text = "not loaded";
	Properties properties = new Properties();
	
	public void contextDestroyed(ServletContextEvent ctx) {
	}

	public void contextInitialized(ServletContextEvent ctx) {
		setServletContext(ctx.getServletContext());
		loadProperties();
	}

	void loadProperties() {
		if (servletContext == null) {
			return;
		}
		InputStream resourceAsStream = getPropertiesStream();
		if (resourceAsStream == null) {
			this.text = "Failed to lookup metadata for " + groupId +"/" + artifactId + " : not found";
			// Missing?  Are we running in development?
			log.error(text);
		} else {
			try {
				this.text = IOUtils.toString(resourceAsStream, "ISO-8859-1");
				properties.load(getPropertiesStream());
				log.debug("Loaded maven info for {} {} OK.", groupId, artifactId);
			} catch (IOException e) {
				this.text = "Failed to lookup metadata for " + groupId +"/" + artifactId + " due to "+ e;
				log.error(text, e);
			}
		}
	}

	private InputStream getPropertiesStream() {
		return servletContext.getResourceAsStream("/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getText() {
		return text;
	}

	public Properties getProperties() {
		return (Properties) properties.clone();
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}