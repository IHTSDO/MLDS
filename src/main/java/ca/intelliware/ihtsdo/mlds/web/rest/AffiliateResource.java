package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.search.AffiliateSearchResult;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDeleter;
import ca.intelliware.ihtsdo.mlds.service.affiliatesimport.*;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AffiliateResource {

    private final Logger log = LoggerFactory.getLogger(AffiliateResource.class);

	@Autowired
	AffiliateRepository affiliateRepository;

	@Autowired
	AffiliateSearchRepository affiliateSearchRepository;

	@Autowired
	AffiliateDetailsRepository affiliateDetailsRepository;

	@Autowired
	ApplicationAuthorizationChecker applicationAuthorizationChecker;

	@Autowired
	AffiliateAuditEvents affiliateAuditEvents;

	@Autowired
	AffiliateImportAuditEvents affiliateImportAuditEvents;

	@Autowired
	AffiliatesImporterService affiliatesImporterService;

	@Autowired
	AffiliatesExporterService affiliatesExporterService;

	@Autowired
	AffiliatesImportGenerator affiliatesImportGenerator;

	@Autowired
	UserRepository userRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	AffiliateDeleter affiliateDeleter;

	@Autowired
	SessionService sessionService;

	@Autowired
	CurrentSecurityContext currentSecurityContext;

	public static final int DEFAULT_PAGE_SIZE = 50;

	public static final String FILTER_HOME_MEMBER = "homeMember eq '(\\w+)'";
	public static final String FILTER_STANDING = "(not)?\\s?standingState eq '(\\w+)'";

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATES,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<AffiliateSearchResult> getAffiliates(
    		@RequestParam(required=false) String q,
    		@RequestParam(value="$page", defaultValue="0", required=false) Integer page,
    		@RequestParam(value="$pageSize", defaultValue="50", required=false) Integer pageSize,
    		@RequestParam(value="$filter", required=false) List<String> filters,
    		@RequestParam(value="$orderby", required=false) String orderby) {
		Page<Affiliate> affiliates;
		Sort sort = createAffiliatesSort(orderby);
		PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
		Member member = null;
		StandingState standingState = null;
		boolean standingStateNot = false;

		if (filters != null && filters.size() > 0 && StringUtils.isNotBlank(filters.get(0))) {
			for (String filter : filters) {
				Matcher homeMemberMatcher = Pattern.compile(FILTER_HOME_MEMBER).matcher(filter);
				if (homeMemberMatcher.matches()) {
					String homeMember = homeMemberMatcher.group(1);
					member = memberRepository.findOneByKey(homeMember);
					continue;
				}
				Matcher standingStateMatcher = Pattern.compile(FILTER_STANDING).matcher(filter);
				if (standingStateMatcher.matches()) {
					standingStateNot = StringUtils.equalsIgnoreCase("not", standingStateMatcher.group(1));
					String standingStateString = standingStateMatcher.group(2);
					standingState = StandingState.valueOf(standingStateString);
					continue;
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

		if (!StringUtils.isBlank(q)) {
			//Note that sorting in the pageRequest is not currently respected by lucene
			affiliates = affiliateSearchRepository.findFullTextAndMember(q, member, standingState, standingStateNot, pageRequest);
		} else {
			if (member == null) {
				if (standingState == null) {
					affiliates = affiliateRepository.findAll(pageRequest);
				} else {
					if (standingStateNot) {
						affiliates = affiliateRepository.findByStandingStateNot(standingState, pageRequest);
					} else {
						affiliates = affiliateRepository.findByStandingState(standingState, pageRequest);
					}
				}
			} else {
				if (standingState == null) {
					affiliates = affiliateRepository.findByHomeMember(member, pageRequest);
				} else {
					if (standingStateNot) {
						affiliates = affiliateRepository.findByHomeMemberAndStandingStateNot(member, standingState, pageRequest);
					} else {
						affiliates = affiliateRepository.findByHomeMemberAndStandingState(member, standingState, pageRequest);
					}
				}
			}
		}
		AffiliateSearchResult result = new AffiliateSearchResult();
		result.setAffiliates(affiliates.getContent());
		result.setTotalResults(affiliates.getTotalElements());
		result.setTotalPages(affiliates.getTotalPages());

		return new ResponseEntity<AffiliateSearchResult>(result, HttpStatus.OK);
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
		Sort defaultSort = Sort.by(Sort.Order.asc("affiliateId"));
		return new SortBuilder().createSort(orderby, ORDER_BY_FIELD_MAPPINGS, defaultSort);
	}

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATE,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public @ResponseBody ResponseEntity<Affiliate> getAffiliate(@PathVariable long affiliateId) {

		Optional<Affiliate> optionalAffiliate = affiliateRepository.findById(affiliateId);
		if (optionalAffiliate.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

		Affiliate affiliate = optionalAffiliate.get();

		//Populate the notifications fields from the user object
		String login = affiliate.getAffiliateDetails().getEmail();
		User user = userRepository.findByLoginIgnoreCase(login);
    	if (user != null) {
    		affiliate.getAffiliateDetails().setAcceptNotifications(user.getAcceptNotifications());
            affiliate.getAffiliateDetails().setCountryNotificationsOnly(user.getCountryNotificationsOnly());
    	}
		return new ResponseEntity<Affiliate>(affiliate, HttpStatus.OK);
    }

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATE,
    		method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Affiliate> updateAffiliate(@PathVariable Long affiliateId, @RequestBody Affiliate body) {
		Optional<Affiliate> affiliateOptional = affiliateRepository.findById(affiliateId);
    	if (affiliateOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
		Affiliate affiliate = affiliateOptional.get();

    	applicationAuthorizationChecker.checkCanManageAffiliate(affiliate);

    	StandingState originalStanding = affiliate.getStandingState();

    	copyAffiliateFields(affiliate, body);

    	if (! Objects.equals(originalStanding, affiliate.getStandingState())) {
    		if (Objects.equals(originalStanding, StandingState.APPLYING)){
    			return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            else {
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

	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
    @RequestMapping(value = Routes.AFFILIATE,
    		method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Timed
    public @ResponseBody ResponseEntity<Affiliate> deleteAffiliate(@PathVariable Long affiliateId) {
		Optional<Affiliate> affiliateOptional = affiliateRepository.findById(affiliateId);

    	if (affiliateOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

		Affiliate affiliate = affiliateOptional.get();
    	applicationAuthorizationChecker.checkCanManageAffiliate(affiliate);
        if (!ObjectUtils.equals(affiliate.getStandingState(), StandingState.APPLYING)) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        affiliateDeleter.deleteAffiliate(affiliate);
        affiliateAuditEvents.logDeleteOfAffiliate(affiliate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RolesAllowed({AuthoritiesConstants.USER,AuthoritiesConstants.STAFF,AuthoritiesConstants.ADMIN})
    @RequestMapping(value = Routes.AFFILIATES_ME,
    		method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
    public @ResponseBody ResponseEntity<Collection<Affiliate>> getAffiliatesMe() {
    	String username = sessionService.getUsernameOrNull();
    	List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(username);
    	//Notifications flag is held against the user object, which is primary source for email address
    	for (Affiliate affiliate : affiliates) {
    		User user = userRepository.findByLoginIgnoreCase(affiliate.getCreator());
    		if (affiliate.getAffiliateDetails() != null) {
    			affiliate.getAffiliateDetails().setAcceptNotifications(user.getAcceptNotifications());
                affiliate.getAffiliateDetails().setCountryNotificationsOnly(user.getCountryNotificationsOnly());
    		}
    	}
    	return new ResponseEntity<Collection<Affiliate>>(affiliates, HttpStatus.OK);
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
		String result;
		if (generateRows == null) {
			result = affiliatesExporterService.exportToCSV();
		} else {
			result = affiliatesImportGenerator.generateFile(generateRows);
		}
		affiliateImportAuditEvents.logExport();
		return new ResponseEntity<String>(result, HttpStatus.OK);
    }

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
		Optional<Affiliate> affiliateOptional = affiliateRepository.findById(affiliateId);

    	if (affiliateOptional.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
		Affiliate affiliate = affiliateOptional.get();
		applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);
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

		Optional<Affiliate> optionalAffiliate = affiliateRepository.findById(affiliateId);

    	if (optionalAffiliate.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

		Affiliate affiliate = optionalAffiliate.get();
		applicationAuthorizationChecker.checkCanAccessAffiliate(affiliate);

    	AffiliateDetails affiliateDetails = affiliate.getAffiliateDetails();
    	if (affiliateDetails == null) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}

    	Application application = affiliate.getApplication();
    	if (application == null || !Objects.equals(application.getApprovalState(), ApprovalState.APPROVED)) {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}

    	String originalEmail = affiliateDetails.getEmail();
    	String newEmail = body.getEmail();
		boolean emailChanged = !Objects.equals(newEmail, originalEmail);
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
    		copyAffiliateDetailsFieldsToUser(user, body);
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
    	affiliateDetails.setAcceptNotifications(body.isAcceptNotifications());
        affiliateDetails.setCountryNotificationsOnly(body.isCountryNotificationsOnly());
         /*MLDS-994 Updating of User Details*/
        affiliateDetails.setOrganizationName(body.getOrganizationName());
        /*MLDS-994 Updating of User Details*/

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

	private void copyAffiliateDetailsFieldsToUser(User user, AffiliateDetails body) {
		user.setFirstName(body.getFirstName());
		user.setLastName(body.getLastName());
		user.setEmail(body.getEmail());
		user.setLogin(body.getEmail());
		user.setAcceptNotifications(body.isAcceptNotifications());
        user.setCountryNotificationsOnly(body.isCountryNotificationsOnly());
	}
}
