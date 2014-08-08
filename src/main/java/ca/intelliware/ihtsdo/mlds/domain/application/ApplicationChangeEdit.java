package ca.intelliware.ihtsdo.mlds.domain.application;

import ca.intelliware.ihtsdo.mlds.domain.Application;

public interface ApplicationChangeEdit {
	void applyChangeOrFail(Application original,Application updated);
}