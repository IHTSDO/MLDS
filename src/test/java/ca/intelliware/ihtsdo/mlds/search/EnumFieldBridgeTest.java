package ca.intelliware.ihtsdo.mlds.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;

public class EnumFieldBridgeTest {
	TranslatedEnumFieldBridge affiliateTypeFieldBridge = new AffiliateTypeFieldBridge();
	
	@Before
	public void setup() {
		new AngularTranslateServiceSetup().setup();
	}
	
	@Test
	public void enumValueReturnsString() throws Exception {
		
		String text = affiliateTypeFieldBridge.objectToString(AffiliateType.ACADEMIC);
		
		assertNotNull(text);
		assertEquals("Academic", text);
	}
	
	@Test
	public void nullYieldsNull() throws Exception {
		
		String text = affiliateTypeFieldBridge.objectToString(null);
		
		assertNull(text);
	}

	@Test
	public void differentEnumGiveDifferentValues() throws Exception {
		
		String text = affiliateTypeFieldBridge.objectToString(AffiliateType.ACADEMIC);
		String text2 = affiliateTypeFieldBridge.objectToString(AffiliateType.COMMERCIAL);
		
		assertNotEquals(text, text2);
	}
	

}
