package ca.intelliware.mockj2ee.servlet;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * @author BC Holmes
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockFilterConfigImpl implements FilterConfig {
	
	private ServletContext servletContext;
    private Hashtable hashtable = new Hashtable();
    private String filterName;
	
	public MockFilterConfigImpl() {
    }
    public MockFilterConfigImpl(String filterName) {
        this.filterName = filterName;
    }
    public String getInitParameter(String name) {
		return (String) this.hashtable.get(name);
	}
	public Enumeration getInitParameterNames() {
        return this.hashtable.keys();
	}
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
    public String getFilterName() {
        return this.filterName;
    }
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
