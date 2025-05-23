package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.ApplicationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUpdateTest {
	private MockMvc mockMvc;
	private ExtensionApplication extensionApplication = new ExtensionApplication();

	@Mock
	ApplicationRepository applicationRepository;

	@Mock
	ApplicationAuditEvents applicationAuditEvents;

	@Mock ApplicationApprovalStateChangeNotifier applicationApprovalStateChangeNotifier;

	@Before
	public void setup() {

		ApplicationResource applicationResource = new ApplicationResource();
		applicationResource.applicationRepository = applicationRepository;
		applicationResource.applicationAuditEvents = applicationAuditEvents;
		applicationResource.applicationApprovalStateChangeNotifier = applicationApprovalStateChangeNotifier;
		applicationResource.objectMapper = new ObjectMapper();
        applicationResource.objectMapper.registerModule(new JavaTimeModule());
		applicationResource.applicationService = new ApplicationService();

		this.mockMvc = MockMvcBuilders
				.standaloneSetup(applicationResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
	}

	@Test
	public void userUpdatesReasonOnExtension() throws Exception {
		withAuthorities(AuthoritiesConstants.USER_ONLY);
		withApplication(extensionApplication);
		makeUpdateRequest("{ \"reason\": \"new reason\" }")
		.andExpect(status().isOk());

		assertEquals("new reason", extensionApplication.getReason());
	}

	@Test
	public void newSubmissionInSubmittedState() throws Exception {
		withAuthorities(AuthoritiesConstants.USER_ONLY);
		withApplication(extensionApplication);
		extensionApplication.setApprovalState(ApprovalState.NOT_SUBMITTED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
		makeUpdateRequest("{ \"approvalState\": \"SUBMITTED\"}")
		.andExpect(status().isOk());

		assertEquals(ApprovalState.SUBMITTED, extensionApplication.getApprovalState());
	}

	@Test
	public void rejectionStatusCanNotBeChangedByUser() throws Exception {
		withAuthorities(AuthoritiesConstants.USER_ONLY);
		withApplication(extensionApplication);
		extensionApplication.setApprovalState(ApprovalState.REJECTED);

		makeUpdateRequest("{ \"approvalState\": \"SUBMITTED\"}")
		.andExpect(status().isConflict());
	}

	private void withAuthorities(String[] userOnly) {
		// noop until we support staff
	}

	private void withApplication(Application application) {
		Mockito.when(applicationRepository.findById(22L)).thenReturn(Optional.ofNullable(application));
	}

	private ResultActions makeUpdateRequest(String requestBody)
			throws Exception {
		return mockMvc.perform(
				MockMvcRequestBuilders
						.post(Routes.APPLICATION, 22L)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON_UTF8));
	}

}
