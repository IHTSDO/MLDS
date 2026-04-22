package ca.intelliware.ihtsdo.mlds.web.rest;



import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklist;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistRespository;
import ca.intelliware.ihtsdo.mlds.registration.DomainBlacklistService;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class DomainBlacklistResource {

	@Resource
	DomainBlacklistRespository domainBlacklistRespository;

	@Resource
	DomainBlacklistService domainBlacklistService;

    @GetMapping(value = "api/domain-blacklist")
	@RolesAllowed({ AuthoritiesConstants.ADMIN })
	@Timed
	public @ResponseBody Iterable<DomainBlacklist> getDomainBlacklist() {
		return domainBlacklistRespository.findAll();
	}

	@RolesAllowed({ AuthoritiesConstants.ADMIN })
	@RequestMapping(value="api/domain-blacklist/create", method=RequestMethod.POST)
	@Timed
	public Object addDomainToBlacklist(@RequestParam String domain) {

		DomainBlacklist newDomain = new DomainBlacklist();

		newDomain.setDomainName(domain);

		domainBlacklistRespository.save(newDomain);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RolesAllowed({ AuthoritiesConstants.ADMIN })
	@RequestMapping(value="api/domain-blacklist/remove", method=RequestMethod.POST)
	@Timed
	public Object removeDomainFromBlacklist(@RequestParam String domain) {
		domainBlacklistRespository.deleteAll(domainBlacklistRespository.findByDomainName(domain));
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
