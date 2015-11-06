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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateSearchRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
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
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}

	@Test
	public void getAffiliatesShouldSortByAffiliateIdByDefault() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
        
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAll(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldBeSortable() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$orderby", "member")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
        
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAll(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("homeMember.key: ASC,affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldBeSortableDescending() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$orderby", "member desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
        
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAll(argument.capture());
		assertThat(argument.getValue().getSort().toString(), is("homeMember.key: DESC,affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldStartOnFirstPageByDefault() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
        
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAll(argument.capture());
		assertThat(argument.getValue().getPageNumber(), is(0));
		assertThat(argument.getValue().getPageSize(), is(50));
		assertThat(argument.getValue().getSort().toString(), is("affiliateId: ASC"));
	}

	@Test
	public void getAffiliatesShouldAllowPaging() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$page", "2")
        		.param("$pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
        
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        verify(affiliateRepository).findAll(argument.capture());
		assertThat(argument.getValue().getPageNumber(), is(2));
		assertThat(argument.getValue().getPageSize(), is(20));
	}


	@Test
	public void getAffiliatesShouldFilterByQuery() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateSearchRepository.findFullTextAndMember(Mockito.eq("test"), Mockito.eq((Member)null), Mockito.eq((StandingState)null), Mockito.eq(false), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("q", "test")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}

	@Test
	public void getAffiliatesShouldFilterByStandingState() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findByStandingState(Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}

	@Test
	public void getAffiliatesShouldFilterByStandingStateNot() throws Exception {
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findByStandingStateNot(Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "not standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}
	
	@Test
	public void getAffiliatesShouldFilterByHomeMember() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);
		
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findByHomeMember(Mockito.eq(member), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}

	@Test
	public void getAffiliatesShouldFilterByHomeMemberAndStandingState() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);
		
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findByHomeMemberAndStandingState(Mockito.eq(member), Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
        		.param("$filter", "standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}

	@Test
	public void getAffiliatesShouldFilterByHomeMemberAndStandingStateNot() throws Exception {
		Member member = new Member("SE", 1L);
		when(memberRepository.findOneByKey("SE")).thenReturn(member);
		
    	Affiliate affiliate = createBlankAffiliate();
    	affiliate.setCreator("creator0");
    	
    	PageImpl<Affiliate> matches = new PageImpl<>(Arrays.asList(affiliate));
    	when(affiliateRepository.findByHomeMemberAndStandingStateNot(Mockito.eq(member), Mockito.eq(StandingState.APPLYING), Mockito.any(Pageable.class))).thenReturn(matches);

        restUserMockMvc.perform(get(Routes.AFFILIATES)
        		.param("$filter", "homeMember eq 'SE'")
        		.param("$filter", "not standingState eq 'APPLYING'")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].creator").value("creator0"));
	}


	private Affiliate createBlankAffiliate() {
    	Affiliate affiliate = new Affiliate();
    	
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
