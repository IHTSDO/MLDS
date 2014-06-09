package ca.intelliware.ihtsdo.mlds.registration;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DomainBlacklistService {
	@Resource
	DomainBlacklistRespository domainBlacklistRespository;
	
	public boolean isDomainBlacklisted(String email) {
		String requestEmailDomain = extractDomain(email);
				
		List<DomainBlacklist> result = domainBlacklistRespository.findByDomainname(requestEmailDomain);
		
		if (result.size() > 0) {
			return true;
		}
				
		return false;
	}

	private String extractDomain(String email) {
		String[] result;
		String delimiter = "@";
		
		result = email.split(delimiter);
		
		return result[1];
	}
}
