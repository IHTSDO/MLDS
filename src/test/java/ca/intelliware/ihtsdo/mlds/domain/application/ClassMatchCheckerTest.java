package ca.intelliware.ihtsdo.mlds.domain.application;

import org.junit.Test;

import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;

public class ClassMatchCheckerTest {
	ClassMatchChecker classMatchChecker = new ClassMatchChecker();

	@Test
	public void sameClassOK() {
		classMatchChecker.applyChangeOrFail(new ExtensionApplication(), new ExtensionApplication());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void differentClassFail() {
		classMatchChecker.applyChangeOrFail(new ExtensionApplication(), new PrimaryApplication());
	}

}
