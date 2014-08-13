package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.PersistentAuditEvent;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.ImportResult;

import com.google.common.collect.Maps;

@Service
public class AffiliateAuditEvents {

	@Resource
	AuditEventService auditEventService;

	
	public void logCreationOf(Affiliate affiliate) {
		logEvent("AFFILIATE_CREATED", affiliate);
	}

	private void logEvent(String eventType, Affiliate affiliate) {
		Map<String, String> auditData = createReleasePackageData(affiliate);
		PersistentAuditEvent auditEvent = auditEventService.createAuditEvent(eventType, auditData);
		auditEvent.setAffiliateId(affiliate.getAffiliateId());
		auditEventService.logAuditableEvent(auditEvent);
	}
	
	private Map<String, String> createReleasePackageData(Affiliate affiliate) {
		Map<String,String> auditData = Maps.newHashMap();
    	auditData.put("affiliate.creator", affiliate.getCreator());
		return auditData;
	}
	
	public void logImport(ImportResult importResult) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("import.success", Boolean.toString(importResult.isSuccess()));
		auditData.put("import.records", Long.toString(importResult.getReadRecords()));
		auditData.put("import.affiliates", Long.toString(importResult.getImportedRecords()));
		auditData.put("import.source", importResult.getSourceMemberKey());
		auditData.put("import.errors", Integer.toString(importResult.getErrors().size()));
		auditEventService.logAuditableEvent("AFFILIATE_IMPORT", auditData);
	}

	public void logExport() {
		Map<String,String> auditData = Maps.newHashMap();
		auditEventService.logAuditableEvent("AFFILIATE_EXPORT", auditData);
	}
}
