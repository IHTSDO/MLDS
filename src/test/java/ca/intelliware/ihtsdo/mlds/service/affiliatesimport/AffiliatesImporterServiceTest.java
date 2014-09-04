package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.ImportApplication;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ca.intelliware.ihtsdo.mlds.Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class AffiliatesImporterServiceTest {
	@Resource AffiliatesImporterService affiliatesImporterService;
	@Resource AffiliatesMapper affiliatesMapper;
	@Resource AffiliateRepository affiliateRepository;
	@Resource MemberRepository memberRepository;
	
	Member sweden;
	private List<String> fields;
	
	@Before
	public void setup() {
		sweden = memberRepository.findOneByKey("SE");

		fields = createEmptyFieldsList();
		setField(fields,"member", "SE");
		setField(fields,"importKey", "XXX_Test");
	}
	
	@Test
	public void firstRunCreatesAffiliate() throws Exception {
		// setup
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		// execution
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		Assert.assertEquals("Our Name", foundAffiliate.getAffiliateDetails().getOrganizationName());
	}
	
	@Test
	public void secondRunUpdatesAffiliateDetails() throws Exception {
		// setup
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		// execution #1
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		affiliateRepository.flush();
		Affiliate firstAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		// execution #2
		setField(fields,"organizationName", "Our New Name");
		LineRecord record2 = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record2, new ImportResult());

		Affiliate secondAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		Assert.assertEquals("same affiliate", firstAffiliate.getAffiliateId(), secondAffiliate.getAffiliateId());
		
		Assert.assertEquals("organizationName changed", "Our New Name", secondAffiliate.getAffiliateDetails().getOrganizationName());
	}

	@Test
	public void updateAddsAnImportApplicationAsLog() throws Exception {
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		// execution #1
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		affiliateRepository.flush();
		
		// execution #2
		setField(fields,"organizationName", "Our New Name");
		LineRecord record2 = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record2, new ImportResult());

		// validation
		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		Assert.assertEquals("Has 2 applications - (primary and import)", 2, Iterables.size(foundAffiliate.getApplications()));
		
		Iterable<ImportApplication> importApplications = Iterables.filter(foundAffiliate.getApplications(), ImportApplication.class);
		Assert.assertEquals("Has an import application", 1, Iterables.size(importApplications));
		
		Assert.assertEquals("Our New Name", importApplications.iterator().next().getAffiliateDetails().getOrganizationName());
	}

	@Test
	public void shouldCreateNotSubmittedUsageOnInitialImport() throws Exception {
		// setup
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		// execution
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		Assert.assertEquals("should have created commercial usage", 1, foundAffiliate.getCommercialUsages().size());
		CommercialUsage usage = foundAffiliate.getCommercialUsages().iterator().next();
		Assert.assertEquals(ApprovalState.NOT_SUBMITTED, usage.getApprovalState());
	}

	private void setField(List<String> fields, final String columnName, String value) {
		fields.set(columnIndexFor(columnName), value);
	}

	private int columnIndexFor(final String columnName) {
		Predicate<FieldMapping> columnNameMatchPredicate = new Predicate<FieldMapping>() {
			String targetColumnName = columnName;
			
			@Override
			public boolean apply(FieldMapping input) {
				return input.columnName.equals(targetColumnName);
			}
		};
		
		return Iterables.indexOf(affiliatesMapper.getMappings(), columnNameMatchPredicate);
	}

	private List<String> createEmptyFieldsList() {
		List<String> fields = Lists.newArrayList();
		List<FieldMapping> mappings = affiliatesMapper.getMappings();
		for (int i = 0; i < mappings.size(); i++) {
			fields.add("");
		}
		return fields;
	}
}
