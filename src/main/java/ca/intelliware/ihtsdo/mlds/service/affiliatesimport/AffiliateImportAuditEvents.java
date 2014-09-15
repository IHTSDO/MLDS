package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

import com.google.common.collect.Maps;

@Service
public class AffiliateImportAuditEvents {
	static final String EVENT_AFFILIATE_EXPORT = "AFFILIATE_EXPORT";
	static final String EVENT_AFFILIATE_IMPORT = "AFFILIATE_IMPORT";
	
	@Resource
	AuditEventService auditEventService;
	
	public void logImport(ImportResult importResult) {
		Map<String,String> auditData = Maps.newHashMap();
		auditData.put("import.success", Boolean.toString(importResult.isSuccess()));
		auditData.put("import.rows", Long.toString(importResult.getReadRows()));
		auditData.put("import.affiliates", Long.toString(importResult.getImportedRecords()));
		auditData.put("import.newAffiliates", Long.toString(importResult.getNewRecords()));
		auditData.put("import.updatedAffiliates", Long.toString(importResult.getUpdatedRecords()));
		auditData.put("import.source", importResult.getSourceMemberKey());
		auditData.put("import.errors", Integer.toString(importResult.getErrors().size()));
		auditEventService.logAuditableEvent(EVENT_AFFILIATE_IMPORT, auditData);
	}

	public void logExport() {
		Map<String,String> auditData = Maps.newHashMap();
		auditEventService.logAuditableEvent(EVENT_AFFILIATE_EXPORT, auditData);
	}
	
}
