package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.AuditEventService;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipCalculator;
import ca.intelliware.ihtsdo.mlds.service.mail.AnnouncementEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.AnnouncementDTO;

public class AnnouncementResourceTest {

	@Mock MemberRepository memberRepository;
	@Mock AnnouncementAuthorizationChecker announcementAuthorizationChecker;
	@Mock AnnouncementEmailSender announcementEmailSender;
	@Mock UserMembershipCalculator userMembershipCalculator;
	@Mock AuditEventService auditEventService;
	
	SecurityContextSetup securityContextSetup = new SecurityContextSetup();
	
	private MockMvc restAnnouncementResource;

	private Member ihtsdoMember;

	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        AnnouncementResource announcementResource = new AnnouncementResource();
        announcementResource.announcementAuthorizationChecker = announcementAuthorizationChecker;
        announcementResource.announcementEmailSender = announcementEmailSender;
        announcementResource.userMembershipCalculator = userMembershipCalculator;
        announcementResource.auditEventService = auditEventService;
        
        MockMvcJacksonTestSupport mockMvcJacksonTestSupport = new MockMvcJacksonTestSupport();
        mockMvcJacksonTestSupport.memberRepository = memberRepository;

		restAnnouncementResource = MockMvcBuilders
				.standaloneSetup(announcementResource)
        		.setMessageConverters(mockMvcJacksonTestSupport.getConfiguredMessageConverters())
        		.build();
		
		ihtsdoMember = withMember("IHTSDO", 1L);
		
		securityContextSetup.asAdmin();
    }

	private Member withMember(String key, long memberId) {
		Member member = new Member(key, memberId);
		Mockito.when(memberRepository.findOneByKey(key)).thenReturn(member);
		return member;
	}

	@Test
	public void postAnnouncementShouldCheckAccess() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		Mockito.doThrow(new IllegalStateException("not allowed")).when(announcementAuthorizationChecker).checkCanPostAnnouncement(Mockito.any(AnnouncementDTO.class));
	
		try {
			restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
	        		.contentType(MediaType.APPLICATION_JSON_UTF8)
	        		.content("{ \"member\": { \"key\":\"OT\"} }")
	                .accept(MediaType.APPLICATION_JSON_UTF8))
					.andExpect(status().is5xxServerError());
	    	Assert.fail();
	    } catch (NestedServletException e) {
	    	Assert.assertThat(e.getRootCause().getMessage(), Matchers.containsString("not allowed"));
	    }
	}
	
	@Test
	public void postAnnouncementShouldSendEmailsToMemberUsers() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		User user = new User();
		user.setUserId(1L);
		user.setEmail("user@test.com");
		
		Mockito.when(userMembershipCalculator.approvedActiveUsersWithHomeMembership(ihtsdoMember)).thenReturn(Arrays.asList(user));
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		Mockito.verify(announcementEmailSender, times(1)).sendAnnouncementEmail("user@test.com", ihtsdoMember, "test title", "test message");
	}

	@Test
	public void postAnnouncementForAllAffiliatesShouldSendEmailsToMemberUsers() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		User user = new User();
		user.setUserId(1L);
		user.setEmail("user@test.com");
		
		Mockito.when(userMembershipCalculator.approvedActiveUsers()).thenReturn(Arrays.asList(user));
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\", \"allAffiliates\": true }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		Mockito.verify(announcementEmailSender, times(1)).sendAnnouncementEmail("user@test.com", ihtsdoMember, "test title", "test message");
	}

	@Test
	public void postAnnouncementShouldSendEmailsToAdditionalAddresses() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		User user = new User();
		user.setUserId(1L);
		
		Mockito.when(userMembershipCalculator.approvedActiveUsersWithHomeMembership(ihtsdoMember)).thenReturn(Collections.<User>emptyList());
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\", \"additionalEmails\": [ \"email1@test.com\", \"email2@test.com\" ] }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		Mockito.verify(announcementEmailSender, times(1)).sendAnnouncementEmail("email1@test.com", ihtsdoMember, "test title", "test message");
		Mockito.verify(announcementEmailSender, times(1)).sendAnnouncementEmail("email2@test.com", ihtsdoMember, "test title", "test message");
	}

	@Test
	public void postAnnouncementShouldSendEmailsToAdditionalAddressesSkippingBlanks() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		User user = new User();
		user.setUserId(1L);
		
		Mockito.when(userMembershipCalculator.approvedActiveUsersWithHomeMembership(ihtsdoMember)).thenReturn(Collections.<User>emptyList());
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\", \"additionalEmails\": [ \"   \", \"\" ] }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		Mockito.verify(announcementEmailSender, never()).sendAnnouncementEmail(Mockito.anyString(), Mockito.any(Member.class), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void postAnnouncementShouldLog() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		Mockito.when(userMembershipCalculator.approvedActiveUsersWithHomeMembership(ihtsdoMember)).thenReturn(Collections.<User>emptyList());
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\", \"additionalEmails\": [ \"email1@test.com\", \"email2@test.com\" ] }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		HashMap<String, String> expected = new HashMap<String,String>();
		expected.put("announcement.member", "IHTSDO");
		expected.put("announcement.title", "test title");
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("ANNOUNCEMENT_POSTED"), Mockito.eq(expected));
	}

	@Test
	public void postAnnouncementShouldLogAllAffiliates() throws Exception {
		securityContextSetup.asIHTSDOStaff();
		
		Mockito.when(userMembershipCalculator.approvedActiveUsers()).thenReturn(Collections.<User>emptyList());
		
		restAnnouncementResource.perform(post(Routes.ANNOUNCEMENTS)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content("{ \"member\": { \"key\":\"IHTSDO\"}, \"subject\":\"test title\", \"body\":\"test message\", \"allAffiliates\": true }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
		
		HashMap<String, String> expected = new HashMap<String,String>();
		expected.put("announcement.member", "IHTSDO");
		expected.put("announcement.title", "test title");
		expected.put("announcement.allAffiliates", "All");
		
		Mockito.verify(auditEventService).logAuditableEvent(Mockito.eq("ANNOUNCEMENT_POSTED"), Mockito.eq(expected));
	}

}
