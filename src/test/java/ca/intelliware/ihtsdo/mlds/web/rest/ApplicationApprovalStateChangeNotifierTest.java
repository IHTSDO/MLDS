package ca.intelliware.ihtsdo.mlds.web.rest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.PrimaryApplication;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationPendingEmailSender;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationApprovalStateChangeNotifierTest {

	@Mock ApplicationPendingEmailSender applicationPendingEmailSender;
	private ApplicationApprovalStateChangeNotifier applicationApprovalStateChangeNotifier;
	
	@Before
	public void setup() {
		applicationApprovalStateChangeNotifier = new ApplicationApprovalStateChangeNotifier();
		
		applicationApprovalStateChangeNotifier.applicationPendingEmailSender = applicationPendingEmailSender;
	}
	
	@Test
	public void sendApplicationPendingEmailForSubmittedTransitionShouldSendPendingEmailToStaff() throws Exception {
		Member member = new Member("SE", 1L);
		member.setStaffNotificationEmail("staff@test.com");
		Application application = withApplication(ApprovalState.SUBMITTED, member);
		applicationApprovalStateChangeNotifier.applicationApprovalStateChange(ApprovalState.NOT_SUBMITTED, application);
		
		Mockito.verify(applicationPendingEmailSender, times(1)).sendApplicationPendingEmail("staff@test.com", application);
	}

	@Test
	public void sendApplicationPendingEmailShouldBeIgnoreWhenNoMailingAddress() throws Exception {
		Member member = new Member("SE", 1L);
		member.setStaffNotificationEmail(null);
		Application application = withApplication(ApprovalState.SUBMITTED, member);
		applicationApprovalStateChangeNotifier.applicationApprovalStateChange(ApprovalState.NOT_SUBMITTED, application);
		
		Mockito.verify(applicationPendingEmailSender, never()).sendApplicationPendingEmail("staff@test.com", application);
	}

	@Test
	public void sendApplicationPendingEmailShouldBeIgnoreWhenNoTransition() throws Exception {
		Member member = new Member("SE", 1L);
		member.setStaffNotificationEmail("staff@test.com");
		Application application = withApplication(ApprovalState.SUBMITTED, member);
		applicationApprovalStateChangeNotifier.applicationApprovalStateChange(ApprovalState.SUBMITTED, application);
		
		Mockito.verify(applicationPendingEmailSender, never()).sendApplicationPendingEmail("staff@test.com", application);
	}
	
	@Test
	public void sendApplicationPendingEmailShouldIgnoredForNonSubmittedApprovalState() throws Exception {
		Member member = new Member("SE", 1L);
		member.setStaffNotificationEmail("staff@test.com");
		Application application = withApplication(ApprovalState.APPROVED, member);
		applicationApprovalStateChangeNotifier.applicationApprovalStateChange(ApprovalState.NOT_SUBMITTED, application);
		
		Mockito.verify(applicationPendingEmailSender, never()).sendApplicationPendingEmail("staff@test.com", application);
	}


	private Application withApplication(ApprovalState approvalState, Member member) {
		PrimaryApplication application = new PrimaryApplication(1L);
		application.setApprovalState(approvalState);
		application.setMember(member);
		application.setAffiliate(new Affiliate(2L));
		return application;
	}
}
