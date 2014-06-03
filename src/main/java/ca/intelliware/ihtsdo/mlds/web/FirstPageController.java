package ca.intelliware.ihtsdo.mlds.web;

import java.security.Principal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import ca.intelliware.ihtsdo.mlds.registration.UserRegistration;
import ca.intelliware.ihtsdo.mlds.registration.UserRegistrationRepository;

@Controller
public class FirstPageController   {
	
	Date serverStartDate = new Date();
	
	@Resource
	TemplateCollector templateCollector;

	@Resource
	UserRegistrationRepository userRegistrationRepository;

	protected UserRegistration getCurrentRegistration() {
		Authentication authentication = SecurityContextHolder
				.getContext()
				.getAuthentication();
		Object principal = authentication
				.getPrincipal();
		String email = null;
		if (principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			email = userDetails.getUsername();
		}
		if (principal instanceof String) {
			email = (String) principal;
		}
		return email == null ? null : userRegistrationRepository.findByEmail(email);
	}

	@RequestMapping("/")
	public ModelAndView getFirstPage()  {
		ModelAndView modelAndView = createApplicationShell("MLDS-Registration");
		return modelAndView;
	}
	private ModelAndView createApplicationShell(String applicationName) {
		ModelAndView modelAndView = new ModelAndView(new InternalResourceView("shell.jsp"));
		modelAndView.addObject("applicationName", applicationName);
		modelAndView.addObject("templateCollector", templateCollector);
		modelAndView.addObject("userRegistration", getCurrentRegistration());
		return modelAndView;
	}
	@RequestMapping("/dashboard")
	public ModelAndView getUserDashboard()  {
		ModelAndView modelAndView = createApplicationShell("MLDS-User");
		return modelAndView;
	}
	@RequestMapping("/admin")
	public ModelAndView getAdminHome()  {
		ModelAndView modelAndView = createApplicationShell("MLDS-Admin");
		return modelAndView;
	}
}
