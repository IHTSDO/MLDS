package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;
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
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.OrganizationType;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.domain.json.ApplicationCollection;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

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
	@Resource ApplicationAuthorizationChecker authorizationChecker;
	@Resource CountryRepository countryRepository;
	@Resource AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource AffiliateDetailsResetter affiliateDetailsResetter;
	@Resource ApplicationService applicationService;
	@Resource RouteLinkBuilder routeLinkBuilder;
	@Resource ObjectMapper objectMapper;
	@Resource UserMembershipAccessor userMembershipAccessor;
	@Resource MemberRepository memberRepository;

	@RequestMapping(value="api/applications")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}
	
	@RequestMapping(value = Routes.APPLICATION_APPROVE,
			method=RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
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
		
		
		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			List<Affiliate> affiliates = affiliateRepository.findByCreator(application.getUsername());
			
			if (affiliates.size() > 0) {
				Affiliate affiliate = affiliates.get(0);
				AffiliateDetails affiliateDetails = (AffiliateDetails) application.getAffiliateDetails().clone(); 
				
				affiliateDetailsResetter.detach(affiliateDetails);
				
				affiliateDetails = affiliateDetailsRepository.save(affiliateDetails);
				affiliate.setAffiliateDetails(affiliateDetails);
				affiliateRepository.save(affiliate);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
		
		
		
		applicationRepository.save(application);
		
		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			User user = userRepository.getUserByEmail(application.getAffiliateDetails().getEmail());
			applicationApprovedEmailSender.sendApplicationApprovalEmail(user, application.getMember().getKey());
		}
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = Routes.APPLICATIONS, 
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	public @ResponseBody ResponseEntity<ApplicationCollection> getApplications(@RequestParam(value="$filter") String filter){
		Iterable<Application> applications;
		if (filter == null) {
			applications = applicationRepository.findAll();
		} else {
			// Limited OData implementation - expand or use real OData library in the future
			if (Objects.equal(filter, "approvalState/pending eq true")) {
				applications = applicationRepository.findByApprovalStateIn(Lists.newArrayList(ApprovalState.SUBMITTED, ApprovalState.RESUBMITTED, ApprovalState.REVIEW_REQUESTED, ApprovalState.CHANGE_REQUESTED));
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<ApplicationCollection>(new ApplicationCollection(applications), HttpStatus.OK);
	}

	@RequestMapping(value = Routes.APPLICATION, 
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER, AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
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
	public  @ResponseBody ResponseEntity<Application> getApplicationForMe(){
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
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
	public @ResponseBody ResponseEntity<Application> getUserApplication(){
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
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
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
		PrimaryApplication application = findOrStartInitialApplication();
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
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		//FIXME should be a different trigger and way to connect applications with affiliate
		List<Affiliate> affiliates = affiliateRepository.findByCreator(application.getUsername());
		Affiliate affiliate = new Affiliate();
		
		//FIXME only supporting 1 affiliate for now
		if (affiliates.size() > 0) {
			affiliate = affiliates.get(0);
		}
		affiliate.setCreator(application.getUsername());
		affiliate.setApplication(application);
		affiliate.setType(application.getType());
		affiliate.setHomeMember(application.getMember());
		affiliateRepository.save(affiliate);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	
	@Transactional
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({AuthoritiesConstants.USER})
	public @ResponseBody ResponseEntity<Application> saveApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
        Application application = findOrStartInitialApplication();
        application = saveApplicationFields(request,application);
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
		
		applicationRepository.save(application);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = Routes.APPLICATION_NOTES_INTERNAL,
			method=RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
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
			if (checkIfValidField(request, "type")){
				primaryApplication.setType(AffiliateType.valueOf(getStringField(request, "type")));
			}
			if (checkIfValidField(request, "subType")) {
				primaryApplication.getAffiliateDetails().setSubType(AffiliateSubType.valueOf(getStringField(request, "subType")));
			}
			primaryApplication.setOtherText(getStringField(request, "otherText"));
			
			primaryApplication.setSnoMedLicence(Boolean.parseBoolean(getStringField(request, "snoMedTC")));
		}
		
		return application;
	}

	private Member findMemberFromAddressCountry(AffiliateDetails affiliateDetails) {
		Validate.notNull(affiliateDetails.getAddress(), "Address is mandatory");
		Validate.notNull(affiliateDetails.getAddress().getCountry(), "Address country is mandatory");
		Validate.notNull(affiliateDetails.getAddress().getCountry().getMember(), "Address country member is mandatory");
		return affiliateDetails.getAddress().getCountry().getMember();
	}

	private PrimaryApplication findOrStartInitialApplication() {
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		PrimaryApplication application = new PrimaryApplication();
		
		if (applications.size() > 0) {
			// FIXME MLDS-308 is this OK?
			application = (PrimaryApplication) applications.get(0);
		}
		return application;
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
	public ResponseEntity<Application> createApplication(@RequestBody CreateApplicationDTO requestBody) {
		
		// FIXME MLDS-308 it is an error to try to create an extension application without a target member.
		Member member = (requestBody.getMemberKey() != null) ?  memberRepository.findOneByKey(requestBody.getMemberKey()) : userMembershipAccessor.getMemberAssociatedWithUser();
		
		Application application = applicationService.startNewApplication(requestBody.getApplicationType(), member);
		
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
	public ResponseEntity<?> updateApplication(@PathVariable long applicationId, @RequestBody ObjectNode requestBody) throws IOException {
		Application original = applicationRepository.findOne(applicationId);
		Application updatedApplication = constructUpdatedApplication(requestBody, original);
		
		try {
			applicationService.doUpdate(original, updatedApplication);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<String>("Forbidden change to application:" + e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<Application>(original, HttpStatus.OK);
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
