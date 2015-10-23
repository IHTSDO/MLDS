package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateCheckDTO;

@RestController
public class AffiliatePublicResource {

	   private final Logger log = LoggerFactory.getLogger(AffiliatePublicResource.class);
	    
		@Resource AffiliateRepository affiliateRepository;
		@Resource MemberRepository memberRepository;

		@RequestMapping(value = Routes.AFFILIATES_CHECK,
	    		method = RequestMethod.GET,
	            produces = MediaType.APPLICATION_JSON_VALUE)
		@Timed
	    @RolesAllowed({ AuthoritiesConstants.ANONYMOUS })
	    public @ResponseBody ResponseEntity<AffiliateCheckDTO> getAffiliates(
	    		@RequestParam(value="member", defaultValue="") String memberKey,
	    		@RequestParam(value="affiliateId", defaultValue="", required=false) String affiliateId,
	    		@RequestParam(value="match", defaultValue="") String match) {
			
			AffiliateCheckDTO response = new AffiliateCheckDTO();
			
			if (StringUtils.isBlank(memberKey)) {
				return badRequest(response, "Missing mandatory parameter: member");
			}
			if (StringUtils.isBlank(match)) {
				return badRequest(response, "Missing mandatory parameter: match");
			}
			int MINIMUM_MATCH_LENGTH = 3;
			if (match.trim().length() < MINIMUM_MATCH_LENGTH) {
				return badRequest(response, "Match parameter value '"+match+"' was shorter than the minimum length: "+MINIMUM_MATCH_LENGTH);
			}

			Member member = memberRepository.findOneByKey(memberKey);
			if (member == null) {
				return badRequest(response, "Unknown member: "+memberKey+". Valid options: "+memberOptions());
			}
			if (StringUtils.isNotBlank(affiliateId)) {
				try {
					long id = Long.valueOf(affiliateId);
					Affiliate affiliate = affiliateRepository.findOne(id);
					if (affiliate == null) {
						return badRequest(response, "Unknown affiliateId: "+affiliateId);
					}
					response.setMatched(member != null && affiliate != null && nameMatches(affiliate, match) && affiliateValid(affiliate));
				} catch (NumberFormatException e) {
					return badRequest(response, "Illegal affiliateId value: "+affiliateId);
				}
			} else {
				response.setMatched(false);
			}
			
			return new ResponseEntity<AffiliateCheckDTO>(response, HttpStatus.OK);
		}

		private ResponseEntity<AffiliateCheckDTO> badRequest(AffiliateCheckDTO response, String errorMessage) {
			response.setErrorMessage(errorMessage);
			return new ResponseEntity<AffiliateCheckDTO>(response, HttpStatus.BAD_REQUEST);
		}

		private String memberOptions() {
			List<String> options = new ArrayList<String>();
			for (Member member : memberRepository.findAll()) {
				options.add(member.getKey());
			}
			Collections.sort(options);
			StringBuilder builder = new StringBuilder();
			for (String memberKey : options) {
				builder.append(memberKey);
				builder.append(' ');
			}
			return builder.toString();
		}

		private boolean affiliateValid(Affiliate affiliate) {
			return affiliate != null && (Objects.equal(affiliate.getStandingState(), StandingState.IN_GOOD_STANDING));
		}

		private boolean nameMatches(Affiliate affiliate, String match) {
			AffiliateDetails details = affiliate.getAffiliateDetails();
			if (details == null) {
				return false;
			}
			return 
				matches(details.getOrganizationName(), match)
				|| matches(details.getEmail(), match);
		}

		private boolean matches(String value, String match) {
			return StringUtils.containsIgnoreCase(value, match);
		}
		
}
