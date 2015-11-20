package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationPendingEmailSender;

@Service
public class ApplicationApprovalStateChangeNotifier {

	@Resource
	ApplicationPendingEmailSender applicationPendingEmailSender;
	
	public void applicationApprovalStateChange(ApprovalState before, Application application) {
		ApprovalState after = application.getApprovalState();
		if (Objects.equal(after, ApprovalState.SUBMITTED) && !Objects.equal(before, after)) { 
			pendingNotification(application);
		} 
	}

	private void pendingNotification(Application application) {
		Affiliate affiliate = application.getAffiliate();
		Member member = application.getMember();
		if (affiliate != null && member != null && StringUtils.isNotBlank(member.getStaffNotificationEmail())) {
			applicationPendingEmailSender.sendApplicationPendingEmail(member.getStaffNotificationEmail(), application);
		}
	}
}
