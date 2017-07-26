package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.FileRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

public class MemberResourceTest {

	@Mock private MemberRepository memberRepository;
	@Mock private FileRepository fileRepository;
	@Mock private SessionService sessionService;
	
	private MockMvc restUserMockMvc;
	
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MemberResource memberResource = new MemberResource();
        
        memberResource.memberRepository = memberRepository;
        memberResource.fileRepository = fileRepository;
        memberResource.sessionService = sessionService;
        
        this.restUserMockMvc = MockMvcBuilders
        		.standaloneSetup(memberResource)
        		.setMessageConverters(new MockMvcJacksonTestSupport().getConfiguredMessageConverters())
        		.build();
        
        Mockito.when(memberRepository.findAll()).thenReturn(Arrays.asList(new Member("SE", 1), new Member("IHTSDO", 2)));
	}
	
    @Test
    public void getMembersShouldIncludeAllMembers() throws Exception {
    	restUserMockMvc.perform(get(Routes.MEMBERS)
    			.accept(MediaType.APPLICATION_JSON_UTF8))
    			.andExpect(status().isOk())
    			.andExpect(content().string(Matchers.allOf(Matchers.containsString("SE"), Matchers.containsString("IHTSDO"))));
    	
    }

	@Test
	public void getMembersShouldReturnPopulatedDTOs() throws Exception {
        restUserMockMvc.perform(get(Routes.MEMBERS)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"memberId\":1,\"key\":\"SE\"")));
	}
	
	@Test
	public void postMembersNotificationsShouldUpdateStaffNotifications() throws Exception {
		Member member = new Member("SE", 1L);
		Mockito.when(memberRepository.findOneByKey("SE")).thenReturn(member);
		
        restUserMockMvc.perform(put(Routes.MEMBER_NOTIFICATIONS, "SE")
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"staffNotificationEmail\": \"staff@test.com\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        
        Mockito.verify(memberRepository).save(member);
        assertThat(member.getStaffNotificationEmail(), equalTo("staff@test.com"));
	}
	
	@Test
	public void updateMemberShouldFailForUnknownMember() throws Exception {
        restUserMockMvc.perform(put(Routes.MEMBER, "ZZ")
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"staffNotificationEmail\": \"new@test.com\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNotFound());
        
        Mockito.verify(memberRepository, never()).save(Mockito.any(Member.class));
	}

	@Test
	public void updateMemberShouldUpdateFields() throws Exception {
		Member member = new Member("SE", 1L);
		member.setStaffNotificationEmail("old@test.com");
		member.setPromotePackages(false);
		Mockito.when(memberRepository.findOneByKey("SE")).thenReturn(member);
		
        restUserMockMvc.perform(put(Routes.MEMBER, "SE")
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"promotePackages\": true, \"staffNotificationEmail\": \"new@test.com\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        
        Mockito.verify(memberRepository).save(member);
        
        assertThat(member.getStaffNotificationEmail(), equalTo("new@test.com"));
        assertThat(member.getPromotePackages(), equalTo(true));
	}
	
	
}
