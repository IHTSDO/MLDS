package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

public class AffiliatePublicResourceTest {
	@Mock AffiliateRepository affiliateRepository;
	@Mock MemberRepository memberRepository;
	
	private MockMvc restAffiliatePublicResource;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        AffiliatePublicResource affiliatePublicResource = new AffiliatePublicResource();
        
        affiliatePublicResource.affiliateRepository = affiliateRepository;
        affiliatePublicResource.memberRepository = memberRepository;
        
        restAffiliatePublicResource = MockMvcBuilders
        		.standaloneSetup(affiliatePublicResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
    }
	
	@Test
	public void memberKeyShouldByMandatory() throws Exception {
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Missing mandatory parameter: member")));
	}

	@Test
	public void matchShouldByMandatory() throws Exception {
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("member", "se")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Missing mandatory parameter: match")));
	}

	@Test
	public void matchShouldByMandatoryAndBeAtLeast3CharsInLength() throws Exception {
		withMember("se", 1L);
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "sm")
					.param("member", "se")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Match parameter value 'sm' was shorter than the minimum length: 3")));
	}

	@Test
	public void unknownMemberShouldProduceErrorAndListOptions() throws Exception {
		Mockito.when(memberRepository.findAll()).thenReturn(Arrays.asList(new Member("us", 10L), new Member("es",11L)));
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "xy")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Unknown member: xy")))
		.andExpect(jsonPath("$.error.message", containsString("options: es us")));
	}

	@Test
	public void nonLongAffiliateIdShouldProduceError() throws Exception {
		withMember("se", 1L);
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.param("affiliateId", "word")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Illegal affiliateId value: word")));
	}

	@Test
	public void unknownAffiliateIdShouldProduceError() throws Exception {
		withMember("se", 1L);
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.message", containsString("Unknown affiliateId: 123")));
	}

	@Test
	public void affiateIdMatchOnOrganizationNameShouldPass() throws Exception {
		withMember("se", 1L);
		
		Affiliate affiliate = withAffiliate(123L);
		affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
		affiliate.getAffiliateDetails().setOrganizationName("Test Hospital");
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(true));
	}

	@Test
	public void affiateIdNameMatchShouldBeContainsAndCaseInsensitiveOrganizationNameShouldPass() throws Exception {
		withMember("se", 1L);
		
		Affiliate affiliate = withAffiliate(123L);
		affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
		affiliate.getAffiliateDetails().setOrganizationName("Test Hospital");
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "HOSP")
					.param("member", "se")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(true));
	}

	@Test
	public void affiateIdWithNonMatchShouldBeFalse() throws Exception {
		withMember("se", 1L);
		
		Affiliate affiliate = withAffiliate(123L);
		affiliate.setStandingState(StandingState.IN_GOOD_STANDING);
		affiliate.getAffiliateDetails().setOrganizationName("test hospital");
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "ANOTHER PLACE")
					.param("member", "se")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(false));
	}

	@Test
	public void affiliateIdWithBadStandingStateShouldFail() throws Exception {
		withMember("se", 1L);
		
		Affiliate affiliate = withAffiliate(123L);
		affiliate.setStandingState(StandingState.DEACTIVATED);
		affiliate.getAffiliateDetails().setOrganizationName("Test Hospital");
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.param("affiliateId", "123")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(false));
	}

	private Affiliate withAffiliate(long affiliateId) {
		Affiliate affiliate = new Affiliate();
		AffiliateDetails affiliateDetails = new AffiliateDetails();
		affiliate.setAffiliateDetails(affiliateDetails);
		Mockito.when(affiliateRepository.findOne(affiliateId)).thenReturn(affiliate);
		return affiliate;
	}

	private Member withMember(String memberKey, long memberId) {
		Member member = new Member(memberKey, memberId);
		Mockito.when(memberRepository.findOneByKey(memberKey)).thenReturn(member);
		return member;
	}

}
