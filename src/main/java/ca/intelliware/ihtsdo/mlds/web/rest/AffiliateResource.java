package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateSearchRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliateImportAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesExporterService;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportGenerator;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImportSpec;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.AffiliatesImporterService;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.ImportResult;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

@RestController
public class AffiliateResource {

    private final Logger log = LoggerFactory.getLogger(AffiliateResource.class);
    
	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	AffiliateSearchRepository affiliateSearchRepository;
	
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;

	@Resource
	ApplicationAuthorizationChecker applicationAuthorizationChecker;

	@Resource
	AffiliateAuditEvents affiliateAuditEvents;

	@Resource
	AffiliateImportAuditEvents affiliateImportAuditEvents;

	@Resource
	AffiliatesImporterService affiliatesImporterService; 

	@Resource
	AffiliatesExporterService affiliatesExporterService; 
	
	@Resource
	AffiliatesImportGenerator affiliatesImportGenerator;
	
	@Resource
	UserRepository userRepository;
	
	@Resource
	MemberRepository memberRepository;

	@Resource
	SessionService sessionService;
	
	@Resource
	CurrentSecurityContext currentSecurityContext;

	public static final int DEFAULT_PAGE_SIZE = 50;
	
	public static final String FILTER_HOME_MEMBER = "homeMember eq '(\\w+)'";
	public static final String FILTER_STANDING = "standingState eq '(\\w+)'";
	
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATES,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliates(
    		@RequestParam(required=false) String q,
    		@RequestParam(value="$page", defaultValue="0", required=false) Integer page,
    		@RequestParam(value="$pageSize", defaultValue="50", required=false) Integer pageSize,
    		@RequestParam(value="$filter", required=false) List<String> filters,
    		@RequestParam(value="$orderby", required=false) String orderby) {
		Page<Affiliate> affiliates;
		Sort sort = createAffiliatesSort(orderby);
		PageRequest pageRequest = new PageRequest(page, pageSize, sort);
		Member member = null;
		StandingState standingState = null;
		
		if (filters == null || filters.size() == 0 || StringUtils.isBlank(filters.get(0))) {
    		if (StringUtils.isBlank(q)) {
    			affiliates = affiliateRepository.findAll(pageRequest);
    		} else {
    			affiliates = affiliateRepository.findByTextQuery(q.toLowerCase(), pageRequest);
    		}
		} else {
			for (String filter : filters) {
				Matcher homeMemberMatcher = Pattern.compile(FILTER_HOME_MEMBER).matcher(filter);
				if (homeMemberMatcher.matches()) {
					String homeMember = homeMemberMatcher.group(1);
					member = memberRepository.findOneByKey(homeMember);
					continue;
				}
				Matcher standingStateMatcher = Pattern.compile(FILTER_STANDING).matcher(filter);
				if (standingStateMatcher.matches()) {
					String standingStateString = standingStateMatcher.group(1);
					standingState = StandingState.valueOf(standingStateString);
					continue;
				}
				//FIXME support more kinds of filters...
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		
		if (!StringUtils.isBlank(q)) {
			//Note that sorting in the pageRequest is not currently respected by lucene
			affiliates = affiliateSearchRepository.findFullTextAndMember(q, member, standingState, pageRequest) ;
		} else {
			if (member == null) {
				if (standingState == null) {
					affiliates = affiliateRepository.findAll(pageRequest);
				} else {
					affiliates = affiliateRepository.findByStandingState(standingState, pageRequest);
				}
			} else {
				if (standingState == null) {
					affiliates = affiliateRepository.findByHomeMember(member, pageRequest);
				} else {
					affiliates = affiliateRepository.findByHomeMemberAndStandingState(member, standingState, pageRequest);
				}
			}
    	}
		return new ResponseEntity<Collection<Affiliate>>(affiliates.getContent(), HttpStatus.OK);
    }
	
	private static final Map<String,List<String>> ORDER_BY_FIELD_MAPPINGS = new HashMap<String,List<String>>();
	static {
		//FIXME using both the affiliateDetails and the application.affiliateDetails causes discrepancies in the order and text shown on the front end. Perhaps we should keep affiliateDetails up to date with primary application affiliateDetail updates?
		ORDER_BY_FIELD_MAPPINGS.put("affiliateId", Arrays.asList("affiliateId"));
		ORDER_BY_FIELD_MAPPINGS.put("name", Arrays.asList("affiliateDetails.firstName", "affiliateDetails.lastName"));
		ORDER_BY_FIELD_MAPPINGS.put("agreementType", Arrays.asList("affiliateDetails.type", "affiliateDetails.subType"));
		ORDER_BY_FIELD_MAPPINGS.put("standingState", Arrays.asList("standingState"));
		ORDER_BY_FIELD_MAPPINGS.put("homeCountry", Arrays.asList("affiliateDetails.address.country.commonName"));
		ORDER_BY_FIELD_MAPPINGS.put("member", Arrays.asList("homeMember.key"));
		ORDER_BY_FIELD_MAPPINGS.put("email", Arrays.asList("affiliateDetails.email"));
	}

	private Sort createAffiliatesSort(String orderby) {
		Sort defaultSort = new Sort(
//				new Order(Direction.ASC, "affiliateDetails.organizationName"),
//				new Order(Direction.ASC, "affiliateDetails.firstName"),
//				new Order(Direction.ASC, "affiliateDetails.lastName"),
//				new Order(Direction.ASC, "application.affiliateDetails.organizationName"),
//				new Order(Direction.ASC, "application.affiliateDetails.firstName"),
//				new Order(Direction.ASC, "application.affiliateDetails.lastName"),
				new Order(Direction.ASC, "affiliateId")
				);
		return new SortBuilder().createSort(orderby, ORDER_BY_FIELD_MAPPINGS, defaultSort);
	}
	
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Affiliate> getAffiliate(@PathVariable long affiliateId) {
		Affiliate affiliate = affiliateRepository.findOne(affiliateId);
		return new ResponseEntity<Affiliate>(affiliate, HttpStatus.OK);
    }

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATE,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Affiliate> updateAffiliate(@PathVariable Long affiliateId, @RequestBody Affiliate body) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	applicationAuthorizationChecker.checkCanManageAffiliate(affiliate);
    	
    	StandingState originalStanding = affiliate.getStandingState();
    	
    	copyAffiliateFields(affiliate, body);
    	
    	if (! Objects.equal(originalStanding, affiliate.getStandingState())) {
    		if (Objects.equal(originalStanding, StandingState.APPLYING)
    				|| Objects.equal(originalStanding, StandingState.REJECTED)) {
    			return new ResponseEntity<>(HttpStatus.CONFLICT);
    		} else {
    			affiliateAuditEvents.logStandingStateChange(affiliate);
    		}
    	}

    	affiliateRepository.save(affiliate);
    	
    	affiliateAuditEvents.logUpdateOfAffiliate(affiliate);
    	
    	return new ResponseEntity<Affiliate>(affiliate, HttpStatus.OK);
    }

	private void copyAffiliateFields(Affiliate affiliate, Affiliate body) {
		affiliate.setNotesInternal(body.getNotesInternal());
		if (body.getStandingState() != null) {
			affiliate.setStandingState(body.getStandingState());
		}
	}

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATES_ME,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesMe() {
    	String username = sessionService.getUsernameOrNull();
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreatorIgnoreCase(username), HttpStatus.OK);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATES_CREATOR,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesForUser(@PathVariable String username) {
    	applicationAuthorizationChecker.checkCanAccessAffiliate(username);
    	return new ResponseEntity<Collection<Affiliate>>(affiliateRepository.findByCreatorIgnoreCase(username), HttpStatus.OK);
    }
	
	
	@RolesAllowed({AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATES_CSV,
    		method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<ImportResult> importAffiliates( @RequestParam("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//FIXME Is this correct that we are assuming UTF8?
		String content = IOUtils.toString(file.getInputStream(),  Charsets.UTF_8);
		ImportResult importResult = affiliatesImporterService.importFromCSV(content);
		affiliateImportAuditEvents.logImport(importResult);
    	HttpStatus httpStatus = importResult.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<ImportResult>(importResult, httpStatus);
    }

	@RolesAllowed({AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATES_CSV,
    		method = RequestMethod.GET,
            produces = "application/csv;charset=UTF-8")
	@Timed
    public @ResponseBody ResponseEntity<String> exportAffiliates(@RequestParam(value="generate",required = false) Integer generateRows) throws IOException {
		//FIXME DGJ Introduce parameter to generate phoney data until we can add an application start
		String result;
		if (generateRows == null) {
			result = affiliatesExporterService.exportToCSV();
		} else {
			result = affiliatesImportGenerator.generateFile(generateRows);
		}
		affiliateImportAuditEvents.logExport();
		return new ResponseEntity<String>(result, HttpStatus.OK);
    }
	
	//FIXME Would like to use custom produces to overload request path
	@RolesAllowed({AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATES_CSV_SPEC,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<AffiliatesImportSpec> getAffiliatesImportSpec() throws IOException {
		AffiliatesImportSpec result = affiliatesExporterService.exportSpec();
		return new ResponseEntity<AffiliatesImportSpec>(result, HttpStatus.OK);
    }

	
	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<AffiliateDetails> updateAffiliateDetail(@PathVariable Long affiliateId) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	return new ResponseEntity<AffiliateDetails>(affiliate.getAffiliateDetails(), HttpStatus.OK);
    }

	@SuppressWarnings("unchecked")
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATE_DETAIL,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
    public @ResponseBody ResponseEntity<?> updateAffiliateDetail(@PathVariable Long affiliateId, @RequestBody AffiliateDetails body) {
    	Affiliate affiliate = affiliateRepository.findOne(affiliateId);
    	applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);
    	if (affiliate == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	AffiliateDetails affiliateDetails = affiliate.getAffiliateDetails();
    	if (affiliateDetails == null) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	
    	Application application = affiliate.getApplication();
    	if (application == null || !Objects.equal(application.getApprovalState(), ApprovalState.APPROVED)) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	
    	String originalEmail = affiliateDetails.getEmail();
    	String newEmail = body.getEmail();
		boolean emailChanged = !Objects.equal(newEmail, originalEmail);
    	if (emailChanged) {
    		if (!currentSecurityContext.isStaffOrAdmin()) {
        		return new ResponseEntity<>("Users may not change their primary email address",HttpStatus.FORBIDDEN);
    		}
    		if (Strings.isNullOrEmpty(newEmail)) {
        		return new ResponseEntity<>("Primary email address (email) is a required field",HttpStatus.BAD_REQUEST);
    		}
    		affiliate.setCreator(newEmail);
    	}
    	
    	User user = userRepository.findByLoginIgnoreCase(originalEmail);
    	if (user != null) {
    		copyAffiliateDetailsNameFieldsToUser(user, body);
    	}
    	
    	affiliateAuditEvents.logUpdateOfAffiliateDetails(affiliate,body);
    	copyAffiliateDetailsFields(affiliateDetails, body);
    	
    	affiliateSearchRepository.reindex(affiliate);
    	
    	return new ResponseEntity<AffiliateDetails>(affiliateDetails, HttpStatus.OK);
    }

	private void copyAffiliateDetailsFields(AffiliateDetails affiliateDetails, AffiliateDetails body) {
		copyAddressFieldsWithoutCountry(affiliateDetails.getAddress(), body.getAddress());
    	copyAddressFields(affiliateDetails.getBillingAddress(), body.getBillingAddress());
    	affiliateDetails.setFirstName(body.getFirstName());
    	affiliateDetails.setLandlineExtension(body.getLandlineExtension());
    	affiliateDetails.setLandlineNumber(body.getLandlineNumber());
    	affiliateDetails.setLastName(body.getLastName());
    	affiliateDetails.setMobileNumber(body.getMobileNumber());
    	
    	affiliateDetails.setAlternateEmail(body.getAlternateEmail());
    	affiliateDetails.setThirdEmail(body.getThirdEmail());
    	
    	affiliateDetails.setEmail(body.getEmail());
    	
		if (currentSecurityContext.isStaffOrAdmin()) {
	    	affiliateDetails.setType(body.getType());
	    	affiliateDetails.setOtherText(body.getOtherText());
	    	affiliateDetails.setSubType(body.getSubType());
	    	affiliateDetails.setAgreementType(body.getAgreementType());
    	}
	}

	private void copyAddressFields(MailingAddress address, MailingAddress body) {
		copyAddressFieldsWithoutCountry(address, body);
		address.setCountry(body.getCountry());
	}

	private void copyAddressFieldsWithoutCountry(MailingAddress address, MailingAddress body) {
		address.setCity(body.getCity());
		address.setPost(body.getPost());
		address.setStreet(body.getStreet());
	}

	private void copyAffiliateDetailsNameFieldsToUser(User user, AffiliateDetails body) {
    	user.setFirstName(body.getFirstName());
    	user.setLastName(body.getLastName());
		user.setEmail(body.getEmail());
		user.setLogin(body.getEmail());
	}
}
