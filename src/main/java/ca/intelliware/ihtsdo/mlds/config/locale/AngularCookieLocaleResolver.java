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

    // @Override
    public void addCookie(HttpServletResponse response, String cookieValue) {
        // Mandatory cookie modification for angular to support the locale switching on the server side.
        cookieValue = "%22" + cookieValue + "%22";

        /* MLDS-992 Missing Cookie */
        this.setCookieSecure(true);
        this.setCookieHttpOnly(true);
        /* MLDS-992 Missing Cookie */

        // super.addCookie(response, cookieValue);
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) != null) {
            return;
        }

        Cookie cookie = WebUtils.getCookie(request, "NG_TRANSLATE_LANG_KEY");
        Locale locale = null;
        TimeZone timeZone = null;

        if (cookie != null) {
            String cookieValue = removeQuotes(cookie.getValue());
            String[] localeAndTimeZone = splitLocaleAndTimeZone(cookieValue);

            locale = parseLocale(localeAndTimeZone[0]);
            timeZone = parseTimeZone(localeAndTimeZone[1]);

            logParsedCookie(cookie, locale, timeZone);
        }

        setLocaleAttributes(request, locale, timeZone);
    }

    private String removeQuotes(String value) {
        return StringUtils.replace(value, "%22", "");
    }

    private String[] splitLocaleAndTimeZone(String value) {
        int spaceIndex = value.indexOf(' ');

        if (spaceIndex == -1) {
            return new String[]{value, null};
        }

        return new String[]{
            value.substring(0, spaceIndex),
            value.substring(spaceIndex + 1)
        };
    }

    private Locale parseLocale(String localePart) {
        if ("-".equals(localePart)) {
            return null;
        }

        // Spring expects locale variations to use underscore rather than dash.
        localePart = localePart.replace("-", "_");
        return StringUtils.parseLocaleString(localePart);
    }

    private TimeZone parseTimeZone(String timeZonePart) {
        if (timeZonePart == null) {
            return null;
        }

        return StringUtils.parseTimeZoneString(timeZonePart);
    }

    private void logParsedCookie(
        Cookie cookie,
        Locale locale,
        TimeZone timeZone) {

        if (!logger.isTraceEnabled()) {
            return;
        }

        String timeZoneMessage =
            timeZone != null
                ? " and time zone '" + timeZone.getID() + "'"
                : "";

        logger.trace(
            "Parsed cookie value [" + cookie.getValue()
                + "] into locale '" + locale
                + "'" + timeZoneMessage);
    }

    private void setLocaleAttributes(
        HttpServletRequest request,
        Locale locale,
        TimeZone timeZone) {

        request.setAttribute(
            LOCALE_REQUEST_ATTRIBUTE_NAME,
            locale != null ? locale : getDefaultLocale());

        request.setAttribute(
            TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
            timeZone != null ? timeZone : getDefaultTimeZone());
    }
}
