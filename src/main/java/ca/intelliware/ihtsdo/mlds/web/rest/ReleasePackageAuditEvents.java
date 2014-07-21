package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@Service
public class ReleasePackageAuditEvents {

	public static final String RELEASE_PACKAGE_CREATED = "RELEASE_PACKAGE_CREATED";
	public static final String RELEASE_PACKAGE_DELETED = "RELEASE_PACKAGE_DELETED";

	@Resource
	AuditEventService auditEventService;

	public void logCreationOf(ReleasePackage releasePackage) {
    	Map<String, String> auditData = createReleasePackageDetails(releasePackage);
    	auditEventService.logAuditableEvent(RELEASE_PACKAGE_CREATED, auditData);
	}

	public void logDeletionOf(ReleasePackage releasePackage) {
    	Map<String, String> auditData = createReleasePackageDetails(releasePackage);
    	auditEventService.logAuditableEvent(RELEASE_PACKAGE_DELETED, auditData);
	}

	private Map<String, String> createReleasePackageDetails(ReleasePackage releasePackage) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("releasePackage.name", releasePackage.getName());
    	auditData.put("releasePackage.releasePackageId", ""+releasePackage.getReleasePackageId());
		return auditData;
	}

	public void logCreationOf(ReleaseVersion releaseVersion) {
		Map<String, String> auditData = createReleaseVersionData(releaseVersion);
		auditEventService.logAuditableEvent("RELEASE_VERSION_CREATED", auditData);
	}

	public void logReleaseVersionDeleted(ReleaseVersion releaseVersion) {
		Map<String, String> auditData = createReleaseVersionData(releaseVersion);
		auditEventService.logAuditableEvent("RELEASE_VERSION_DELETED", auditData);
	}

	private Map<String, String> createReleaseVersionData(ReleaseVersion releaseVersion) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("releaseVersion.name", releaseVersion.getName());
		auditData.put("releaseVersion.releaseVersionId", ""+releaseVersion.getReleaseVersionId());
		return auditData;
	}
	
	public void logCreationOf(ReleaseFile releaseFile) {
		Map<String, String> auditData = createReleaseFileDetails(releaseFile);
		auditEventService.logAuditableEvent("RELEASE_FILE_CREATED", auditData);
	}

	public void logDeletionOf(ReleaseFile releaseFile) {
		Map<String, String> auditData = createReleaseFileDetails(releaseFile);
		auditEventService.logAuditableEvent("RELEASE_FILE_DELETED", auditData);
	}

	private Map<String, String> createReleaseFileDetails(ReleaseFile releaseFile) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("releaseFile.label", releaseFile.getLabel());
		auditData.put("releaseFile.releaseFileId", ""+releaseFile.getReleaseFileId());
		return auditData;
	}

}
