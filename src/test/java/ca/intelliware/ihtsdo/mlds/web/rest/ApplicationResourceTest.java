package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationResource.CreateApplicationDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ApplicationResourceTest {
	private MockMvc mockMvc;
	
	@Mock
	ApplicationRepository applicationRepository;
	
	@Mock
	ApplicationAuditEvents applicationAuditEvents;
	
	@Mock
	ApplicationAuthorizationChecker applicationAuthorizationChecker;

	ApplicationResource applicationResource;
	
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
        
        applicationResource = new ApplicationResource();
        
        applicationResource.applicationRepository = applicationRepository;
        applicationResource.applicationAuditEvents = applicationAuditEvents;
        applicationResource.authorizationChecker = applicationAuthorizationChecker;

        this.mockMvc = MockMvcBuilders.standaloneSetup(applicationResource).build();

	}

	@Test
	public void createApplicationShouldFailWhenCheckerDoesNotPass() throws Exception {
		Mockito.doThrow(new IllegalStateException("ACCOUNT DEACTIVATED")).when(applicationAuthorizationChecker).checkCanCreateApplication(Mockito.any(CreateApplicationDTO.class));

		try {
			mockMvc.perform(
					MockMvcRequestBuilders
							.post(Routes.APPLICATIONS)
							.contentType(MediaType.APPLICATION_JSON)
							.content("{ \"applicationType\": \"EXTENSION\", \"memberKey\":\"SE\" }")
							.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().is5xxServerError())
							;
			Assert.fail();
        } catch (NestedServletException e) {
        	Assert.assertThat(e.getRootCause().getMessage(), Matchers.containsString("ACCOUNT DEACTIVATED"));
        }
	}

}
