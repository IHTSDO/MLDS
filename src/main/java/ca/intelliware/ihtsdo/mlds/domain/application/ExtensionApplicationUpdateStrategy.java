package ca.intelliware.ihtsdo.mlds.domain.application;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;

public class ExtensionApplicationUpdateStrategy implements ApplicationChangeEdit {
	@Override
	public void applyChangeOrFail(Application original, Application updated) {
		if (original instanceof ExtensionApplication) {
			ExtensionApplication updatedExtensionApplication = (ExtensionApplication) updated;
			ExtensionApplication extensionApplication = (ExtensionApplication) original;
			extensionApplication.setReason(updatedExtensionApplication.getReason());
		}
	}
	
}