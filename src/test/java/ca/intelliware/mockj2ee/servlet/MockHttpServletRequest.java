package ca.intelliware.mockj2ee.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Purpose: Provide a Test implementation of an HttpRequest.
 *
 * @author Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockHttpServletRequest implements HttpServletRequest {

	private Hashtable parameters = new Hashtable();
	private Map attributes = new HashMap();
	private Hashtable headers = new Hashtable();
	private Vector cookies = new Vector();
	private ServletInputStream inputStream;

	private ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
	private ServletOutputStream outputStream = new MockServletOutputStream(
			byteArrayOut);

	private String method = "GET";
//	private String contentType = "text/plain";

	private String requestURI = ""; //capture the request URI
	private String contextPath = ""; // capture the context path
	private String servletPath = ""; // capture the servlet path
	private Locale locale = Locale.US; // capture the locale
	private String scheme = "http";
	private int serverPort = 80;
	private String serverName = "";
	private String remoteAddr = "127.0.0.1";
	private String remoteHost = "Test Remote Host...";

	private Hashtable roles = new Hashtable(); // the user's roles

	protected static Hashtable sessions; //A Hashtable of sessions

	public static final String SESSION_HEADER_KEY = "sesessionid";
	public static final String DEFAULT_SESSION_ID = "DefaultSession";
	public static final String DEFAULT_USER_ID = "TestUser";

	private MockServletContext servletContext;

	public MockHttpServletRequest() {
	}
	public MockHttpServletRequest(String requestURL) throws MalformedURLException {
		this(new URL(requestURL));
	}
	public MockHttpServletRequest(URL requestURL, String contextPath) {
		this.scheme = requestURL.getProtocol();
		this.requestURI = requestURL.getPath();
		this.serverPort = requestURL.getPort();
		this.serverName = requestURL.getHost();
		this.contextPath = contextPath;

		String queryString = requestURL.getQuery();
		if (queryString == null) {
			queryString = "";
		}
		for (StringTokenizer tokenizer = new StringTokenizer(queryString, "&"); tokenizer.hasMoreTokens(); ) {
			String token = tokenizer.nextToken();
			if (token.indexOf('=') >= 0) {
				String key = token.substring(0, token.indexOf('='));
				String value = token.substring(token.indexOf('=')+1);
			    setParameter(key, URLDecoder.decode(value));
			}
		}

        this.headers.put("Content-Type", "text/plain");
	}
	public MockHttpServletRequest(URL requestURL) {
		this(requestURL, null);
		if (this.requestURI.startsWith("/") && this.requestURI.indexOf('/', 1) >= 0) {
			this.contextPath = this.requestURI.substring(0, this.requestURI.indexOf('/', 1));
		}
	}
	/**
	 * add a cookie to the collection of cookies.
	 */
	public void addCookie(Cookie cookie) {
		cookies.addElement(cookie);
	}
	/**
	 * add a role to the collection of roles.
	 */
	public void addRole(String role) {
		roles.put(role, role);
	}
	/**
	 * getAttribute method comment.
	 */
	public Object getAttribute(String key) {
		if ("javax.servlet.include.path_info".equals(key)) {
			return getPathInfo();
		} else {
			return this.attributes.get(key);
		}
	}
	/**
	 * getAttributeNames method comment.
	 */
	public Enumeration getAttributeNames() {
		Vector vector = new Vector(this.attributes.keySet());
		return vector.elements();
	}
	/**
	 * getAuthType method comment.
	 */
	public java.lang.String getAuthType() {
		return null;
	}
	/**
	 * getCharacterEncoding method comment.
	 */
	public java.lang.String getCharacterEncoding() {
		return "ISO-8859-1";
	}
	/**
	 * getContentLength method comment.
	 */
	public int getContentLength() {
		return getOutputStreamContents().length;
	}
	/**
	 * getContentType method comment.
	 */
	public String getContentType() {
		return getHeader("Content-Type");
	}
	public String getContextPath() {
		return this.contextPath == null ? "" : this.contextPath;
	}
	/**
	 * getCookies method comment.
	 */
	public Cookie[] getCookies() {
		Cookie[] tmp = new Cookie[cookies.size()];
		cookies.toArray(tmp);
		return tmp;
	}
	public long getDateHeader(String headerName) {
		String header = getHeader(headerName);
		return StringUtils.isBlank(header) ? -1L : NumberUtils.toLong(header);
	}
	/**
	 *
	 */
	public static HttpSession getDefaultSession() {

		return getSessionForId(DEFAULT_SESSION_ID);

	}
	/**
     * <p>Returns the value of the specified request header as a String.
     * If the request did not include a header of the specified name, this method returns
     * <code>null</code>.
     * If there are multiple headers with the same name, this method returns the
     * first head in the request. The header name is case insensitive.  You can use this
     * method with any request header.
     *
     * @param name - a <code>String</code> specifying the header name
	 */
	public java.lang.String getHeader(String key) {
		return (String) this.headers.get(key.toLowerCase());
	}
	/**
	 * getHeaderNames method comment.
	 */
	public java.util.Enumeration getHeaderNames() {
		return headers.keys();
	}
	public Enumeration getHeaders(String name) {
		return null;
	}
	public javax.servlet.ServletInputStream getInputStream()
			throws java.io.IOException {
		if (inputStream == null) {
			byte[] data = getOutputStreamContents();
			ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(data);
			inputStream = new MockServletInputStream(byteArrayIn);
		}
		return inputStream;
	}
	/**
	 * getIntHeader method comment.
	 */
	public int getIntHeader(java.lang.String arg1) {
		return 0;
	}
	public Locale getLocale() {
		return this.locale;
	}
	public Enumeration getLocales() {
		Vector table = new Vector();
		table.add(this.locale);
		return table.elements();
	}
	/**
	 * get the HTTP request method.
	 */
	public String getMethod() {
		return this.method;
	}
	public ServletOutputStream getOutputStream() {
		return outputStream;
	}
	public byte[] getOutputStreamContents() {
		try {
			outputStream.close();
		} catch (IOException e) {
			// ignore
		}
		return byteArrayOut.toByteArray();
	}
	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public String getParameter(String key) {


		List parameterValue = (List) this.parameters.get(key);
		if (parameterValue == null || parameterValue.size() == 0) {
		    return null;
		}
		return (String) parameterValue.get(0);
	}
	/**
	 * getParameterMap method comment.
	 */
	public java.util.Map getParameterMap() {
		Map map = new HashMap();
		for (Iterator i = this.parameters.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			map.put(key, ((List)this.parameters.get(key)).toArray(new String[]{}));
		}

		return Collections.unmodifiableMap(map);
	}
	/**
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration getParameterNames() {

		return this.parameters.keys();
	}
	/**
	 * getParameterValues method comment.
	 */
	public String[] getParameterValues(String key) {

		if (parameters.get(key) != null) {
			return (String[]) ((List)parameters.get(key)).toArray(new String[]{});
		} else {
			return null;
		}
	}
	/**
	 * getPathInfo method comment.
	 */
	public java.lang.String getPathInfo() {
		return StringUtils.substringAfter(this.requestURI, this.servletPath);
	}
	/**
	 * getPathTranslated method comment.
	 */
	public java.lang.String getPathTranslated() {
		return null;
	}
	/**
	 * getProtocol method comment.
	 */
	public java.lang.String getProtocol() {
		return null;
	}
	/**
	 * getQueryString method comment.
	 */
	public java.lang.String getQueryString() {
		return null;
	}
	/**
	 * getReader method comment.
	 */
	public java.io.BufferedReader getReader() throws java.io.IOException {
		return null;
	}
	/**
	 * getRealPath method comment.
	 */
	public java.lang.String getRealPath(java.lang.String arg1) {
		return null;
	}
	/**
	 * getRemoteUser method comment.
	 */
	public java.lang.String getRemoteUser() {
		return null;
	}
	public RequestDispatcher getRequestDispatcher(java.lang.String path) {
		return this.servletContext == null ? null : this.servletContext.getRequestDispatcher(path);
	}
	/**
	 * getRequestedSessionId method comment.
	 */
	public java.lang.String getRequestedSessionId() {
		return null;
	}
	/**
	 * getRequestURI method comment.
	 */
	public java.lang.String getRequestURI() {
		return requestURI;
	}
	/**
	 * getRequestURL method comment.
	 */
	public java.lang.StringBuffer getRequestURL() {
		StringBuffer buffer = new StringBuffer(this.scheme);
        buffer.append("://");
		if (this.serverName != null) {
			buffer.append(String.valueOf(this.serverName));
		}
		if (this.serverPort > 0) {
		    buffer.append(":");
		    buffer.append(String.valueOf(this.serverPort));
		}
		if (this.requestURI != null) {
			buffer.append(this.requestURI);
		}
		return buffer;
	}
	/**
	  * getScheme method comment.
	  */
	public String getScheme() {
		return scheme;
	}
	/**
	 * getServerName method comment.
	 */
	public java.lang.String getServerName() {
		return this.serverName;
	}

	/**
	 * getServerPort method comment.
	 */
	public int getServerPort() {
		return serverPort;
	}
	/**
	 * getServletPath method comment.
	 */
	public java.lang.String getServletPath() {
		return this.servletPath;
	}
	/**
	 * getSession method comment.
	 */
	public javax.servlet.http.HttpSession getSession() {
		return getSession(false);
	}
	/**
	 *
	 */
	public HttpSession getSession(boolean b) {

		HttpSession session = null;

		//Access the current static Hashtable of sessions, using the current
		// sesessionid header parameter.
		String id = this.getHeader(SESSION_HEADER_KEY);
		if (id == null) {

			//No header, use the default
			session = getSessionForId(DEFAULT_SESSION_ID);

		} else {

			//Header there, use it to grab the session
			session = getSessionForId(id);

		}

		return session;
	}
	/**
	 *
	 */
	public static HttpSession getSessionForId(String id) {

		if (sessions == null) {
			resetSessions();
		}

		HttpSession session = null;
		Object sessionObj = sessions.get(id);

		//Always answer a Session!
		if (sessionObj == null) {
			session = new MockHttpSession(id);
			sessions.put(id, session);
		} else {
			session = (HttpSession) sessionObj;
		}

		return session;
	}
	public java.security.Principal getUserPrincipal() {
		return null;
	}
	/**
	 * isRequestedSessionIdFromCookie method comment.
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}
	/**
	 * isRequestedSessionIdFromUrl method comment.
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}
	/**
	 * isRequestedSessionIdFromURL method comment.
	 */
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}
	/**
	 * isRequestedSessionIdValid method comment.
	 */
	public boolean isRequestedSessionIdValid() {
		return false;
	}
	public boolean isSecure() {
		return false;
	}
	public boolean isUserInRole(String role) {
		return roles.containsKey(role);
	}
	/**
	 * @param key
	 *            java.lang.String
	 * @param value
	 *            java.lang.Object
	 */
	public void putParameter(String key, Object value) {
	    List valueList = new ArrayList();
	    valueList.add(value);
		this.parameters.put(key, valueList);
		return;
	}
	public void removeAttribute(String name) {
	}
	/**
	 * @param key
	 *            java.lang.String
	 */
	public void removeParameter(String key) {
		parameters.remove(key);
		return;
	}
	/**
	 *
	 */
	public static void resetSessionId(String id) {

		if (sessions == null) {
			resetSessions();
		} else {
			sessions.remove(id);
		}

		return;
	}
	/**
	 *
	 */
	public static void resetSessions() {

		sessions = new Hashtable();

		return;
	}
	/**
	 *
	 */
	public static void resetDefaultSession() {

		resetSessionId(DEFAULT_SESSION_ID);

		return;

	}
	/**
	 * setAttribute method comment.
	 */
	public void setAttribute(String key, Object value) {

		attributes.put(key, value);
	}
	/**
	 * setCharacterEncoding method comment.
	 */
	public void setCharacterEncoding(java.lang.String arg1)
			throws java.io.UnsupportedEncodingException {
	}
	public void setContentType(String contentType) {
		setHeader("Content-Type", contentType);
	}
	/**
	 * setContextPath method comment.
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public void setHeader(String key, Object value) {
		this.headers.put(key.toLowerCase(), value);
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	/**
	 * set the HTTP request method.
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	public void setParameter(String parameterName, String value) {
	    if (parameters.get(parameterName) == null) {
	        List valueList = new ArrayList();
	        valueList.add(value);
	        parameters.put(parameterName, valueList);
	    } else {
	        ((List)parameters.get(parameterName)).add(value);
	    }
	}
	/**
	 * setAttribute method comment.
	 */
	public void setParametersDictionary(Hashtable dict) {
		parameters = dict;
	}
	/**
	 * setRequestURI method comment.
	 */
	public void setRequestURI(String uri) {
		requestURI = uri;
		return;
	}
	/**
	 * Insert the method's description here. Creation date: (5/31/2001 12:43:30
	 * PM)
	 *
	 * @param newScheme
	 *            java.lang.String
	 */
	public void setScheme(String newScheme) {
		scheme = newScheme;
	}
	/**
	 * Insert the method's description here. Creation date: (5/31/2001 12:43:30
	 * PM)
	 *
	 * @param newServerPort
	 *            int
	 */
	public void setServerPort(int newServerPort) {
		serverPort = newServerPort;
	}
	public void setServletPath(String path) {
		this.servletPath = path;
	}
	public void setSessionId(String sessionId) {
		setHeader(SESSION_HEADER_KEY, sessionId);
	}
    public int getRemotePort() {
        return 0;
    }
    public String getLocalName() {
        return null;
    }
    public String getLocalAddr() {
        return null;
    }
    public int getLocalPort() {
        return 0;
    }
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public void setServletContext(MockServletContext servletContext) {
		this.servletContext = servletContext;
	}
	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}
}