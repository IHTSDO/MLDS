package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.List;
import java.util.Set;

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
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ImportApplication;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
	
	@Before
	public void setup() {
		sweden = memberRepository.findOneByKey("SE");
	}
	
	@Test
	public void firstRunCreatesAffiliate() throws Exception {
		List<String> fields = createEmptyFieldsList();
		setField(fields,"member", "SE");
		setField(fields,"importKey", "XXX_Test");
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		Assert.assertEquals("Our Name", foundAffiliate.getAffiliateDetails().getOrganizationName());
				
	}
	
	@Test
	public void secondRunUpdatesAffiliateDetails() throws Exception {
		List<String> fields = createEmptyFieldsList();
		setField(fields,"member", "SE");
		setField(fields,"importKey", "XXX_Test");
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		setField(fields,"organizationName", "Our New Name");
		LineRecord record2 = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record2, new ImportResult());

		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		Assert.assertEquals("Our New Name", foundAffiliate.getAffiliateDetails().getOrganizationName());
	}

	@Test
	public void updateAddsAnImportApplicationAsLog() throws Exception {
		List<String> fields = createEmptyFieldsList();
		setField(fields,"member", "SE");
		setField(fields,"importKey", "XXX_Test");
		setField(fields,"organizationName", "Our Name");
		LineRecord record = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record, new ImportResult());
		
		setField(fields,"organizationName", "Our New Name");
		LineRecord record2 = new LineRecord(1, fields.toArray(new String[0]), false);
		
		affiliatesImporterService.processLineRecord(record2, new ImportResult());

		Affiliate foundAffiliate = affiliateRepository.findByImportKeyAndHomeMember("XXX_Test", sweden);
		
		Iterable<ImportApplication> importApplications = Iterables.filter(foundAffiliate.getApplications(), ImportApplication.class);
		Assert.assertEquals("Has an import application", 1, Iterables.size(importApplications));
		Assert.assertEquals("Our New Name", importApplications.iterator().next().getAffiliateDetails().getOrganizationName());
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
		for (FieldMapping fieldMapping : mappings) {
			fields.add("");
		}
		return fields;
	}
}
