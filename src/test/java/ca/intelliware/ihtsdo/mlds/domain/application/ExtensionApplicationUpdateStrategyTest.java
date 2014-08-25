package ca.intelliware.ihtsdo.mlds.domain.application;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;

public class ExtensionApplicationUpdateStrategyTest {
	ExtensionApplication original = new ExtensionApplication();
	ExtensionApplication updated = new ExtensionApplication();
	ExtensionApplicationUpdateStrategy extensionApplicationUpdateStrategy = new ExtensionApplicationUpdateStrategy();

	@Test
	public void copyReason() {
		updated.setReason("new reason");
		
		extensionApplicationUpdateStrategy.applyChangeOrFail(original, updated);
		
		assertEquals("new reason", original.getReason());
	}
	
	@Test
	public void doNotBlowUpOnPrimary() throws Exception {
		extensionApplicationUpdateStrategy.applyChangeOrFail(new PrimaryApplication(), new PrimaryApplication());
	}

}
