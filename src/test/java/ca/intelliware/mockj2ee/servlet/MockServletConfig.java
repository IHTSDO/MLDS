package ca.intelliware.mockj2ee.servlet;

import java.util.Hashtable;

/**
 * @author: Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockServletConfig implements javax.servlet.ServletConfig {

	private Hashtable initParams = new Hashtable();
	private MockServletContext context;
	private String servletName;

	public MockServletConfig() {
		this(null, new MockServletContext());
	}

	public MockServletConfig(MockServletContext context) {
		this(null, context);
	}

	public MockServletConfig(String servletName, MockServletContext context) {
		this.context = context;
		this.servletName = servletName;
	}

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public String getInitParameter(String key) {

		String value = (String) initParams.get(key);
		return value;
	}
	/**
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration getInitParameterNames() {
		return initParams.keys();
	}
	/**
	 * @return javax.servlet.ServletContext
	 */
	public javax.servlet.ServletContext getServletContext() {
		return this.context;
	}
	public String getServletName() {
		return this.servletName;
	}
	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public void setInitParameter(String key, String value) {

		initParams.put(key, value);

		return;
	}
}