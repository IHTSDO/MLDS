package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import ca.intelliware.ihtsdo.mlds.domain.ExtensionApplication;
import ca.intelliware.ihtsdo.mlds.repository.*;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class AffiliateResource_AffiliatesFilter_Test {

    @Mock
    private ApplicationAuthorizationChecker applicationAuthorizationChecker;

    @Mock
    private AffiliateRepository affiliateRepository;

    @Mock
    private AffiliateSearchRepository affiliateSearchRepository;

    @Mock
    private AffiliateDetailsRepository affiliateDetailsRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private UserRepository userRepository;

    private MockMvc restUserMockMvc;

    SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        AffiliateResource affiliateResource = new AffiliateResource();

        affiliateResource.affiliateDetailsRepository = affiliateDetailsRepository;
        affiliateResource.affiliateRepository = affiliateRepository;
        affiliateResource.affiliateSearchRepository = affiliateSearchRepository;
        affiliateResource.applicationAuthorizationChecker = applicationAuthorizationChecker;
        affiliateResource.userRepository = userRepository;
        affiliateResource.sessionService = sessionService;
        affiliateResource.memberRepository = memberRepository;
        affiliateResource.currentSecurityContext = new CurrentSecurityContext();

        securityContextSetup.asAdmin();

		this.restUserMockMvc = MockMvcBuilders
				.standaloneSetup(affiliateResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
    }

	@Test
	public void getAffiliates() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.affiliates", Matchers.hasSize(1)))
                .andExpect(contentMatchesTestCreator());
	}

	private PageImpl<Affiliate> withAffiliateResultForTestCreator() {
		Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("testCreator");
        affiliate.setDeactivated(false);

    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
		return matches;
	}

	private ResultMatcher contentMatchesTestCreator() {
		return jsonPath("$.affiliates.[0].creator").value("testCreator");
	}

	@Test
	public void getAffiliatesCanReturnNoResults() throws Exception {
    	PageImpl<Affiliate> matches = new PageImpl<>(Lists.<Affiliate>emptyList());
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.affiliates", Matchers.hasSize(0)));
	}

	@Test
	public void getAffiliatesShouldSortByAffiliateIdByDefault() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());

        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAllByDeactivatedFalse(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldBeSortable() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$orderby", "member")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());

        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAllByDeactivatedFalse(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("homeMember.key: ASC,affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldBeSortableDescending() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$orderby", "member desc")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());

        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAllByDeactivatedFalse(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("homeMember.key: DESC,affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldStartOnFirstPageByDefault() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());

        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAllByDeactivatedFalse(argument.capture());
		assertThat(argument.getValue().getPageNumber(), is(0));
		assertThat(argument.getValue().getPageSize(), is(50));
		assertThat(argument.getValue().getSort().toString(), is("affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldAllowPaging() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findAllByDeactivatedFalse(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$page", "2")
        		.param("$pageSize", "20")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());

        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAllByDeactivatedFalse(argument.capture());
		assertThat(argument.getValue().getPageNumber(), is(2));
		assertThat(argument.getValue().getPageSize(), is(20));
	}


	@Test
	public void getAffiliatesShouldFilterByQuery() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateSearchRepository.findFullTextAndMember(Mockito.eq("test"), Mockito.eq((Member)null), Mockito.eq((StandingState)null), Mockito.eq(false), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("q", "test")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldFilterByStandingState() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findByStandingStateAndDeactivatedFalse(Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldFilterByStandingStateNot() throws Exception {
    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findByStandingStateNotAndDeactivatedFalse(Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "not standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldFilterByHomeMember() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);

    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findByHomeMember(Mockito.eq(member), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldFilterByHomeMemberAndStandingState() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);

    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findByHomeMemberAndStandingStateAndDeactivatedFalse(Mockito.eq(member), Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
        		.param("$filter", "standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldFilterByHomeMemberAndStandingStateNot() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);

    	PageImpl<Affiliate> matches = withAffiliateResultForTestCreator();
    	when(affiliateRepository.findByHomeMemberAndStandingStateNotAndDeactivatedFalse(Mockito.eq(member), Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
        		.param("$filter", "not standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(contentMatchesTestCreator());
	}

	@Test
	public void getAffiliatesShouldReturnFullTotalCountAcrossPageBoundaryForMemberWithMultipleApplications() throws Exception {
		Member member = new Member("DK", 1L);
		when(memberRepository.findOneByKey("DK")).thenReturn(member);
        
		List<Affiliate> affiliatesPage0 = Lists.newArrayList();
		for (long i = 1; i <= 50; i++) {
			Affiliate affiliate = createBlankAffiliate(i);
			affiliate.setCreator("creator" + i);
			affiliate.setHomeMember(member);
			if (i == 1) {
				ExtensionApplication extApp1 = new ExtensionApplication(101L);
				extApp1.setMember(member);
				affiliate.addApplication(extApp1);

				ExtensionApplication extApp2 = new ExtensionApplication(102L);
				extApp2.setMember(member);
				affiliate.addApplication(extApp2);
			}
			affiliatesPage0.add(affiliate);
		}

		PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Order.asc("affiliateId")));
		long totalAffiliates = 177L;
		PageImpl<Affiliate> pageResult = new PageImpl<>(affiliatesPage0, pageRequest, totalAffiliates);
		when(affiliateRepository.findByHomeMember(Mockito.eq(member), Mockito.any(Pageable.class))).thenReturn(pageResult);

		restUserMockMvc.perform(get(Routes.AFFILIATES)
				.param("$filter", "homeMember eq 'DK'")
				.param("$page", "0")
				.param("$pageSize", "50")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.affiliates", Matchers.hasSize(50)))
				.andExpect(jsonPath("$.totalResults", is(177)))
				.andExpect(jsonPath("$.totalPages", is(4)));
	}


	private Affiliate createBlankAffiliate() {
		return createBlankAffiliate(null);
	}

	private Affiliate createBlankAffiliate(Long affiliateId) {
		Affiliate affiliate = affiliateId != null ? new Affiliate(affiliateId) : new Affiliate();

    	AffiliateDetails affiliateDetails = new AffiliateDetails();
    	affiliateDetails.setAddress(new MailingAddress());
    	affiliateDetails.setBillingAddress(new MailingAddress());
    	affiliate.setAffiliateDetails(affiliateDetails);

    	PrimaryApplication application = new PrimaryApplication(1L);
    	application.setApprovalState(ApprovalState.APPROVED);
    	affiliate.setApplication(application);

    	return affiliate;
    }

}
