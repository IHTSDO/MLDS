package ca.intelliware.ihtsdo.mlds.search;

import java.io.File;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;

public class AngularTranslateServiceSetup {
	MockServletContext mockServletContext;
	AngularTranslateService angularTranslateService = new AngularTranslateService();
	
	public void setup() {
		System.out.println("AngularTranslateServiceSetup - working directory is " + new File(".").getAbsolutePath());
		mockServletContext = new MockServletContext("src/main/webapp", new FileSystemResourceLoader());
		angularTranslateService.setServletContext(mockServletContext);
	}
}