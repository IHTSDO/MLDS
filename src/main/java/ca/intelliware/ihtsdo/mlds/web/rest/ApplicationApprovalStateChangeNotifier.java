package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationPendingEmailSender;

@Service
public class ApplicationApprovalStateChangeNotifier {

	@Resource
	ApplicationPendingEmailSender applicationPendingEmailSender;
	
	public void applicationApprovalStateChange(Application application) {
		if (Objects.equal(application.getApprovalState(), ApprovalState.SUBMITTED)) {
			pendingNotification(application);
		} 
	}

	private void pendingNotification(Application application) {
		//FIXME will getAffiliate always be not-null?
		Long affiliateId = application.getAffiliate().getAffiliateId();
		Member member = application.getMember();
		if (member != null && StringUtils.isNotBlank(member.getStaffNotificationEmail())) {
			applicationPendingEmailSender.sendApplicationPendingEmail(member.getStaffNotificationEmail(), application.getMember().getKey(), affiliateId, application.getApplicationId());
		}
	}
}
