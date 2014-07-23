package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@Service
public class ReleasePackageAuditEvents {

	@Resource
	AuditEventService auditEventService;

	//////////////////////////////////////////////////////////////////////////////////////
	// Release Package
	
	public void logCreationOf(ReleasePackage releasePackage) {
		logReleasePackageEvent("RELEASE_PACKAGE_CREATED", releasePackage);
	}

	public void logDeletionOf(ReleasePackage releasePackage) {
		logReleasePackageEvent("RELEASE_PACKAGE_DELETED", releasePackage);
	}

	private void logReleasePackageEvent(String eventType, ReleasePackage releasePackage) {
		Map<String, String> auditData = createReleasePackageData(releasePackage);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleasePackageId(releasePackage.getReleasePackageId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleasePackageData(ReleasePackage releasePackage) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("releasePackage.name", releasePackage.getName());
		return auditData;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// Release Version

	public void logCreationOf(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent("RELEASE_VERSION_CREATED", releaseVersion);
	}

	public void logDeletionOf(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent("RELEASE_VERSION_DELETED", releaseVersion);
	}

	public void logTakenOnline(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent("RELEASE_VERSION_TAKEN_ONLINE", releaseVersion);
	}

	public void logTakenOffline(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent("RELEASE_VERSION_TAKEN_OFFLINE", releaseVersion);
	}

	private void logReleaseVersionEvent(String eventType, ReleaseVersion releaseVersion) {
		Map<String, String> auditData = createReleaseVersionData(releaseVersion);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleaseVersionId(releaseVersion.getReleaseVersionId());
		auditEvent.setReleasePackageId(releaseVersion.getReleasePackage().getReleasePackageId());
		auditEventService.logAuditableEvent(auditEvent);
	}

	private Map<String, String> createReleaseVersionData(ReleaseVersion releaseVersion) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("releaseVersion.name", releaseVersion.getName());
		return auditData;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// Release File

	public void logCreationOf(ReleaseFile releaseFile) {
		logReleaseFileEvent("RELEASE_FILE_CREATED", releaseFile);
	}

	public void logDeletionOf(ReleaseFile releaseFile) {
		logReleaseFileEvent("RELEASE_FILE_DELETED", releaseFile);
	}

	private void logReleaseFileEvent(String eventType, ReleaseFile releaseFile) {
		Map<String, String> auditData = createReleaseFileData(releaseFile);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleaseFileId(releaseFile.getReleaseFileId());
		auditEvent.setReleaseVersionId(releaseFile.getReleaseVersion().getReleaseVersionId());
		auditEvent.setReleasePackageId(releaseFile.getReleaseVersion().getReleasePackage().getReleasePackageId());
		auditEventService.logAuditableEvent(auditEvent);
	}

	private Map<String, String> createReleaseFileData(ReleaseFile releaseFile) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("releaseFile.label", releaseFile.getLabel());
		return auditData;
	}
}
