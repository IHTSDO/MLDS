package ca.intelliware.ihtsdo.mlds.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Lists;

import java.util.List;

public class CommercialUsageCollection {
    private List<CommercialUsage> applications;

    public CommercialUsageCollection(Iterable<CommercialUsage> applications) {
        super();
        this.applications = Lists.newArrayList(applications);
    }

    @JsonValue
    public CommercialUsage[] getApplications() {
        return applications.toArray(new CommercialUsage[0]);
    }
}
