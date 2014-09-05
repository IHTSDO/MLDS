package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.HashMap;
import java.util.Map;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;

public class AuditDataBuilder {
	final Map<String, String> auditData;

	public AuditDataBuilder(Map<String, String> auditData) {
		this.auditData = auditData;
	}

	/**
	 * Start building an map for use as audit data -- creates the underlying hashmap
	 * @return
	 */
	public static AuditDataBuilder start() {
		return new AuditDataBuilder(new HashMap<String,String>());
	}
	
	/**
	 * Wrap an existing Map to add properties
	 * @param auditData
	 * @return
	 */
	public static AuditDataBuilder wrap(Map<String, String> auditData) {
		return new AuditDataBuilder(auditData);
	}

	public AuditDataBuilder addReleaseVersionName(ReleaseVersion releaseVersion) {
		if (releaseVersion != null) {
			auditData.put("releaseVersion.name", releaseVersion.getName());
		}
		return this;
	}
	
	public AuditDataBuilder addReleasePackageName(ReleasePackage releasePackage) {
		if (releasePackage != null) {
			auditData.put("releasePackage.name", releasePackage.getName());
		}
		return this;
	}

	public AuditDataBuilder addReleaseFileLabel(ReleaseFile releaseFile) {
		if (releaseFile != null) {
			auditData.put("releaseFile.label", releaseFile.getLabel());
		}
		return this;
	}

	public AuditDataBuilder addAffiliateCreator(Affiliate affiliate) {
		if (affiliate != null) {
	    	auditData.put("affiliate.creator", affiliate.getCreator());
		}
		return this;
	}

	public AuditDataBuilder addAffiliateId(Affiliate affiliate) {
		if (affiliate != null) {
	    	auditData.put("affiliate.affiliateId", ""+affiliate.getAffiliateId());
		}
		return this;
	}

	public AuditDataBuilder addAffiliateHomeMember(Affiliate affiliate) {
		if (affiliate != null) {
	    	auditData.put("affiliate.homeMember", ""+affiliate.getHomeMemberKey());
		}
		return this;
	}

	public Map<String,String> toAuditData() {
		return auditData;
	}

}