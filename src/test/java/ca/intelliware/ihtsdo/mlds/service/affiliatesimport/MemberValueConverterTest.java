package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class MemberValueConverterTest {
    @Mock
    private MemberRepository memberRepository;
    
	private MemberValueConverter fixture;
	
	private Member memberAB;
	private Member memberST;

	private LineRecord lineRecord;

	private FieldMapping fieldMapping;

	private ImportResult result;

    @Before
    public void setup() {
    	fixture = new MemberValueConverter();
    	
    	fixture.memberRepository = memberRepository;
    	
    	memberAB = new Member("AB", 1);
    	memberST = new Member("ST", 2);
    	
		lineRecord = new LineRecord(1, new String[]{"field"}, false);
		fieldMapping = new FieldMapping(2, "columnName", null, null, null);
		result = new ImportResult();
    }
    
    @Test
    public void toStringShouldReturnEmptyStringForNull() {
    	assertEquals(fixture.toString(null), "");
    }

    @Test
    public void toStringShouldReturnMember() {
    	assertEquals(fixture.toString(memberAB), "AB");
    }

    @Test
    public void toObjectShouldReturnNullForNullLookup() {
    	assertEquals(fixture.toObject(null), null);
    }

    @Test
    public void toObjectShouldReturnNullForUknownMember() {
    	assertEquals(fixture.toObject("ZZ"), null);
    }

    @Test
    public void toObjectShouldReturnMember() {
    	when(memberRepository.findOneByKey("AB")).thenReturn(memberAB);
    	
    	assertEquals(fixture.toObject("AB"), memberAB);
    }
    
    @Test
    public void validateShouldPassForKnownMember() {
    	when(memberRepository.findOneByKey("AB")).thenReturn(memberAB);
    	
    	fixture.validate("AB", lineRecord, fieldMapping, result);
    	
    	assertTrue(result.errors.isEmpty());
    }

    @Test
    public void validateShouldAddErrorForUnknownMember() {
    	when(memberRepository.findOneByKey("ZZ")).thenReturn(null);
    	
    	fixture.validate("ZZ", lineRecord, fieldMapping, result);
    	
    	assertEquals(result.errors.size(), 1);
    }
    
    @Test
    public void getOptionsShouldProvideSortedListOfOptionsAsStrings() {
    	when(memberRepository.findAll()).thenReturn(Lists.newArrayList(memberST, memberAB));
    	
    	assertEquals(fixture.getOptions(), Lists.newArrayList("AB", "ST"));
    }
}
