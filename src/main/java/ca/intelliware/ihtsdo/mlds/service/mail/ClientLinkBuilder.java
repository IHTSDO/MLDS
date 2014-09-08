package ca.intelliware.ihtsdo.mlds.service.mail;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class ClientLinkBuilder {
	@Resource TemplateEvaluator templateEvaluator;
	
	public String buildPasswordResetLink(String tokenKey) {
		return templateEvaluator.getUrlBase() + "#/resetPassword?token="+tokenKey;
	}

	public String buildLoginLink() {
		return templateEvaluator.getUrlBase() + "#/login";
	}
	
	public String buildViewReleasesLink() {
		return templateEvaluator.getUrlBase() + "#/viewReleases";
	}
	
}