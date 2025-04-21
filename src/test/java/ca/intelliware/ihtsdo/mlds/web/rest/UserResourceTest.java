package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import ca.intelliware.ihtsdo.mlds.config.MySqlTestContainerTest;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.service.UserService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AffiliateDetailsResponseDTO;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

import java.util.Collections;


/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.application.properties")
@ActiveProfiles("dev")
@Transactional
public class UserResourceTest extends MySqlTestContainerTest {

    @Autowired
    private UserRepository userRepository;

    @Mock
    public UserService userService;

    private MockMvc restUserMockMvc;

    UserResource userResource;

    @Before
    public void setup() {
        userResource = new UserResource();
        userResource.userRepository = userRepository;
        userResource.userService = userService;
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserDetailsShouldReturnOk_WhenNoAffiliatesFound() throws Exception {
        String login = "test@test.com";
        Long affiliateDetailsId = 1L;

        User user = new User();
        user.setLogin(login);
        user.setUserId(1L);

        AffiliateDetailsResponseDTO responseDTO = new AffiliateDetailsResponseDTO(user, null, Collections.emptyList());

        when(userService.getAffiliateDetails(login, affiliateDetailsId)).thenReturn(responseDTO);

        restUserMockMvc.perform(post("/api/getUserDetails")
                .param("login", login)
                .param("affiliateDetailsId", affiliateDetailsId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.login").value(login))
            .andExpect(jsonPath("$.affiliateDetails").isEmpty())
            .andExpect(jsonPath("$.affiliate").isEmpty());

        Mockito.verify(userService, Mockito.times(1)).getAffiliateDetails(login, affiliateDetailsId);
    }


    @Test
    public void getUserDetailsShouldReturnOk_WhenUserNotFound() throws Exception {
        String login = "nonexistent@test.com";
        Long affiliateDetailsId = 1L;

        AffiliateDetailsResponseDTO responseDTO = new AffiliateDetailsResponseDTO(null, null, Collections.emptyList());

        when(userService.getAffiliateDetails(login, affiliateDetailsId)).thenReturn(responseDTO);

        restUserMockMvc.perform(post("/api/getUserDetails")
                .param("login", login)
                .param("affiliateDetailsId", affiliateDetailsId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user").isEmpty())
            .andExpect(jsonPath("$.affiliateDetails").isEmpty())
            .andExpect(jsonPath("$.affiliate").isEmpty());

        Mockito.verify(userService, Mockito.times(1)).getAffiliateDetails(login, affiliateDetailsId);
    }
}
