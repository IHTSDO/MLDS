package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

@Service
public class MemberValueConverter extends ValueConverter {
	
	@Resource 
	MemberRepository memberRepository;

	@Override
	public Member toObject(String valueString) {
		return memberRepository.findOneByKey(valueString);
	}

	@Override
	public void validate(String valueString, LineRecord lineRecord, FieldMapping mapping, ImportResult result) {
		Member member = toObject(valueString);
		if (member == null) {
			result.addError(lineRecord, mapping, "Field value="+valueString+" not one of the recognized ISO 3166-1 alpha-2 country codes used for member");
		}
	}
	
	@Override
	public String toString(Object value) {
		if (value != null) {
			Member member = (Member) value;
			return member.getKey();
		} else {
			return "";
		}
	}
	
	@Override
	public List<String> getOptions() {
		List<String> options = new ArrayList<String>();
		for (Member member : memberRepository.findAll()) {
			options.add(member.getKey());
		}
		Collections.sort(options);
		return options;
	}

}