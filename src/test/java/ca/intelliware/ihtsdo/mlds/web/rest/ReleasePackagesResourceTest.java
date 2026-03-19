package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.*;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.SecurityContextSetup;
import ca.intelliware.ihtsdo.mlds.service.ReleasePackagePrioritizer;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

import java.util.List;
import java.util.Optional;

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

	@Mock
	ReleasePackagePrioritizer releasePackagePrioritizer;

    @Mock
    ReleaseVersionAccessRepository releaseVersionAccessRepository;

    @Mock
    private ReleasePackageConfigRepository releasePackageConfigRepository;

    @Mock
    private ObjectMapper objectMapper;


    @Captor
	ArgumentCaptor<ReleasePackage> releasePacakgeCaptor;

	ReleasePackagesResource releasePackagesResource;


	SecurityContextSetup securityContextSetup = new SecurityContextSetup();

    @Mock
    private UserRepository userRepository;



    @Mock
    private SessionService sessionService;

	@Before
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        releasePackagesResource = new ReleasePackagesResource(
            userRepository,
            releasePackageConfigRepository,
            sessionService
        );

        releasePackagesResource.releasePackageRepository = releasePackageRepository;
        releasePackagesResource.authorizationChecker = authorizationChecker;
        releasePackagesResource.currentSecurityContext = currentSecurityContext;
        releasePackagesResource.releasePackageAuditEvents = releasePackageAuditEvents;
        releasePackagesResource.userMembershipAccessor = userMembershipAccessor;
        releasePackagesResource.releasePackagePrioritizer = releasePackagePrioritizer;
        releasePackagesResource.releaseVersionAccessRepository = releaseVersionAccessRepository;


        when(userMembershipAccessor.getMemberAssociatedWithUser()).thenReturn(new Member("IHTSDO", 1));

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
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageCreateIgnoresBodyMemberAndAttachesPackageToUserMember() throws Exception {
        Member userMember = new Member("SE", 1);
		when(userMembershipAccessor.getMemberAssociatedWithUser()).thenReturn(userMember);

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\", \"member\": { \"key\": \"DK\" } }")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(releasePacakgeCaptor.getValue().getMember(), userMember);
	}

	@Test
	public void testReleasePackageCreateUsesBodyMemberForAdmin() throws Exception {
		Member userMember = new Member("SE", 1);
		Member bodyMember = new Member("DK", 2);
		when(memberRepository.findOneByKey("DK")).thenReturn(bodyMember);
//		Mockito.when(userMembershipAccessor.getMemberAssociatedWithUser()).thenReturn(userMember);
		securityContextSetup.asAdmin();

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\", \"member\": { \"key\": \"DK\"} }")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(bodyMember, releasePacakgeCaptor.getValue().getMember());
	}

	@Test
	public void testReleasePackageCreateDefaultsMemberForAdminIfNotInBody() throws Exception {
		Member userMember = new Member("XX", 1);
		when(userMembershipAccessor.getMemberAssociatedWithUser()).thenReturn(userMember);
		securityContextSetup.asAdmin();

		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\"}")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		verify(releasePackageRepository).save(releasePacakgeCaptor.capture());
		assertEquals(releasePacakgeCaptor.getValue().getMember(), userMember);
	}

	@Test
	public void testReleasePackageLogsAuditEvent() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageAuditEvents).logCreationOf(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageCreateShouldInitializePackagesPriorityToEndOfList() throws Exception {
		restReleasePackagesResource.perform(MockMvcRequestBuilders.post(Routes.RELEASE_PACKAGES)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));

		verify(releasePackagePrioritizer).prioritize(Mockito.any(ReleasePackage.class), Mockito.eq(ReleasePackagePrioritizer.END_PRIORITY));
	}


	@Test
	public void testReleasePackageUpdateFailsForUnknownId() throws Exception {
		when(releasePackageRepository.findById(999L)).thenReturn(Optional.empty());

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 999L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"releasePackageId\": 999, \"name\": \"name\", \"description\": \"description\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

		verify(releasePackageRepository, Mockito.never()).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldSave() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageRepository).save(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageUpdateShouldOnlyCopySubsetOfFields() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		releasePackage.setName("originalName");
		releasePackage.setDescription("originalDescription");
		releasePackage.setCreatedBy("originalCreatedBy");

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"releasePackageId\": 1, \"name\": \"newName\", \"description\": \"newDescription\", \"createdBy\": \"newCreatedBy\" }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		ArgumentCaptor<ReleasePackage> savedReleasePackage = ArgumentCaptor.forClass(ReleasePackage.class);
		verify(releasePackageRepository).save(savedReleasePackage.capture());

		Assert.assertEquals("newName", savedReleasePackage.getValue().getName());
		Assert.assertEquals("newDescription", savedReleasePackage.getValue().getDescription());

		Assert.assertEquals("originalCreatedBy", savedReleasePackage.getValue().getCreatedBy());
	}

	@Test
	public void testReleasePackageUpdateShouldReorderMemberPackagesWhenPriorityChanged() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		releasePackage.setName("originalName");
		releasePackage.setDescription("originalDescription");
		releasePackage.setCreatedBy("originalCreatedBy");
		releasePackage.setPriority(5);

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.put(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"releasePackageId\": 1, \"priority\": 9 }")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		ArgumentCaptor<ReleasePackage> savedReleasePackage = ArgumentCaptor.forClass(ReleasePackage.class);
		verify(releasePackageRepository).save(savedReleasePackage.capture());

		verify(releasePackagePrioritizer).prioritize(releasePackage, 9);
	}

	@Test
	public void testReleasePackageDeleteShouldFailForActiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion activeVersion = new ReleaseVersion(2L);
		activeVersion.setOnline(true);
		releasePackage.addReleaseVersion(activeVersion);

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict());

		verify(releasePackageRepository, Mockito.never()).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteShouldSucceedForInactiveVersion() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();
		ReleaseVersion inactiveVersion = new ReleaseVersion(2L);
		inactiveVersion.setOnline(false);
		releasePackage.addReleaseVersion(inactiveVersion);

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageRepository).delete(Mockito.any(ReleasePackage.class));
	}

	@Test
	public void testReleasePackageDeleteLogsAuditEvent() throws Exception {
		ReleasePackage releasePackage = new ReleasePackage();

		when(releasePackageRepository.findById(1L)).thenReturn(Optional.of(releasePackage));

		restReleasePackagesResource.perform(MockMvcRequestBuilders.delete(Routes.RELEASE_PACKAGE, 1L)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

		verify(releasePackageAuditEvents).logDeletionOf(Mockito.any(ReleasePackage.class));
	}

    @Test
    public void shouldFilterVersionsBasedOnAccess() throws Exception {

        ReleasePackage rp = new ReleasePackage();

        ReleaseVersion v1 = new ReleaseVersion(1L);
        v1.setArchive(false);

        rp.addReleaseVersion(v1);

        when(releasePackageRepository.findAll()).thenReturn(List.of(rp));
        when(authorizationChecker.buildAccessContext(any())).thenReturn(mock(ReleasePackageAuthorizationChecker.AccessContext.class));
        when(authorizationChecker.canAccessReleaseVersion(eq(v1), any())).thenReturn(true);

        restReleasePackagesResource.perform(MockMvcRequestBuilders.get(Routes.RELEASE_PACKAGES))
            .andExpect(status().isOk());
    }

}
