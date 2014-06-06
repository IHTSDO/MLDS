package ca.intelliware.mockj2ee.servlet;
/**
 * A Test implementation of an ServletContext.
 *
 * @author Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
public class MockServletContext implements ServletContext {
	
	private Map resourceMap = new HashMap();
	private String basePath;
	private String servletContextName;
	public Hashtable requestDispatchers = new Hashtable();
	private Hashtable attributes = new Hashtable();
	private File webRootDirectory;
	private Hashtable initParameters = new Hashtable();
	private String serverInfo = "Mock Server version 1.5";
	
	private MockRequestDispatcher mostRecentlyUsedDispatcher;
	
	/**
	 * @return java.lang.Object
	 * @param key
	 *            java.lang.String
	 */
	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}
	/**
	 * getAttributeNames method comment.
	 */
	public java.util.Enumeration getAttributeNames() {
		return this.attributes.keys();
	}
	/**
	 * getContext method comment.
	 */
	public javax.servlet.ServletContext getContext(java.lang.String arg1) {
		return null;
	}
	public String getInitParameter(String name) {
		return (String) this.initParameters.get(name);
	}
	public Enumeration getInitParameterNames() {
		return this.initParameters.keys();
	}
	public boolean setInitParameter(String name, String value) {
		this.initParameters.put(name, value);
		return true;
	}
	/**
	 * getMajorVersion method comment.
	 */
	public int getMajorVersion() {
		return 0;
	}
	/**
	 * @return java.lang.String
	 * @param type
	 *            java.lang.String
	 */
	public String getMimeType(String type) {
		return null;
	}
	/**
	 * getMinorVersion method comment.
	 */
	public int getMinorVersion() {
		return 0;
	}
	public RequestDispatcher getNamedDispatcher(String name) {
		return this.getRequestDispatcher(name);
	}
	/**
	 * @return String
	 */
	public String getRealPath(String path) {
		String tmpPath = path;
		if (this.basePath == null) {
			return null;
		}
		char fileSeparator = System.getProperty("file.separator").charAt(0);
		if (fileSeparator != '/') {
			tmpPath = tmpPath.replace('/', fileSeparator);
		}
		if (this.basePath.charAt(this.basePath.length() - 1) == fileSeparator
				&& tmpPath.charAt(0) == fileSeparator) {
			tmpPath = tmpPath.substring(1);
		}
		if (this.basePath.charAt(this.basePath.length() - 1) != fileSeparator
				&& tmpPath.charAt(0) != fileSeparator) {
			tmpPath = fileSeparator + tmpPath;
		}
		return this.basePath + tmpPath;
	}
	/**
	 * Return a new
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		if (!this.requestDispatchers.containsKey(path)) {
			RequestDispatcher dispatcher = new MockRequestDispatcher(this, path);
			this.requestDispatchers.put(path, dispatcher);
		}
		return (RequestDispatcher) this.requestDispatchers.get(path);
	}
	public Hashtable getRequestDispatchers() {
		return this.requestDispatchers;
	}
	/**
	 * getResource method comment.
	 */
	public URL getResource(java.lang.String resourceName)
			throws java.net.MalformedURLException {
		File file = getResourceAsFile(resourceName);
		return file == null ? null : file.toURL();
	}
	/**
	 * @param resourceName
	 * @return
	 */
	private File getResourceAsFile(java.lang.String resourceName) {
		Object object = this.resourceMap.get(resourceName);
		if (object == null && this.webRootDirectory != null) {
			String fileName = "." + resourceName.replace('/', File.separatorChar);
			File file = new File(this.webRootDirectory, fileName);
			return file.exists() ? file : null;
		} else if (object instanceof File) {
			return (File) object;
		} else {
			return null;
		}
	}
	/**
	 * getResourceAsStream method comment.
	 */
	public InputStream getResourceAsStream(String resourceName) {
		File file = getResourceAsFile(resourceName);
		try {
			return file == null ? null : new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	/**
	 * getResourcePaths method comment.
	 */
	public java.util.Set getResourcePaths(java.lang.String arg1) {
		return null;
	}
	/**
	 * @return javax.servlet.Servlet
	 * @param name
	 *            java.lang.String
	 */
	public javax.servlet.Servlet getServlet(String name) {
		return null;
	}
	/**
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration getServletNames() {
		return null;
	}
	/**
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration getServlets() {
		return null;
	}
	/**
	 * @param e
	 *            java.lang.Exception
	 * @param msg
	 *            java.lang.String
	 */
	public void log(java.lang.Exception e, String msg) {
	}
	/**
	 * @param msg
	 *            java.lang.String
	 */
	public void log(String msg) {
		return;
	}
	/**
	 * log method comment.
	 */
	public void log(java.lang.String arg1, java.lang.Throwable arg2) {
	}
	/**
	 * removeAttribute method comment.
	 */
	public void removeAttribute(java.lang.String arg1) {
	}
	/**
	 * setAttribute method comment.
	 */
	public void setAttribute(java.lang.String name, java.lang.Object value) {
		this.attributes.put(name, value);
	}
	/**
	 * @param newBasePath
	 *            String
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	/**
	 * @return Returns the servletContextName.
	 */
	public String getServletContextName() {
		return this.servletContextName;
	}
	/**
	 * @param servletContextName The servletContextName to set.
	 */
	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}
	
	/**
	 * Consider using the setWebRootDirectory method instead.  It's sexier.
	 * @param name
	 * @param file
	 */
	public void addResource(String name, File file) {
		this.resourceMap.put(name, file);
	}
    public MockRequestDispatcher getMostRecentlyUsedDispatcher() {
        return this.mostRecentlyUsedDispatcher;
    }
    public void setMostRecentlyUsedDispatcher(
            MockRequestDispatcher mostRecentlyUsedDispatcher) {
        this.mostRecentlyUsedDispatcher = mostRecentlyUsedDispatcher;
    }
	
	public void setWebRootDirectory(File webRootDirectory) {
		this.webRootDirectory = webRootDirectory;
        if (this.basePath == null) {
            this.basePath = webRootDirectory.getAbsolutePath();
        }
	}
	/**
	 * Returns the name and version of the servlet container on which 
	 * the servlet is running.
	 */
	public String getServerInfo() {
		return this.serverInfo;
	}
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}
	public String getContextPath() {
		return null;
	}
	@Override
	public int getEffectiveMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getEffectiveMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Dynamic addServlet(String servletName, String className) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Dynamic addServlet(String servletName,
			Class<? extends Servlet> servletClass) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, String className) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, Class<? extends Filter> filterClass) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T extends Filter> T createFilter(Class<T> clazz)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setSessionTrackingModes(
			Set<SessionTrackingMode> sessionTrackingModes) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addListener(String className) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public <T extends EventListener> void addListener(T t) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public <T extends EventListener> T createListener(Class<T> clazz)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void declareRoles(String... roleNames) {
		// TODO Auto-generated method stub
		
	}
    
}