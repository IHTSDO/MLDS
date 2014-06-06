package ca.intelliware.commons.j5goodies.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class HttpUtil {

    public static final int DEFAULT_SSL_PORT = 443;

    public static final Set<String> TOP_LEVEL_DOMAIN_QUALIFIERS;

    static {

        Set<String> set = new HashSet<String>();
        set.add(".aero");
        set.add(".biz");
        set.add(".cat");
        set.add(".com");
        set.add(".coop");
        set.add(".edu");
        set.add(".gov");
        set.add(".info");
        set.add(".int");
        set.add(".jobs");
        set.add(".mil");
        set.add(".com");
        set.add(".mobi");
        set.add(".museum");
        set.add(".name");
        set.add(".net");
        set.add(".org");
        set.add(".pro");
        set.add(".travel");
        TOP_LEVEL_DOMAIN_QUALIFIERS = Collections.unmodifiableSet(set);
    }

    public static String convertToHttps(String url) throws URISyntaxException {
        return convertToHttps(url, DEFAULT_SSL_PORT);
    }

    public static String convertToHttps(String url, int port) throws URISyntaxException {
        if (url == null) {
            return null;
        } else {
            URI uri = new URI(url);
            if (isHttpURI(uri)) {
                return "https://" + uri.getHost() + ":" + port + uri.getPath();
            } else {
                return url;
            }
        }
    }

    /**
     * <p>Determines if the content type of a request is "multipart/form-data".
     *
     * @param request - the HTTP request object
     * @return <code>true</code> if the content type is "multipart/form-data";
     *         <code>false</code> otherwise.
     */
    public static boolean isMultipartFormData(HttpServletRequest request) {
        String type = request.getHeader("Content-Type");
        return (type != null && type.startsWith("multipart/form-data"));
    }

    /**
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static boolean isHttpURI(String uri) throws URISyntaxException {
        return isHttpURI(new URI(uri));
    }
    /**
     * @param uri
     * @return
     */
    private static boolean isHttpURI(URI uri) {
        return "http".equals(uri.getScheme());
    }

    /**
     * Normalize a request URI path.  Normalization involves:
     *
     * <ul>
     * <li>Removing all "../" and "./" path elements
     * <li>Removing double slashes.
     * </ul>
     *
     * @see java.net.URI.normalize()
     * @param uriString
     * @return
     */
    public static String normalize(String uriString) {
        if (uriString != null) {
            try {
                URI uri = URI.create(uriString.replace(' ','+'));
                uriString = uri.normalize().toASCIIString().replace('+', ' ');
            } catch (IllegalArgumentException e) {
                uriString = null;
            }
        }

        return uriString;
    }
    public static String decode(String uriString) {
        try {
            return URLDecoder.decode(uriString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return uriString;
        }
    }
    public static String getReferer(HttpServletRequest httpRequest) {
        return httpRequest.getHeader("referer");
    }
    public static String getUserAgent(HttpServletRequest httpRequest) {
        return httpRequest.getHeader("user-agent");
    }
    public static String getRequestURLString(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURL().toString();
    }
    public static void setNoCache(HttpServletResponse httpResponse) {
        httpResponse.addHeader("Pragma", "No-cache");
        httpResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, " +
        		"max-age=0, proxy-revalidate, no-transform, pre-check=0, post-check=0, private");
        httpResponse.addDateHeader("Expires", 1);
    }
    public static String getRequestURIWithoutContext(HttpServletRequest httpRequest) {
        String uri = httpRequest.getRequestURI();
        uri = normalizeSlashes(uri);

        if (!StringUtils.isBlank(httpRequest.getContextPath())) {
            uri = uri.substring(httpRequest.getContextPath().length());
        }
        return uri;
    }

    private static String normalizeSlashes(String uri) {
    	if (StringUtils.isNotBlank(uri)) {
	    	String[] parts = StringUtils.split(uri, "/");
			return "/" + StringUtils.join(parts, "/");
    	} else {
    		return uri;
    	}
	}

	public static boolean isPrivateNetwork(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
		return ipAddress.startsWith("192.168.") ||
        	ipAddress.startsWith("172.16.") ||
        	ipAddress.startsWith("172.17.") ||
        	ipAddress.startsWith("172.18.") ||
        	ipAddress.startsWith("172.19.") ||
        	ipAddress.startsWith("172.20.") ||
        	ipAddress.startsWith("172.21.") ||
        	ipAddress.startsWith("172.22.") ||
        	ipAddress.startsWith("172.23.") ||
        	ipAddress.startsWith("172.24.") ||
        	ipAddress.startsWith("172.25.") ||
        	ipAddress.startsWith("172.26.") ||
        	ipAddress.startsWith("172.27.") ||
        	ipAddress.startsWith("172.28.") ||
        	ipAddress.startsWith("172.29.") ||
        	ipAddress.startsWith("172.30.") ||
        	ipAddress.startsWith("172.31.") ||
        	ipAddress.startsWith("10.") ||
        	ipAddress.startsWith("169.254.");
    }

    public static String getHighLevelDomainName(String url) {
        try {
            URI uri = new URI(url);
            String hostName = uri.getHost();
            if ("localhost".equalsIgnoreCase(hostName)) {
                return hostName;
            } else {
                int index = hostName.lastIndexOf('.');
                if (index > 0) {
                    index = hostName.lastIndexOf('.', index-1);
                    return index < 0 ? hostName : hostName.substring(index+1);
                } else {
                    return null;
                }
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }
    public static String getHostName(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }
    public static String getUrlWithoutPath(String url) {
        try {
            URI uri = new URI(url);
            return uri.resolve("/").toASCIIString();
        } catch (URISyntaxException e) {
            return null;
        }
    }
    public static String getUrlWithoutPath(HttpServletRequest request) {
        return getUrlWithoutPath(request.getRequestURL().toString());
    }
    public static String getFullyQualifiedContext(HttpServletRequest request) {
        String url = getUrlWithoutPath(request);
        if (url == null) {
            return null;
        } else {
            url += request.getContextPath();
            return url.endsWith("/") ? url : url + "/";
        }
    }
    
    public static String qualifyUrlWithContextPath(String unqualifiedUrl, String contextPath) {
    	return contextPath + unqualifiedUrl;
    }

	public static String qualifyUrlWithContextPath(String unqualifiedUrl, HttpServletRequest httpRequest) {
		return qualifyUrlWithContextPath(unqualifiedUrl, httpRequest.getContextPath());
	}
}
