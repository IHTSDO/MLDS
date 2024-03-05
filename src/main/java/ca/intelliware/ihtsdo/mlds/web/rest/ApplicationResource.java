package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.domain.json.ApplicationCollection;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.*;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@RestController
public class ApplicationResource {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;
	@Resource
	AffiliateRepository affiliateRepository;
	@Resource
	ApplicationApprovedEmailSender applicationApprovedEmailSender;
	@Resource ApplicationApprovalStateChangeNotifier applicationApprovalStateChangeNotifier;
	@Resource
	UserRepository userRepository;
	@Resource ApplicationAuditEvents applicationAuditEvents;
	@Resource
	AffiliateAuditEvents affiliateAuditEvents;
	@Resource ApplicationAuthorizationChecker authorizationChecker;
	@Resource
	CountryRepository countryRepository;
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource
	AffiliateDetailsResetter affiliateDetailsResetter;
	@Resource
	ApplicationService applicationService;
	@Resource
	RouteLinkBuilder routeLinkBuilder;
	@Resource ObjectMapper objectMapper;
	@Resource
	UserMembershipAccessor userMembershipAccessor;
	@Resource MemberRepository memberRepository;
	@Resource
	CommercialUsageService commercialUsageService;

	@RequestMapping(value="api/applications")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}

	@RequestMapping(value = Routes.APPLICATION_APPROVE,
			method=RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public @ResponseBody ResponseEntity<Application> approveApplication(@PathVariable long applicationId, @RequestBody String approvalStateString) throws CloneNotSupportedException {
		//FIXME why cant this be the body type?
		ApprovalState approvalState = ApprovalState.valueOf(approvalStateString);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

		if (applicationOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Application application = applicationOptional.get();
		authorizationChecker.checkCanApproveApplication(application);

		//FIXME should there be state transition validation?
		application.setApprovalState(approvalState);

		//FIXME add flags to approval state?
		if (Objects.equal(approvalState, ApprovalState.APPROVED) || Objects.equal(approvalState, ApprovalState.REJECTED)) {
			application.setCompletedAt(Instant.now());
		}

		Affiliate affiliate = findAffiliateByUsername(application.getUsername());
		if (affiliate == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			AffiliateDetails affiliateDetails = (AffiliateDetails) application.getAffiliateDetails().clone();

			affiliateDetailsResetter.detach(affiliateDetails);

			affiliateDetails = affiliateDetailsRepository.save(affiliateDetails);
			affiliate.setAffiliateDetails(affiliateDetails);
			affiliateRepository.save(affiliate);
		}

		//FIXME MLDS-314 not sure where this code should be
		if (Objects.equal(application.getApplicationType(), Application.ApplicationType.PRIMARY)) {
			StandingState newStandingState = null;
			if (Objects.equal(application.getApprovalState(), ApprovalState.APPROVED)) {
				// MLDS-902 When the authorizing user is a member (ie not IHTSDO) then the account standing
				// state should transition to "In Good Standing"
				if (authorizationChecker.isAdmin()) {
					newStandingState = StandingState.PENDING_INVOICE;
				} else {
					newStandingState = StandingState.IN_GOOD_STANDING;
				}
			} else if (Objects.equal(application.getApprovalState(), ApprovalState.REJECTED)) {
				newStandingState = StandingState.REJECTED;
			}
			if (Objects.equal(affiliate.getStandingState(), StandingState.APPLYING) && newStandingState != null) {
				affiliate.setStandingState(newStandingState);
				affiliateRepository.save(affiliate);
				affiliateAuditEvents.logStandingStateChange(affiliate);
			}
		}

		applicationRepository.save(application);

		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			User user = userRepository.getUserByEmailIgnoreCase(application.getAffiliateDetails().getEmail());
			applicationApprovedEmailSender.sendApplicationApprovalEmail(user, application.getMember().getKey(), affiliate.getAffiliateId());
		}

		applicationAuditEvents.logApprovalStateChange(application);

		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	private Affiliate findAffiliateByUsername(String username) {
		Affiliate affiliate = null;
		List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(username);

		if (affiliates.size() > 0) {
			affiliate = affiliates.get(0);
		}
		return affiliate;
	}

	private static final String FILTER_PENDING = "approvalState/pending eq true";
	public static final String FILTER_HOME_MEMBER = "homeMember eq '(\\w+)'";

	@RequestMapping(value = Routes.APPLICATIONS,
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public @ResponseBody ResponseEntity<ApplicationCollection> getApplications(
    		@RequestParam(value="$page", defaultValue="0", required=false) Integer page,
    		@RequestParam(value="$pageSize", defaultValue="50", required=false) Integer pageSize,
    		@RequestParam(value="$orderby", required=false) String orderby,
			@RequestParam(value="$filter", required=false) List<String> filters) {

		Page<Application> applications;
		Sort sort = createApplicationsSort(orderby);
//		PageRequest pageRequest = new PageRequest(page, pageSize, sort);
        Pageable pageRequest = PageRequest.of(page, pageSize, sort);
        Member member = null;
		List<ApprovalState> approvalStates = null;

		if (filters == null || filters.size() == 0 || StringUtils.isBlank(filters.get(0))) {
			applications = applicationRepository.findAll(pageRequest);
		} else {
			for (String filter : filters) {
				Matcher homeMemberMatcher = Pattern.compile(FILTER_HOME_MEMBER).matcher(filter);
				if (homeMemberMatcher.matches()) {
					String homeMember = homeMemberMatcher.group(1);
					member = memberRepository.findOneByKey(homeMember);
					continue;
				}
				Matcher pendingMatcher = Pattern.compile(FILTER_PENDING).matcher(filter);
				if (pendingMatcher.matches()) {
					approvalStates = Lists.newArrayList(ApprovalState.SUBMITTED, ApprovalState.RESUBMITTED, ApprovalState.REVIEW_REQUESTED, ApprovalState.CHANGE_REQUESTED);
					continue;
				}
				//FIXME Limited OData implementation - expand or use real OData library in the future
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			if (member != null) {
				if (approvalStates != null) {
					applications = applicationRepository.findByApprovalStateInAndMember(approvalStates, member, pageRequest);
				} else {
					applications = applicationRepository.findByMember(member, pageRequest);
				}
			} else {
				applications = applicationRepository.findByApprovalStateIn(approvalStates, pageRequest);
			}
		}
		return new ResponseEntity<ApplicationCollection>(new ApplicationCollection(applications), HttpStatus.OK);
	}

	private static final Map<String,List<String>> ORDER_BY_FIELD_MAPPINGS = new HashMap<String,List<String>>();
	static {
		ORDER_BY_FIELD_MAPPINGS.put("applicationId", Arrays.asList("applicationId"));
		ORDER_BY_FIELD_MAPPINGS.put("affiliateName", Arrays.asList("affiliateDetails.firstName", "affiliateDetails.lastName"));
		ORDER_BY_FIELD_MAPPINGS.put("applicationType", Arrays.asList("applicationTypeValue"));
		ORDER_BY_FIELD_MAPPINGS.put("agreementType", Arrays.asList("affiliateDetails.agreementType", "affiliate.affiliateDetails.agreementType"));
		ORDER_BY_FIELD_MAPPINGS.put("useType", Arrays.asList("affiliateDetails.type", "affiliateDetails.subType"));
		ORDER_BY_FIELD_MAPPINGS.put("submittedAt", Arrays.asList("submittedAt"));
		ORDER_BY_FIELD_MAPPINGS.put("approvalState", Arrays.asList("approvalState"));
		ORDER_BY_FIELD_MAPPINGS.put("country", Arrays.asList("affiliateDetails.address.country.commonName"));
		ORDER_BY_FIELD_MAPPINGS.put("member", Arrays.asList("member.key"));
		ORDER_BY_FIELD_MAPPINGS.put("email", Arrays.asList("affiliateDetails.email"));
	}

//	private Sort createApplicationsSort(String orderby) {
//		Sort defaultSort = new Sort(
//				new Order(Direction.ASC, "applicationId")
//			);
//		return new SortBuilder().createSort(orderby, ORDER_BY_FIELD_MAPPINGS, defaultSort);
//	}

    private Sort createApplicationsSort(String orderby) {
        Sort defaultSort = Sort.by(Sort.Order.asc("applicationId"));
        return new SortBuilder().createSort(orderby, ORDER_BY_FIELD_MAPPINGS, defaultSort);
    }


    @RequestMapping(value = Routes.APPLICATION,
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public  @ResponseBody ResponseEntity<Application> getApplication(@PathVariable long applicationId){

		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		if (applicationOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Application application = applicationOptional.get();
		authorizationChecker.checkCanAccessApplication(application);
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	@RequestMapping(value = Routes.APPLICATION_ME,
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	@Timed
	public  @ResponseBody ResponseEntity<Application> getApplicationForMe(){
		List<Application> applications = applicationRepository.findByUsernameIgnoreCase(sessionService.getUsernameOrNull());
		if (applications.size() > 0) {
			return new ResponseEntity<Application>(applications.get(0), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Transactional
	@RequestMapping(value="/api/application",
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	@Timed
	public @ResponseBody ResponseEntity<Application> getUserApplication(){
		List<Application> applications = applicationRepository.findByUsernameIgnoreCase(sessionService.getUsernameOrNull());
		if (applications.size() > 0) {
			return new ResponseEntity<Application>(applications.get(0), HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@Transactional
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	@Timed
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
//		PrimaryApplication application = (PrimaryApplication) applicationRepository.findOne(applicationId);
//        PrimaryApplication application = (PrimaryApplication) applicationRepository.findOneById(applicationId);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		PrimaryApplication application = (PrimaryApplication) applicationOptional.get();
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		ApprovalState originalApplicationState = application.getApprovalState();

        application = saveApplicationFields(request,application);
		authorizationChecker.checkCanAccessApplication(application);

		// Mark application as submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)) {
			application.setApprovalState(ApprovalState.RESUBMITTED);
		} else if (Objects.equal(application.getApprovalState(), ApprovalState.NOT_SUBMITTED)){
			application.setApprovalState(ApprovalState.SUBMITTED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		application.setSubmittedAt(Instant.now());
		applicationRepository.save(application);

		Affiliate affiliate = application.getAffiliate();
		affiliate.setCreator(application.getUsername());
		affiliate.setApplication(application);
		affiliate.setType(application.getType());
		affiliate.setHomeMember(application.getMember());

		{
			AffiliateDetails affiliateDetails = (AffiliateDetails) application.getAffiliateDetails().clone();
			affiliateDetailsResetter.detach(affiliateDetails);
			affiliate.setAffiliateDetails(affiliateDetails);
		}

		affiliateRepository.save(affiliate);

		applicationAuditEvents.logApprovalStateChange(application);

		if (application.getCommercialUsage() != null
				&& Objects.equal(application.getCommercialUsage().getState(), UsageReportState.NOT_SUBMITTED)) {
			commercialUsageService.transitionCommercialUsageApproval(application.getCommercialUsage(), UsageReportTransition.SUBMIT);
		}

		applicationApprovalStateChangeNotifier.applicationApprovalStateChange(originalApplicationState, application);

		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}


	@Transactional
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	@Timed
	public @ResponseBody ResponseEntity<Application> saveApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
//		Application application = applicationRepository.findOne(applicationId);
//        Application application = applicationRepository.findOneById(applicationId);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		Application application = applicationOptional.get();
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
        application = saveApplicationFields(request,application);
        authorizationChecker.checkCanAccessApplication(application);

        ApprovalState preApprovalState = application.getApprovalState();
		// Mark application as not submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)) {
			application.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		} else {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}

		if (!Objects.equal(application.getApprovalState(), preApprovalState)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		application.getAffiliate().setAffiliateDetails(application.getAffiliateDetails());

		applicationRepository.save(application);

		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = Routes.APPLICATION_NOTES_INTERNAL,
			method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	@Timed
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody String notesInternal) {
//		Application application = applicationRepository.findOne(applicationId);
//        Application application = applicationRepository.findOneById(applicationId);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		Application application = applicationOptional.get();
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		application.setNotesInternal(notesInternal);
		applicationRepository.save(application);
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}


	private <T extends Application> T saveApplicationFields(JsonNode request,T application) {
        JsonNode affiliateDetailsJsonNode = request.get("affiliateDetails");
        JsonNode address = affiliateDetailsJsonNode.get("address");
        JsonNode billing = affiliateDetailsJsonNode.get("billingAddress");

		application.setUsername(sessionService.getUsernameOrNull());

		AffiliateDetails affiliateDetails = application.getAffiliateDetails();
		createAffiliateDetails(affiliateDetailsJsonNode, address, billing, affiliateDetails);
		affiliateDetailsRepository.save(affiliateDetails);
		application.setAffiliateDetails(affiliateDetails);

		application.setMember(findMemberFromAddressCountry(affiliateDetails));

		if (application.getApprovalState() == null) {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}

		if (application instanceof PrimaryApplication) {
			PrimaryApplication primaryApplication = (PrimaryApplication) application;
			// FIXME MLDS-32 MB we should update the client to match the new structure
			if (checkIfValidField(request, "type")){
				primaryApplication.getAffiliateDetails().setType(AffiliateType.valueOf(getStringField(request, "type")));
			}
			if (checkIfValidField(request, "subType")) {
				primaryApplication.getAffiliateDetails().setSubType(AffiliateSubType.valueOf(getStringField(request, "subType")));
			}
			primaryApplication.getAffiliateDetails().setOtherText(getStringField(request, "otherText"));

			primaryApplication.setSnoMedLicense(Boolean.parseBoolean(getStringField(request, "snoMedTC")));
		}

		return application;
	}

	private Member findMemberFromAddressCountry(AffiliateDetails affiliateDetails) {
		Validate.notNull(affiliateDetails.getAddress(), "Address is mandatory");
		Validate.notNull(affiliateDetails.getAddress().getCountry(), "Address country is mandatory");
		Validate.notNull(affiliateDetails.getAddress().getCountry().getMember(), "Address country member is mandatory");
		return affiliateDetails.getAddress().getCountry().getMember();
	}

	private void createAffiliateDetails(JsonNode affiliateDetailsJsonNode, JsonNode addressJsonNode, JsonNode billingJsonNode, AffiliateDetails affiliateDetails) {
		if (affiliateDetails == null) {
			affiliateDetails = new AffiliateDetails();
		}

		affiliateDetails.setFirstName(getStringField(affiliateDetailsJsonNode, "firstName"));
		affiliateDetails.setLastName(getStringField(affiliateDetailsJsonNode, "lastName"));
		affiliateDetails.setLandlineNumber(getStringField(affiliateDetailsJsonNode, "landlineNumber"));
		affiliateDetails.setLandlineExtension(getStringField(affiliateDetailsJsonNode, "landlineExtension"));
		affiliateDetails.setMobileNumber(getStringField(affiliateDetailsJsonNode, "mobileNumber"));
		affiliateDetails.setEmail(getStringField(affiliateDetailsJsonNode, "email"));
		affiliateDetails.setAlternateEmail(getStringField(affiliateDetailsJsonNode, "alternateEmail"));
		affiliateDetails.setThirdEmail(getStringField(affiliateDetailsJsonNode, "thirdEmail"));
		affiliateDetails.setOrganizationName(getStringField(affiliateDetailsJsonNode, "organizationName"));
		affiliateDetails.setOrganizationTypeOther(getStringField(affiliateDetailsJsonNode, "organizationTypeOther"));

		if (checkIfValidField(affiliateDetailsJsonNode, "organizationType")) {
			affiliateDetails.setOrganizationType(OrganizationType.valueOf(getStringField(affiliateDetailsJsonNode, "organizationType")));
		}

		String agreementTypeString = getStringField(affiliateDetailsJsonNode, "agreementType");
		affiliateDetails.setAgreementType(Strings.isNullOrEmpty(agreementTypeString)?null:AgreementType.valueOf(agreementTypeString));
		MailingAddress mailingAddress = new MailingAddress();

		mailingAddress.setStreet(getStringField(addressJsonNode, "street"));
		mailingAddress.setCity(getStringField(addressJsonNode, "city"));
		mailingAddress.setPost(getStringField(addressJsonNode, "post"));

		if (addressJsonNode != null) {
			JsonNode country = addressJsonNode.get("country");
			if (checkIfValidField(country, "isoCode2")) {
//				mailingAddress.setCountry(countryRepository.findOne(getStringField(country, "isoCode2")));
                mailingAddress.setCountry(countryRepository.findByIsoCode2(getStringField(country, "isoCode2")));
			}
		}

		affiliateDetails.setAddress(mailingAddress);

		MailingAddress billingAddress = new MailingAddress();

		billingAddress.setStreet(getStringField(billingJsonNode, "street"));
		billingAddress.setCity(getStringField(billingJsonNode, "city"));
		billingAddress.setPost(getStringField(billingJsonNode, "post"));

		if(billingJsonNode != null) {
			JsonNode billingCountry = billingJsonNode.get("country");
			if (checkIfValidField(billingCountry, "isoCode2")) {
//				billingAddress.setCountry(countryRepository.findOne(getStringField(billingCountry, "isoCode2")));
                billingAddress.setCountry(countryRepository.findByIsoCode2(getStringField(billingCountry, "isoCode2")));
			}
		}

		affiliateDetails.setBillingAddress(billingAddress);
	}

	private String getStringField(JsonNode jsonNode, String attribute) {
		if (checkIfValidField(jsonNode, attribute)) {
			return jsonNode.get(attribute).asText();
		}
		return "";
	}

	private Boolean checkIfValidField(JsonNode jsonNode, String attribute) {
		if (jsonNode != null && jsonNode.get(attribute) != null) {
			String text = jsonNode.get(attribute).asText();
			if(!Objects.equal(text, "") && !Objects.equal(text, "null")) {
				return true;
			}
		}
		return false;
	}

	public static class CreateApplicationDTO {
		Application.ApplicationType applicationType;
		String memberKey;

		public Application.ApplicationType getApplicationType() {
			return applicationType;
		}

		public void setApplicationType(Application.ApplicationType applicationType) {
			this.applicationType = applicationType;
		}

		public String getMemberKey() {
			return memberKey;
		}

		public void setMemberKey(String memberKey) {
			this.memberKey = memberKey;
		}

	}
	@RequestMapping(value = Routes.APPLICATIONS,
			method=RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public ResponseEntity<Application> createApplication(@RequestBody CreateApplicationDTO requestBody) {
		authorizationChecker.checkCanCreateApplication(requestBody);

		// FIXME MLDS-308 it is an error to try to create an extension application without a target member.
		Member member = (requestBody.getMemberKey() != null) ?  memberRepository.findOneByKey(requestBody.getMemberKey()) : userMembershipAccessor.getMemberAssociatedWithUser();

		Application application = applicationService.startNewApplication(requestBody.getApplicationType(), member);

		applicationAuditEvents.logCreationOf(application);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(routeLinkBuilder.toURLWithKeyValues(Routes.APPLICATION, "applicationId", application.getApplicationId()));
		ResponseEntity<Application> result = new ResponseEntity<Application>(application, headers, HttpStatus.CREATED);
		return result;
	}

	@RequestMapping(value = Routes.APPLICATION,
			method=RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
	@Timed
	public ResponseEntity<?> updateApplication(@PathVariable long applicationId, @RequestBody ObjectNode requestBody) throws IOException {
//		Application original = applicationRepository.findOne(applicationId);
//        Application original = applicationRepository.findOneById(applicationId);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		Application original = applicationOptional.get();
		Application updatedApplication = constructUpdatedApplication(requestBody, original);

		try {
			ApprovalState originalApprovalState = original.getApprovalState();
			applicationService.doUpdate(original, updatedApplication);

			applicationAuditEvents.logApprovalStateChange(updatedApplication);
			applicationApprovalStateChangeNotifier.applicationApprovalStateChange(originalApprovalState, updatedApplication);

		} catch (IllegalArgumentException e) {
			return new ResponseEntity<String>("Forbidden change to application:" + e.getMessage(), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<Application>(original, HttpStatus.OK);
	}

	@RequestMapping(value = Routes.APPLICATION,
			method=RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Transactional
	@Timed
	public ResponseEntity<?> deleteApplication(@PathVariable long applicationId) throws IOException {
//		Application application = applicationRepository.findOne(applicationId);
//        Application application = applicationRepository.findOneById(applicationId);
		Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
		Application application = applicationOptional.get();

		if (!application.getApplicationType().equals(Application.ApplicationType.EXTENSION)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

//		applicationRepository.delete(applicationId);
//        applicationRepository.deleteByApplicationId(applicationId);
		applicationRepository.delete(application);
		applicationAuditEvents.logDeletionOf(application);
		return new ResponseEntity<Application>(HttpStatus.OK);
	}


	/**
	 * Verify that the requestBody has the same applicationType, or default it to the original type
	 * @param requestBody
	 * @param original
	 * @return a detached Application instance with all of the changes applied
	 */
	private Application constructUpdatedApplication(ObjectNode requestBody, Application original) throws IOException, JsonParseException,
			JsonMappingException, JsonProcessingException {
		ObjectNode treeCopyOfOriginal = objectMapper.readValue(objectMapper.writeValueAsString(original), ObjectNode.class);
		String typeTag = requestBody.get("applicationType")!= null?requestBody.get("applicationType").asText():null;
		String originalTypeTag = treeCopyOfOriginal.get("applicationType").asText();
		if (!Strings.isNullOrEmpty(typeTag) && !typeTag.equals(originalTypeTag)) {
			throw new IllegalArgumentException("Can't change type of application via update");
		} else {
			requestBody.put("applicationType", originalTypeTag);
		}
		Application updatedApplication = objectMapper.treeToValue(requestBody, Application.class);
		return updatedApplication;
	}
}
