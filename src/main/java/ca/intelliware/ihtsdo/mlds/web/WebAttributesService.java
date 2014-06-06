package ca.intelliware.ihtsdo.mlds.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.commons.event.model.user.WebAttributes;

@Service
public class WebAttributesService {
	@Resource
	private HttpServletRequest httpRequest;

	public WebAttributes getCurrentRequestWebAttributes() {
		return WebAttributes.create(httpRequest);
	}
}
