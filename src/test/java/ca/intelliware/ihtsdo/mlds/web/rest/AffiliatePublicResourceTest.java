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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
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
		.andExpect(jsonPath("$.error", containsString("Bad Request")))
		.andExpect(jsonPath("$.message", containsString("Missing mandatory parameter: member")));
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
		.andExpect(jsonPath("$.error", containsString("Bad Request")))
		.andExpect(jsonPath("$.message", containsString("Missing mandatory parameter: match")));
	}

	@Test
	public void matchShouldByBeAtLeast3CharsInLength() throws Exception {
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
		.andExpect(jsonPath("$.error", containsString("Bad Request")))
		.andExpect(jsonPath("$.message", containsString("Match parameter value: 'sm' was shorter than the minimum length: 3")));
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
		.andExpect(jsonPath("$.error", containsString("Bad Request")))
		.andExpect(jsonPath("$.message", containsString("Unknown member: 'xy'")))
		.andExpect(jsonPath("$.message", containsString("options: es us")));
	}

	@Test
	public void nonLongAffiliateIdShouldReturnFalse() throws Exception {
		withMember("se", 1L);
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.param("affiliateId", "word")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.matched").value(false));
	}

	@Test
	public void affiateIdWithGoodMatchShouldReturnTrue() throws Exception {
		Member member = withMember("se", 1L);
		
		Mockito.when(affiliateRepository.findForCheck(Mockito.eq(123L), Mockito.eq(member), Mockito.eq("test hospital"), Mockito.any(PageRequest.class))).thenReturn(pageResult(createAffiliate(1L)));
		
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
	public void affiateIdWithBadMatchShouldReturnFalse() throws Exception {
		Member member = withMember("se", 1L);
		
		Mockito.when(affiliateRepository.findForCheck(Mockito.eq(123L), Mockito.eq(member), Mockito.eq("test hospital"), Mockito.any(PageRequest.class))).thenReturn(pageResult());
		
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
	
	@Test
	public void anyAffiliateWithSingleMatchShouldReturnTrue() throws Exception {
		Member member = withMember("se", 1L);
		
		Mockito.when(affiliateRepository.findForCheck(Mockito.eq(-1L), Mockito.eq(member), Mockito.eq("test hospital"), Mockito.any(PageRequest.class))).thenReturn(pageResult(createAffiliate(1L)));
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(true));
	}

	@Test
	public void anyAffiliateWithNoMatchesShouldReturnFalse() throws Exception {
		Member member = withMember("se", 1L);
		
		Mockito.when(affiliateRepository.findForCheck(Mockito.eq(-1L), Mockito.eq(member), Mockito.eq("test hospital"), Mockito.any(PageRequest.class))).thenReturn(pageResult());
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(false));
	}

	@Test
	public void anyAffiliateWithMultipleatchesShouldReturnFalse() throws Exception {
		Member member = withMember("se", 1L);
		
		Mockito.when(affiliateRepository.findForCheck(Mockito.eq(-1L), Mockito.eq(member), Mockito.eq("test hospital"), Mockito.any(PageRequest.class))).thenReturn(pageResult(createAffiliate(1L), createAffiliate(2L)));
		
		restAffiliatePublicResource.perform(
				MockMvcRequestBuilders
					.get(Routes.AFFILIATES_CHECK)
					.param("match", "test hospital")
					.param("member", "se")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.matched").value(false));
	}

	private Page<Affiliate> pageResult(Affiliate... affiliates) {
		return new PageImpl<Affiliate>(Arrays.asList(affiliates));
	}

	private Affiliate createAffiliate(long id) {
		return new Affiliate(id);
	}


	private Member withMember(String memberKey, long memberId) {
		Member member = new Member(memberKey, memberId);
		Mockito.when(memberRepository.findOneByKey(memberKey)).thenReturn(member);
		return member;
	}

}
