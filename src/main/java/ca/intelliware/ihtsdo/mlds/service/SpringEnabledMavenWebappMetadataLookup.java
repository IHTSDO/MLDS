package ca.intelliware.ihtsdo.mlds.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

@Service
public class SpringEnabledMavenWebappMetadataLookup extends MavenWebappMetadataLookup implements ServletContextAware, InitializingBean {
	{
		this.setGroupId("org.ihtsdo.mlds");
		this.setArtifactId("mlds");
	}
	
	@Override
	public void afterPropertiesSet() {
		loadProperties();
	}
}