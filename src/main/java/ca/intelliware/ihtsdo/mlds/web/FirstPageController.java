package ca.intelliware.ihtsdo.mlds.web;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

@Controller
public class FirstPageController   {
	
	Date serverStartDate = new Date();

	@RequestMapping("/")
	public ModelAndView getFirstPage()  {
		return new ModelAndView(new InternalResourceView("shell.jsp"), "applicationName", "MLDS-Registration");
	}
	@RequestMapping("/dashboard")
	public ModelAndView getUserDashboard()  {
		return new ModelAndView(new InternalResourceView("shell.jsp"), "applicationName", "MLDS-User");
	}
	@RequestMapping("/admin")
	public ModelAndView getAdminHome()  {
		return new ModelAndView(new InternalResourceView("shell.jsp"), "applicationName", "MLDS-Admin");
	}
}
