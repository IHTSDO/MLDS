package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationResource;
import ca.intelliware.ihtsdo.mlds.web.rest.Routes;
@RunWith(MockitoJUnitRunner.class)
public class ApplicationUpdateTest {
	private MockMvc mockMvc;
	private ExtensionApplication extensionApplication = new ExtensionApplication();
	
	@Mock
	ApplicationRepository applicationRepository;

	@Before
	public void setup() {

		ApplicationResource applicationResource = new ApplicationResource();
		applicationResource.applicationRepository = applicationRepository;
		applicationResource.objectMapper = new ObjectMapper();

		this.mockMvc = MockMvcBuilders.standaloneSetup(applicationResource).build();
	}

	@Test
	public void userUpdatesReasonOnExtension() throws Exception {
		withApplication(extensionApplication);
		
		updateApplication("{ \"reason\": \"new reason\" }");
		
		assertEquals("new reason", extensionApplication.getReason());
	}

	@Test
	public void newSubmissionInSubmittedState() throws Exception {
		withApplication(extensionApplication);
		extensionApplication.setApprovalState(ApprovalState.NOT_SUBMITTED);
		
		updateApplication("{ \"approvalState\": \"SUBMITTED\"}");
				
		assertEquals(ApprovalState.SUBMITTED, extensionApplication.getApprovalState());
	}
	
	@Test
	public void resubmissionInResubmittedState() throws Exception {
		withApplication(extensionApplication);
		extensionApplication.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		
		updateApplication("{ \"approvalState\": \"SUBMITTED\"}");
		
		assertEquals(ApprovalState.RESUBMITTED, extensionApplication.getApprovalState());
	}
	
	private void withApplication(Application application) {
		Mockito.stub(applicationRepository.findOne(22L)).toReturn(application);
	}

	private void updateApplication(String requestBody) throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
						.post(Routes.APPLICATION, 22L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
						.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
	}

}
