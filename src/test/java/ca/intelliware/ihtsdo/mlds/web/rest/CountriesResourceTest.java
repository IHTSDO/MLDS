package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.application.properties")
@ActiveProfiles("dev")
@Transactional
public class CountriesResourceTest {

    private MockMvc restUserMockMvc;
    
    @Inject
    private ApplicationContext context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CountriesResource countryResource = new CountriesResource();
        context.getAutowireCapableBeanFactory().autowireBean(countryResource);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(countryResource).build();
    }

    @Test
    public void countriesListContainsDenmark() throws Exception {
        restUserMockMvc.perform(get(Routes.COUNTRIES)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[?(@.isoCode2=='DK')]").exists());

    }
    
    @Test
    public void nonAuthenticatedUserCanFetchListOfCountries() throws Exception {
    	restUserMockMvc.perform(get(Routes.COUNTRIES)
    			.accept(MediaType.APPLICATION_JSON_UTF8))
    			.andExpect(status().isOk())
    			.andExpect(content().string(Matchers.not(Matchers.isEmptyOrNullString())));
    	
    }

}
