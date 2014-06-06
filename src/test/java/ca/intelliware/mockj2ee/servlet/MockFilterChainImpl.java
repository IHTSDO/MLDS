package ca.intelliware.mockj2ee.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author BC Holmes
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockFilterChainImpl implements FilterChain {
	private boolean called = false;

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		this.called = true;
	}
	/**
	 * @return Returns the called.
	 */
	public boolean wasCalled() {
		return this.called;
	}
	/**
	 * @param called The called to set.
	 */
	public void setCalled(boolean called) {
		this.called = called;
	}
}
