package ca.intelliware.ihtsdo.mlds.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnumFieldBridgeTest {
	MockServletContext mockServletContext;
	AngularTranslateService angularTranslateService = new AngularTranslateService();
	
	TranslatedEnumFieldBridge affiliateTypeFieldBridge = new AffiliateTypeFieldBridge();
	
	@Before
	public void setup() {
		mockServletContext = new MockServletContext("src/main/webapp", new FileSystemResourceLoader());
		angularTranslateService.objectMapper = new ObjectMapper();
		angularTranslateService.servletContext = mockServletContext;
	}
	
	@Test
	public void enumValueReturnsString() throws Exception {
		
		String text = affiliateTypeFieldBridge.objectToString(AffiliateType.ACADEMIC);
		
		assertNotNull(text);
		assertEquals("Academic", text);
	}

	@Test
	public void differentEnumGiveDifferentValues() throws Exception {
		
		String text = affiliateTypeFieldBridge.objectToString(AffiliateType.ACADEMIC);
		String text2 = affiliateTypeFieldBridge.objectToString(AffiliateType.COMMERCIAL);
		
		assertNotEquals(text, text2);
	}
	

}
