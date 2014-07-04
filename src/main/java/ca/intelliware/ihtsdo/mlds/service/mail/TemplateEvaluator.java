package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

@Service
public class TemplateEvaluator {
    @Resource SpringTemplateEngine templateEngine;
    @Resource MessageSource messageSource;
    
    @Resource HttpServletRequest request;
	@Resource ServletContext servletContext;
	@Resource ApplicationContext applicationContext;
    
	public String getUrlBase() {
		return request.getScheme() + "://" + 
                request.getServerName() +
                ":" + request.getServerPort();
	}

    public String evaluateTemplate(String templateName, Locale locale, Map<String,?> variables) {
    	IWebContext context = new SpringWebContext(request, null, servletContext, locale, variables, applicationContext);
    	return templateEngine.process(templateName, context);

    }

	public String getTitleFor(String emailName, Locale locale) {
        final String title = messageSource.getMessage(emailName + ".title", null, locale);
		return title;
	}
	
}