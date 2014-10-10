package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseFileRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleasePackageRepository;
import ca.intelliware.ihtsdo.mlds.repository.ReleaseVersionRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

@RunWith(MockitoJUnitRunner.class)
public class ReleasePackagesResourceTest {

    private MockMvc restReleasePackagesResource;

	@Mock
	ReleasePackageRepository releasePackageRepository;

	@Mock
	ReleaseVersionRepository releaseVersionRepository;

	@Mock
	ReleaseFileRepository releaseFileRepository;

	@Mock
	ReleasePackageAuthorizationChecker authorizationChecker;
	
    @Mock
    private UserMembershipAccessor userMembershipAccessor;
	
	CurrentSecurityContext currentSecurityContext = new CurrentSecurityContext();

	@Mock
	ReleasePackageAuditEvents releasePackageAuditEvents;
	
	@Mock
	MemberRepository memberRepository;
	
	@Captor
	ArgumentCaptor<ReleasePackage> releasePacakgeCaptor;
	
	ReleasePackagesResource releasePackagesResource;

	SecurityContextSetup securityContextSetup = new SecurityContextSetup();


	@Before
    public void setup() {
        releasePackagesResource = new ReleasePackagesResource();
        
        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.releasePackageAuditEvents = releasePackageAuditEvents;
        releasePackagesResource.userMembershipAccessor = userMembershipAccessor;
        
        Mockito.stub(userMembershipAccessor.getMemberAssociatedWithUser()).toReturn(new Member("IHTSDO", 1));

        MockMvcJacksonTestSupport mockMvcJacksonTestSupport = new MockMvcJacksonTestSupport();
        mockMvcJacksonTestSupport.memberRepository = memberRepository;
		this.restReleasePackagesResource = MockMvcBuilders
        		.standaloneSetup(releasePackagesResource)
        		.setMessageConverters(mockMvcJacksonTestSupport.getConfiguredMessageConverters())
        		.build();
    }

	@Test
	public void testReleasePackageCreateSavesRecord() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageCreateIgnoresBodyMemberAndAttachesPackageToUserMember() throws Exception {
        Member userMember = new Member("SE", 1);
		Mockito.stub(userMembershipAccessor.getMemberAssociatedWithUser()).toReturn(userMember);
        
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\", \"member\": { \"key\": \"DK\" } }")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(releasePacakgeCaptor.getValue().getMember(), userMember);
	}
	
	@Test
	public void testReleasePackageCreateUsesBodyMemberForAdmin() throws Exception {
		Member userMember = new Member("SE", 1);
		Member bodyMember = new Member("DK", 2);
		Mockito.stub(memberRepository.findOneByKey("DK")).toReturn(bodyMember);
		Mockito.stub(userMembershipAccessor.getMemberAssociatedWithUser()).toReturn(userMember);
		securityContextSetup.asAdmin();
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\", \"member\": { \"key\": \"DK\"} }")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(bodyMember, releasePacakgeCaptor.getValue().getMember());
	}
	
	@Test
	public void testReleasePackageCreateDefaultsMemberForAdminIfNotInBody() throws Exception {
		Member userMember = new Member("XX", 1);
		Mockito.stub(userMembershipAccessor.getMemberAssociatedWithUser()).toReturn(userMember);
		securityContextSetup.asAdmin();
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(releasePacakgeCaptor.getValue().getMember(), userMember);
	}
	
	@Test
	public void testReleasePackageLogsAuditEvent() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageAuditEvents).logCreationOf(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateFailsForUnknownId() throws Exception {
		Mockito.when(releasePackageRepository.findOne(999L)).thenReturn(null);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 999, \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
		
		Mockito.verify(releasePackageRepository, Mockito.never()).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldSave() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldOnlyCopySubsetOfFields() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		releasePackage.setName("originalName");
		releasePackage.setDescription("originalDescription");
		releasePackage.setCreatedBy("originalCreatedBy");
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\", \"createdBy\": \"newCreatedBy\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		ArgumentCaptor<ReleasePackage> savedReleasePackage = ArgumentCaptor.forClass(ReleasePackage.class);
		Mockito.verify(releasePackageRepository).save(savedReleasePackage.capture());
		
		Assert.assertEquals("newName", savedReleasePackage.getValue().getName());
		Assert.assertEquals("newDescription", savedReleasePackage.getValue().getDescription());
		
		Assert.assertEquals("originalCreatedBy", savedReleasePackage.getValue().getCreatedBy());
	}

	@Test
	public void testReleasePackageDeleteShouldFailForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion activeVersion = new ReleaseVersion(2L);
		activeVersion.setOnline(true);
		releasePackage.addReleaseVersion(activeVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
		
		Mockito.verify(releasePackageRepository, Mockito.never()).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteShouldSucceedForInactiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
		inactiveVersion.setOnline(false);
		releasePackage.addReleaseVersion(inactiveVersion);
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
		
		Mockito.verify(releasePackageRepository).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteLogsAuditEvent() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		
		Mockito.when(releasePackageRepository.findOne(1L)).thenReturn(releasePackage);
		
		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

		Mockito.verify(releasePackageAuditEvents).logDeletionOf(Mockito.any(ReleasePackage.class));
	}

}
