package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.AffiliateAuditEvents;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.RouteLinkBuilder;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationResource.CreateApplicationDTO;

public class ApplicationResource_CreateApplication_Test {
	private MockMvc mockMvc;
	
	@Mock
	ApplicationRepository applicationRepository;
	
	@Mock
	ApplicationAuditEvents applicationAuditEvents;
	
	@Mock
	ApplicationAuthorizationChecker applicationAuthorizationChecker;
	
	@Mock
	MemberRepository memberRepository;
	
	@Mock
	ApplicationService applicationService;
	
	
	ApplicationResource applicationResource;
	
	Member sweden;
	
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
        
        applicationResource = new ApplicationResource();
        
        applicationResource.applicationRepository = applicationRepository;
        applicationResource.applicationAuditEvents = applicationAuditEvents;
        applicationResource.authorizationChecker = applicationAuthorizationChecker;
        applicationResource.applicationService = applicationService;
        applicationResource.memberRepository = memberRepository;
        
        applicationResource.routeLinkBuilder = new RouteLinkBuilder();

        this.mockMvc = MockMvcBuilders.standaloneSetup(applicationResource).build();

		sweden = new Member("SE", 1);
		Mockito.when(memberRepository.findOneByKey("SE")).thenReturn(sweden);

	}

	@Test
	public void createApplicationShouldFailWhenCheckerDoesNotPass() throws Exception {
		Mockito.doThrow(new IllegalStateException("ACCOUNT DEACTIVATED")).when(applicationAuthorizationChecker).checkCanCreateApplication(Mockito.any(CreateApplicationDTO.class));

		try {
			postRequestForStartSwedishExtension()
				.andExpect(status().is5xxServerError());
			Assert.fail();
        } catch (NestedServletException e) {
        	Assert.assertThat(e.getRootCause().getMessage(), Matchers.containsString("ACCOUNT DEACTIVATED"));
        }
	}

	@Test
	public void createApplicationShouldStartNewApplication() throws Exception {
		withNewSwedishExtensionApplication(2);

		postRequestForStartSwedishExtension()
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.applicationId").value(2));
	}

	@Test
	public void createApplicationShouldLogNewApplication() throws Exception {
		withNewSwedishExtensionApplication(2);

		postRequestForStartSwedishExtension()
			.andExpect(status().isCreated());
		
		Mockito.verify(applicationAuditEvents).logCreationOf(Mockito.any(ExtensionApplication.class));
	}

	@Test
	public void createApplicationShouldIncludeResourceUrlOfNewApplication() throws Exception {
		withNewSwedishExtensionApplication(2);

		postRequestForStartSwedishExtension()
			.andExpect(status().isCreated())
			.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/applications/2")));
	}

	private ResultActions postRequestForStartSwedishExtension() throws Exception {
		return mockMvc.perform(
				MockMvcRequestBuilders
						.post(Routes.APPLICATIONS)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"applicationType\": \"EXTENSION\", \"memberKey\":\"SE\" }")
						.accept(MediaType.APPLICATION_JSON));
	}

	private void withNewSwedishExtensionApplication(int applicationId) {
		ExtensionApplication application = new ExtensionApplication(applicationId);
		Mockito.when(applicationService.startNewApplication(ApplicationType.EXTENSION, sweden)).thenReturn(application);
	}
	
}
