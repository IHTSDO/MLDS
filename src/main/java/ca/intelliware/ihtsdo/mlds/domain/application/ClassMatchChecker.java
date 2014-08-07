package ca.intelliware.ihtsdo.mlds.domain.application;

import ca.intelliware.ihtsdo.mlds.domain.Application;

public class ClassMatchChecker implements ApplicationChangeEdit{
	@Override
	public void applyChangeOrFail(Application original, Application updated) {
		if (original.getClass() != updated.getClass()) {
			throw new IllegalArgumentException("Can't change applicationType.");
		}
	}
	
}