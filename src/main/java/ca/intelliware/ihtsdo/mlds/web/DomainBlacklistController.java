package ca.intelliware.ihtsdo.mlds.web;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklist;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistRespository;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistService;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

@Controller
public class DomainBlacklistController {

	@Resource
	DomainBlacklistRespository domainBlacklistRespository;
	
	@Resource
	DomainBlacklistService domainBlacklistService;
	
	@RolesAllowed({ AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@RequestMapping(value="api/domain-blacklist")
	public @ResponseBody Iterable<DomainBlacklist> getDomainBlacklist() {
		return domainBlacklistRespository.findAll();
	}
	
	@RolesAllowed({ AuthoritiesConstants.ADMIN })
	@RequestMapping(value="api/domain-blacklist/create", method=RequestMethod.POST)
	public Object addDomainToBlacklist(@RequestParam String domain) {
		
		System.out.println(domain);
		
		DomainBlacklist newDomain = new DomainBlacklist();
		
		newDomain.setDomainname(domain);
		
		domainBlacklistRespository.save(newDomain);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RolesAllowed({ AuthoritiesConstants.ADMIN })
	@RequestMapping(value="api/domain-blacklist/remove", method=RequestMethod.POST)
	public Object removeDomainFromBlacklist(@RequestParam String domain) {
		domainBlacklistRespository.delete(domainBlacklistRespository.findByDomainname(domain));
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
