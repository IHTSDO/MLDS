package ca.intelliware.ihtsdo.mlds.search;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;

public class AngularTranslateServiceSetup {
	MockServletContext mockServletContext;
	AngularTranslateService angularTranslateService;
	
	public void setup() {
		if (AngularTranslateService.getInstance() == null) {
			angularTranslateService = new AngularTranslateService();
		} else {
			angularTranslateService = AngularTranslateService.getInstance();
		}
		
		mockServletContext = new MockServletContext("src/main/webapp", new FileSystemResourceLoader());
		angularTranslateService.setServletContext(mockServletContext);
	}
}