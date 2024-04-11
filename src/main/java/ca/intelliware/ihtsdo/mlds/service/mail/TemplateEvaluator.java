package ca.intelliware.ihtsdo.mlds.service.mail;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;


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
        Context context = new Context(locale, (Map<String, Object>) variables);
        return templateEngine.process(templateName, context);

    }

	public String getTitleFor(String emailName, Locale locale) {
        final String title = messageSource.getMessage(emailName + ".title", null, locale);
		return title;
	}

}
