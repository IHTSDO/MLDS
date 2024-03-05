package ca.intelliware.ihtsdo.mlds.service.mail;


import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ClientLinkBuilder {
	@Resource
	TemplateEvaluator templateEvaluator;

	public String buildPasswordResetLink(String tokenKey) {
		return templateEvaluator.getUrlBase() + "#/resetPassword?token="+tokenKey;
	}

	public String buildLoginLink() {
		return templateEvaluator.getUrlBase() + "#/login";
	}

	public String buildViewReleasesLink() {
		return templateEvaluator.getUrlBase() + "#/viewReleases";
	}

	public String buildViewApplication(long applicationId) {
		return templateEvaluator.getUrlBase() + "#/applicationReview/"+applicationId;
	}

	public String buildViewReleasePackageLink(long releasePackageId) {
		return templateEvaluator.getUrlBase() + "#/viewReleases/viewRelease/"+releasePackageId;
	}
	
}