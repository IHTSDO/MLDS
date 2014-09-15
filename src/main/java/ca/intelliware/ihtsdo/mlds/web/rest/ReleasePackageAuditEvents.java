package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.service.AuditDataBuilder;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@Service
public class ReleasePackageAuditEvents {

	private static final String EVENT_RELEASE_FILE_CREATED = "RELEASE_FILE_CREATED";
	private static final String EVENT_RELEASE_FILE_DELETED = "RELEASE_FILE_DELETED";
	private static final String EVENT_RELEASE_FILE_DOWNLOADED = "RELEASE_FILE_DOWNLOADED";
	private static final String EVENT_RELEASE_PACKAGE_DELETED = "RELEASE_PACKAGE_DELETED";
	private static final String EVENT_RELEASE_PACKAGE_CREATED = "RELEASE_PACKAGE_CREATED";
	private static final String EVENT_RELEASE_VERSION_TAKEN_OFFLINE = "RELEASE_VERSION_TAKEN_OFFLINE";
	private static final String EVENT_RELEASE_VERSION_TAKEN_ONLINE = "RELEASE_VERSION_TAKEN_ONLINE";
	private static final String EVENT_RELEASE_VERSION_CREATED = "RELEASE_VERSION_CREATED";
	private static final String EVENT_RELEASE_VERSION_DELETED = "RELEASE_VERSION_DELETED";
	
	@Resource
	AuditEventService auditEventService;
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Release Package
	
	public void logCreationOf(ReleasePackage releasePackage) {
		logReleasePackageEvent(EVENT_RELEASE_PACKAGE_CREATED, releasePackage);
	}

	public void logDeletionOf(ReleasePackage releasePackage) {
		logReleasePackageEvent(EVENT_RELEASE_PACKAGE_DELETED, releasePackage);
	}

	private void logReleasePackageEvent(String eventType, ReleasePackage releasePackage) {
		Map<String, String> auditData = createReleasePackageData(releasePackage);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleasePackageId(releasePackage.getReleasePackageId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleasePackageData(ReleasePackage releasePackage) {
		return AuditDataBuilder.start()
				.addReleasePackageName(releasePackage)
				.toAuditData();
	}

	//////////////////////////////////////////////////////////////////////////////////////
	// Release Version

	public void logCreationOf(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent(EVENT_RELEASE_VERSION_CREATED, releaseVersion);
	}

	public void logDeletionOf(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent(EVENT_RELEASE_VERSION_DELETED, releaseVersion);
	}

	public void logTakenOnline(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent(EVENT_RELEASE_VERSION_TAKEN_ONLINE, releaseVersion);
	}

	public void logTakenOffline(ReleaseVersion releaseVersion) {
		logReleaseVersionEvent(EVENT_RELEASE_VERSION_TAKEN_OFFLINE, releaseVersion);
	}

	private void logReleaseVersionEvent(String eventType, ReleaseVersion releaseVersion) {
		Map<String, String> auditData = createReleaseVersionData(releaseVersion);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleaseVersionId(releaseVersion.getReleaseVersionId());
		auditEvent.setReleasePackageId(releaseVersion.getReleasePackage().getReleasePackageId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleaseVersionData(ReleaseVersion releaseVersion) {
		return AuditDataBuilder.start()
			.addReleaseVersionName(releaseVersion)
			.addReleasePackageName(releaseVersion.getReleasePackage())
			.toAuditData();
	}


	

	//////////////////////////////////////////////////////////////////////////////////////
	// Release File

	public void logCreationOf(ReleaseFile releaseFile) {
		logReleaseFileEvent(EVENT_RELEASE_FILE_CREATED, releaseFile);
	}

	public void logDeletionOf(ReleaseFile releaseFile) {
		logReleaseFileEvent(EVENT_RELEASE_FILE_DELETED, releaseFile);
	}

	private void logReleaseFileEvent(String eventType, ReleaseFile releaseFile) {
		Map<String, String> auditData = createReleaseFileData(releaseFile);
		logReleaseFileEvent(eventType, releaseFile, auditData);
	}

	private void logReleaseFileEvent(String eventType, ReleaseFile releaseFile, Map<String, String> auditData) {
		PersistentAuditEvent auditEvent = createAuditEvent(eventType, releaseFile, auditData);
		auditEventService.logAuditableEvent(auditEvent);
	}

	private PersistentAuditEvent createAuditEvent(String eventType,
			ReleaseFile releaseFile, Map<String, String> auditData) {
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setReleaseFileId(releaseFile.getReleaseFileId());
		auditEvent.setReleaseVersionId(releaseFile.getReleaseVersion().getReleaseVersionId());
		auditEvent.setReleasePackageId(releaseFile.getReleaseVersion().getReleasePackage().getReleasePackageId());
		return auditEvent;
	}

	private Map<String, String> createReleaseFileData(ReleaseFile releaseFile) {
		AuditDataBuilder builder = AuditDataBuilder.start();
		builder.addReleaseFileLabel(releaseFile);
		builder.addReleaseVersionName(releaseFile.getReleaseVersion());
		builder.addReleasePackageName(releaseFile.getReleaseVersion().getReleasePackage());

		return builder.toAuditData();
	}

	public void logDownload(ReleaseFile releaseFile, int statusCode, Affiliate affiliate) {
		Map<String, String> auditData = createReleaseFileData(releaseFile);
		auditData.put("download.statusCode", Integer.toString(statusCode));
		PersistentAuditEvent auditEvent = createAuditEvent(EVENT_RELEASE_FILE_DOWNLOADED, releaseFile, auditData);
		if (affiliate != null) {
			auditEvent.setAffiliateId(affiliate.getAffiliateId());
		}
		auditEventService.logAuditableEvent(auditEvent);
	}
}
