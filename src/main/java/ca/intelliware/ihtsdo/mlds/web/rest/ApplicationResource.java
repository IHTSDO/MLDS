package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
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

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateSubType;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.AgreementType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.OrganizationType;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.domain.json.ApplicationCollection;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.service.ApprovalTransition;
import ca.intelliware.ihtsdo.mlds.service.CommercialUsageService;
import ca.intelliware.ihtsdo.mlds.service.UsageReportTransition;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
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

@RestController
public class ApplicationResource {
	@Resource ApplicationRepository applicationRepository;
	@Resource SessionService sessionService;
	@Resource AffiliateRepository affiliateRepository;
	@Resource ApplicationApprovedEmailSender applicationApprovedEmailSender;
	@Resource UserRepository userRepository;
	@Resource ApplicationAuditEvents applicationAuditEvents;
	@Resource AffiliateAuditEvents affiliateAuditEvents;
	@Resource ApplicationAuthorizationChecker authorizationChecker;
	@Resource CountryRepository countryRepository;
	@Resource AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource AffiliateDetailsResetter affiliateDetailsResetter;
	@Resource ApplicationService applicationService;
	@Resource RouteLinkBuilder routeLinkBuilder;
	@Resource ObjectMapper objectMapper;
	@Resource UserMembershipAccessor userMembershipAccessor;
	@Resource MemberRepository memberRepository;
	@Resource CommercialUsageService commercialUsageService;

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
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
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
		if (Objects.equal(application.getApplicationType(), ApplicationType.PRIMARY)) {
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
		PageRequest pageRequest = new PageRequest(page, pageSize, sort);
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

	private Sort createApplicationsSort(String orderby) {
		Sort defaultSort = new Sort(
				new Order(Direction.ASC, "applicationId")
			);
		return new SortBuilder().createSort(orderby, ORDER_BY_FIELD_MAPPINGS, defaultSort);
	}

	@RequestMapping(value = Routes.APPLICATION, 
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	@Timed
	public  @ResponseBody ResponseEntity<Application> getApplication(@PathVariable long applicationId){
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
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
		PrimaryApplication application = (PrimaryApplication) applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

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
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	
	@Transactional
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	@Timed
	public @ResponseBody ResponseEntity<Application> saveApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
		Application application = applicationRepository.findOne(applicationId);
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
		Application application = applicationRepository.findOne(applicationId);
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
				mailingAddress.setCountry(countryRepository.findOne(getStringField(country, "isoCode2")));
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
				billingAddress.setCountry(countryRepository.findOne(getStringField(billingCountry, "isoCode2")));
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
		Application original = applicationRepository.findOne(applicationId);
		Application updatedApplication = constructUpdatedApplication(requestBody, original);
		
		try {
			applicationService.doUpdate(original, updatedApplication);
			
			applicationAuditEvents.logApprovalStateChange(updatedApplication);
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
		Application application = applicationRepository.findOne(applicationId);
		
		if (!application.getApplicationType().equals(ApplicationType.EXTENSION)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		applicationRepository.delete(applicationId);
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
