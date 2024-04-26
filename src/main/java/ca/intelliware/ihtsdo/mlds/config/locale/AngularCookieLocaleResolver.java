package ca.intelliware.ihtsdo.mlds.config.locale;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Angular cookie saved the locale with a double quote (%22en%22).
 * So the default CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 *
 * This class will check if a double quote has been added, if so it will remove it.
 */
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() {
            @Override
            public Locale getLocale() {
                return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }
            @Override
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

//    @Override
    public void addCookie(HttpServletResponse response, String cookieValue) {
        // Mandatory cookie modification for angular to support the locale switching on the server side.
        cookieValue = "%22" + cookieValue + "%22";
        /*MLDS-992 Missing Cookie*/
        this.setCookieSecure(true);
        this.setCookieHttpOnly(true);
        /*MLDS-992 Missing Cookie*/
//        super.addCookie(response, cookieValue);
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            // Retrieve and parse cookie value.
            Cookie cookie = WebUtils.getCookie(request, "NG_TRANSLATE_LANG_KEY");
            Locale locale = null;
            TimeZone timeZone = null;
            if (cookie != null) {
                String value = cookie.getValue();

                // Remove the double quote
                value = StringUtils.replace(value, "%22", "");

                String localePart = value;
                String timeZonePart = null;
                int spaceIndex = localePart.indexOf(' ');
                if (spaceIndex != -1) {
                    localePart = value.substring(0, spaceIndex);
                    timeZonePart = value.substring(spaceIndex + 1);
                }

                locale = null;
                if (!"-".equals(localePart)) {
                	//Spring expects locale variations to use underscore rather than dash
                	localePart = localePart.replaceAll("-","_");
                	StringUtils.parseLocaleString(localePart);
                };
                if (timeZonePart != null) {
                    timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale +
                            "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
                    (locale != null ? locale: determineDefaultLocale(request)));

            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
                    (timeZone != null ? timeZone : determineDefaultTimeZone(request)));
        }
    }
}
