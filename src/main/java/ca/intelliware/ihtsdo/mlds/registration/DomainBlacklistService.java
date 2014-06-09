package ca.intelliware.ihtsdo.mlds.registration;

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
				
		return domainBlacklistRespository.findByDomainname(requestEmailDomain) != null;
	}

	private String extractDomain(String email) {
		String[] result;
		String delimiter = "@";
		
		result = email.split(delimiter);
		
		return result[1];
	}
}
