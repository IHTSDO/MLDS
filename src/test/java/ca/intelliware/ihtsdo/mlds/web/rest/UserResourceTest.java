package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;
import javax.transaction.Transactional;

import ca.intelliware.ihtsdo.mlds.config.PostgresTestContainerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.repository.UserRepository;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.application.properties")
@ActiveProfiles("dev")
@Transactional
public class UserResourceTest extends PostgresTestContainerTest {

    @Inject
    private UserRepository userRepository;

    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }
}
