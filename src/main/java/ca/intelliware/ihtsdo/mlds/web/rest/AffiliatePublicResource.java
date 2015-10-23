package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateCheckDTO;

@RestController
public class AffiliatePublicResource {

	private static final int ZERO_ONE_MANY_RESULTS_LENGTH = 3;
	private static final int MINIMUM_MATCH_LENGTH = 3;

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
			if (match.trim().length() < MINIMUM_MATCH_LENGTH) {
				return badRequest(response, "Match parameter value: '"+match+"' was shorter than the minimum length: "+MINIMUM_MATCH_LENGTH);
			}

			Member member = memberRepository.findOneByKey(memberKey);
			if (member == null) {
				return badRequest(response, "Unknown member: '"+memberKey+"'. Valid options: "+memberOptions());
			}
			
			long affilateIdOptional = AffiliateRepository.AFFILIATE_ID_OPTIONAL_VALUE;
			if (StringUtils.isNotBlank(affiliateId)) {
				try {
					// Do not try to check affiliateId to not leak valid ids
					affilateIdOptional = Long.valueOf(affiliateId);
				} catch (NumberFormatException e) {
					response.setMatched(false);
					return new ResponseEntity<AffiliateCheckDTO>(response, HttpStatus.OK);
				}
			}

			Page<Affiliate> matchingAffiliates = affiliateRepository.findForCheck(affilateIdOptional, member, match, createZeroOneManyPageRequest());
			response.setMatched(isSingleAffiliateMatch(matchingAffiliates));
			
			return new ResponseEntity<AffiliateCheckDTO>(response, HttpStatus.OK);
		}

		private boolean isSingleAffiliateMatch(Page<Affiliate> resultPage) {
			return resultPage.getNumberOfElements() == 1;
		}

		private PageRequest createZeroOneManyPageRequest() {
			// Limit number of results to tell difference between 0, 1, many results
			PageRequest pageRequest = new PageRequest(0, ZERO_ONE_MANY_RESULTS_LENGTH);
			return pageRequest;
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
}
