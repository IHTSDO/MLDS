package ca.intelliware.ihtsdo.mlds.domain.json;

import java.util.List;

import ca.intelliware.ihtsdo.mlds.domain.Application;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Lists;

/** Stupid wrapper to get around type erasure and Jacksons dependence declared type to include the type tag */
public class ApplicationCollection {
	private List<Application> applications;

	public ApplicationCollection(Iterable<Application> applications) {
		super();
		this.applications = Lists.newArrayList(applications);
	}

	@JsonValue
	public Application[] getApplications() {
		return applications.toArray(new Application[0]);
	}
}