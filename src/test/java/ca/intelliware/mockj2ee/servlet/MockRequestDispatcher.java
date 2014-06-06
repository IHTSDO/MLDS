package ca.intelliware.mockj2ee.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockRequestDispatcher implements RequestDispatcher {
    
    private MockServletContext context;

	private String path = null;
	int numberOfTimesForwardHasBeenCalled = 0;
	int numberOfTimesIncludeHasBeenCalled = 0;

    public MockRequestDispatcher(MockServletContext context) {
        this.context = context;
    }
    public MockRequestDispatcher(MockServletContext context, String path) {
        this.context = context;
        setPath(path);
    }
    public void forward(ServletRequest request, ServletResponse response)
        throws ServletException, IOException {
        this.context.setMostRecentlyUsedDispatcher(this);
        this.numberOfTimesForwardHasBeenCalled ++;
    }
	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 9:17:36 PM)
	 * @return int
	 */
	public int getNumberOfTimesForwardHasBeenCalled() {
		return this.numberOfTimesForwardHasBeenCalled;
	}
    public void include(ServletRequest request, ServletResponse response)
        throws ServletException, IOException {
        this.context.setMostRecentlyUsedDispatcher(this);
        this.numberOfTimesIncludeHasBeenCalled ++;
    }
    public void setPath(String path) {
        this.path = path;
    }
	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		return this.path;
	}
}
