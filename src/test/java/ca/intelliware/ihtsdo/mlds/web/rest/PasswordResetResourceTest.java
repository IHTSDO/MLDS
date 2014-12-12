package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.PasswordResetService;
import ca.intelliware.ihtsdo.mlds.service.mail.PasswordResetEmailSender;

@RunWith(MockitoJUnitRunner.class)
public class PasswordResetResourceTest {
    @Mock
    UserRepository userRepository;
    
    @Mock
    PasswordResetService passwordResetService;
    
    @Mock
    PasswordResetEmailSender passwordResetEmailSender;
    
	PasswordResetResource passwordResetResource;
	
	private MockMvc restPasswordResetResource;

	@Before
    public void setup() {
        passwordResetResource = new PasswordResetResource();
        
        passwordResetResource.passwordResetService = passwordResetService;
        passwordResetResource.userRepository = userRepository;
        passwordResetResource.passwordResetEmailSender = passwordResetEmailSender;
        
        this.restPasswordResetResource = MockMvcBuilders.standaloneSetup(passwordResetResource).build();
    }

	@Test
	public void requestPasswordResetShouldFailIfEmailNotIncluded() throws Exception {
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET)
				.content("{\"email\": null}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.containsString("no email")));
		
		Mockito.verify(passwordResetEmailSender, Mockito.never()).sendPasswordResetEmail(Mockito.any(User.class), Mockito.anyString());
	}

	@Test
	public void requestPasswordResetShouldFailIfEmailNotRegistered() throws Exception {
		Mockito.when(userRepository.getUserByEmailIgnoreCase("unknown@email.com")).thenReturn(null);
		
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET)
				.content("{\"email\": \"unknown@email.com\"}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
		
		Mockito.verify(passwordResetEmailSender, Mockito.never()).sendPasswordResetEmail(Mockito.any(User.class), Mockito.anyString());
	}

	@Test
	public void requestPasswordResetShouldTriggerResetEmailForKnownEmail() throws Exception {
		Mockito.when(userRepository.getUserByEmailIgnoreCase("test@email.com")).thenReturn(new User());
		
		Mockito.when(passwordResetService.createTokenForUser(Mockito.any(User.class))).thenReturn("TEST-TOKEN");
		
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET)
				.content("{\"email\": \"test@email.com\"}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(passwordResetEmailSender, Mockito.atLeastOnce()).sendPasswordResetEmail(Mockito.any(User.class), Mockito.matches("TEST-TOKEN"));
	}

	@Test
	public void resetPasswordShouldFailIfPasswordNotIncluded() throws Exception {
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET_ITEM, "TEST-TOKEN")
				.content("{\"password\": null}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.containsString("no password")))
                ;
		
		Mockito.verify(passwordResetService, Mockito.never()).resetPassword(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void resetPasswordShouldResetPassword() throws Exception {
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET_ITEM, "TEST-TOKEN")
				.content("{\"password\": \"new-password\"}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(passwordResetService, Mockito.atLeastOnce()).resetPassword("TEST-TOKEN", "new-password");
	}

	@Test
	public void resetPasswordShouldFailForBadTokenResetPassword() throws Exception {
		Mockito.doThrow(new IllegalArgumentException()).when(passwordResetService).resetPassword(Mockito.anyString(),  Mockito.anyString());
		
		restPasswordResetResource.perform(MockMvcRequestBuilders.post(Routes.PASSWORD_RESET_ITEM, "BAD-TOKEN")
				.content("{\"password\": \"new-password\"}")
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
		
		Mockito.verify(passwordResetService, Mockito.atLeastOnce()).resetPassword("BAD-TOKEN", "new-password");
	}
}
