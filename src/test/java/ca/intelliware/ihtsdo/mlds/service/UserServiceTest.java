package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AffiliateRepository affiliateRepository;

    @Mock
    private CommercialUsageRepository commercialUsageRepository;

    @InjectMocks
    private UserService userService;

    Member swedenMember;

    @Before
    public void setup() {
        swedenMember = new Member("SE", 1);
        swedenMember.setPendingApplication(10);
        swedenMember.setInvoicesPending(0);
        swedenMember.setUsageReports(5);
    }


    @Test
    public void testRemovePendingApplication() {
        Affiliate affiliate1 = withAffiliate(StandingState.APPLYING, AffiliateType.ACADEMIC, 10L);
        Affiliate affiliate2 = withAffiliate(StandingState.APPLYING, AffiliateType.INDIVIDUAL, 11L);

        PrimaryApplication app1 = withExistingSwedishPrimaryApplication(1L, affiliate1);
        PrimaryApplication app2 = withExistingSwedishPrimaryApplication(2L, affiliate2);

        when(applicationRepository.getAllApplication()).thenReturn(Arrays.asList(app1, app2));
        when(memberRepository.findMemberById(1L)).thenReturn(swedenMember);

        userService.removePendingApplication();

        List<Long> resultAffiliate = Arrays.asList(10L,11L);
        verify(applicationRepository, atLeastOnce()).getAllApplication();
        verify(applicationRepository, atLeastOnce()).updateLastProcessed(eq(resultAffiliate), any(Instant.class));
    }


    @Test
    public void testRemoveInvoicesPending_NoPendingInvoices() {

        when(memberRepository.findMemberById(1L)).thenReturn(swedenMember);
        userService.removeInvoicesPending();

        verify(affiliateRepository, never()).getIHTSDOPendingInvoices();
        verify(affiliateRepository, never()).updateLastProcessed(anyList(), any(Instant.class));
        verify(affiliateRepository, never()).updateAffiliateStandingStateAndDeactivationReason(anyLong(), any(),any());
    }

    @Test
    public void testRemoveInvoicesPending_WithPendingInvoices() {

        swedenMember.setInvoicesPending(5);
        when(memberRepository.findMemberById(1L)).thenReturn(swedenMember);


        Affiliate affiliate1 = new Affiliate(10L);
        affiliate1.setStandingState(StandingState.PENDING_INVOICE);
        affiliate1.setCreated(Instant.now().minus(6, ChronoUnit.DAYS));

        Affiliate affiliate2 = new Affiliate(11L);
        affiliate2.setStandingState(StandingState.PENDING_INVOICE);
        affiliate2.setCreated(Instant.now().minus(7, ChronoUnit.DAYS));

        when(affiliateRepository.getIHTSDOPendingInvoices()).thenReturn(Arrays.asList(affiliate1, affiliate2));


        when(affiliateRepository.findActiveAffiliateIds(Arrays.asList(10L, 11L))).thenReturn(Arrays.asList(10L, 11L));


        Instant now = Instant.now();

        userService.removeInvoicesPending();

        verify(affiliateRepository).getIHTSDOPendingInvoices();
        verify(affiliateRepository).updateLastProcessed(eq(Arrays.asList(10L, 11L)), argThat(argument -> {
            return argument.isAfter(now.minusSeconds(1)) && argument.isBefore(now.plusSeconds(1));
        }));
        verify(affiliateRepository).updateAffiliateStandingStateAndDeactivationReason(10L,StandingState.DEREGISTERED, ReasonForDeactivation.AUTODEACTIVATION);
        verify(affiliateRepository).updateAffiliateStandingStateAndDeactivationReason(11L,StandingState.DEREGISTERED, ReasonForDeactivation.AUTODEACTIVATION);
    }


    @Test
    public void testRemoveUsageReports_WithValidData() throws Exception {

        Affiliate affiliate = new Affiliate(1L);
        affiliate.setHomeMember(swedenMember);

        CommercialUsage usage = new CommercialUsage();

        usage.setState(UsageReportState.NOT_SUBMITTED);
        setField(usage, "commercialUsageId", 1L);
        setField(usage, "affiliate", affiliate);
        setField(usage, "created", Instant.now().minus(10, ChronoUnit.DAYS));

        when(commercialUsageRepository.findByState()).thenReturn(List.of(usage));
        when(affiliateRepository.findById(1L)).thenReturn(java.util.Optional.of(affiliate));
        when(affiliateRepository.findActiveAffiliateIds(any())).thenReturn(List.of(1L));
        when(affiliateRepository.updateAffiliateStandingStateAndDeactivationReason(eq(1L),any(), any())).thenReturn(1);

        when(memberRepository.findMemberById(1L)).thenReturn(swedenMember);

        userService.removeUsageReports();

        verify(commercialUsageRepository, times(1)).findByState();
        verify(affiliateRepository, times(1)).findById(1L);
        verify(affiliateRepository, times(1)).updateAffiliateStandingStateAndDeactivationReason(1L,StandingState.DEREGISTERED, ReasonForDeactivation.AUTODEACTIVATION);
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Affiliate withAffiliate(StandingState existingStandingState, AffiliateType affiliateType, Long affiliateId) {
        Affiliate affiliate = new Affiliate(affiliateId);
        affiliate.setStandingState(existingStandingState);
        affiliate.setAffiliateDetails(new AffiliateDetails());
        affiliate.setType(affiliateType);
        Mockito.when(affiliateRepository.findByCreatorIgnoreCase(Mockito.anyString())).thenReturn(List.of(affiliate));
        return affiliate;
    }

    private PrimaryApplication withExistingSwedishPrimaryApplication(long primaryApplicationId, Affiliate affiliate) {
        PrimaryApplication primaryApplication = new PrimaryApplication(primaryApplicationId);
        primaryApplication.setAffiliateDetails(new AffiliateDetails());
        primaryApplication.setMember(swedenMember);
        primaryApplication.setAffiliate(affiliate);
        primaryApplication.setApprovalState(ApprovalState.NOT_SUBMITTED);
        primaryApplication.setSubmittedAt(Instant.now().minus(30, ChronoUnit.DAYS));
        CommercialUsage commercialUsage = new CommercialUsage(10L, affiliate);
        commercialUsage.setType(affiliate.getType());
        primaryApplication.setCommercialUsage(commercialUsage);
        return primaryApplication;
    }
}
