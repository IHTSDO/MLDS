package ca.intelliware.ihtsdo.mlds.web;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

@Controller
public class FirstPageController   {
	
	Date serverStartDate = new Date();
	
	@Resource
	TemplateCollector templateCollector;

	@RequestMapping("/")
	public ModelAndView getFirstPage()  {
		ModelAndView modelAndView = createApplicationShell("MLDS-Registration");
		return modelAndView;
	}
	private ModelAndView createApplicationShell(String applicationName) {
		ModelAndView modelAndView = new ModelAndView(new InternalResourceView("shell.jsp"));
		modelAndView.addObject("applicationName", applicationName);
		modelAndView.addObject("templateCollector", templateCollector);
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
