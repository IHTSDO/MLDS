package ca.intelliware.mockj2ee.servlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.FastDateFormat;
/**
 * Purpose: Provide a Test implementation of an HttpResponse.
 *
 *
 *
 * Creation date: (6/27/00 11:43:12 AM)
 * @version:
 * @author Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockHttpServletResponse implements HttpServletResponse {
	private int status = SC_OK;
	private Locale locale = Locale.US;
	private Map<String,Cookie> cookies = new HashMap<String,Cookie>();
	private String redirectURL;
	private String contentType;
	private Map<String,String> headers = new HashMap<String,String>();
	private PrintWriter writer;
	private ServletInputStream inputStream;
	private ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
	private ServletOutputStream outputStream = new MockServletOutputStream(
			byteArrayOut);
	/**
	 * HttpResponseTest constructor comment.
	 */
	public MockHttpServletResponse() {
		super();
	}
	/**
	 * addCookie method comment.
	 */
	public void addCookie(Cookie arg1) {
		this.cookies.put(arg1.getName(), arg1);
	}
	public void addDateHeader(String name, long value) {
		setDateHeader(name, value);
	}
	public void addHeader(String name, String value) {
		setHeader(name, value);
	}
	public void addIntHeader(String name, int value) {
		setHeader(name, Integer.toString(value));
	}
	/**
	 * containsHeader method comment.
	 */
	public boolean containsHeader(java.lang.String arg1) {
		return false;
	}
	/**
	 * encodeRedirectUrl method comment.
	 */
	public java.lang.String encodeRedirectUrl(java.lang.String arg1) {
		return arg1;
	}
	/**
	 * encodeRedirectURL method comment.
	 */
	public java.lang.String encodeRedirectURL(java.lang.String arg1) {
		return arg1;
	}
	/**
	 * encodeUrl method comment.
	 */
	public java.lang.String encodeUrl(java.lang.String arg1) {
		return arg1;
	}
	/**
	 * encodeURL method comment.
	 */
	public java.lang.String encodeURL(java.lang.String arg1) {
		return arg1;
	}
	public void flushBuffer() throws IOException {
	}
	public int getBufferSize() {
		return 0;
	}
	/**
	 * getCharacterEncoding method comment.
	 */
	public java.lang.String getCharacterEncoding() {
		return null;
	}
	public Cookie getCookie(String name) {
		return (Cookie) this.cookies.get(name);
	}
	/**
	 * getOutputStream method comment.
	 */
	public ServletInputStream getInputStream() throws java.io.IOException {
		if (this.inputStream == null) {
			byte[] data = getOutputStreamContents();
			ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(data);
			this.inputStream = new MockServletInputStream(byteArrayIn);
		}
		return this.inputStream;
	}
	public java.util.Locale getLocale() {
		return this.locale;
	}
	/**
	 * getOutputStream method comment.
	 */
	public javax.servlet.ServletOutputStream getOutputStream()
			throws java.io.IOException {
		return this.outputStream;
	}
	public byte[] getOutputStreamContents() {
		try {
			if (this.writer != null) {
				this.writer.close();
			} else {
				this.outputStream.close();
			}
		} catch (IOException e) {
			//TODO ignore
		}
		return this.byteArrayOut.toByteArray();
	}
	/**
	 * Added a test method for access to the URL redirect
	 */
	public String getRedirectURL() {
		return this.redirectURL;
	}
	/**
	 * not part of the servlet api interfaces added as a convenience for testing
	 */
	public int getStatus() {
		return this.status;
	}
	/**
	 * getWriter method comment.
	 */
	public PrintWriter getWriter() throws IOException {
		if (this.writer == null) {
			this.writer = new PrintWriter(getOutputStream());
		}
		return this.writer;
	}
	public boolean isCommitted() {
		return false;
	}
	public void reset() {
	}
	/**
	 * resetBuffer method comment.
	 */
	public void resetBuffer() {
	}
	/**
	 * sendError method comment.
	 */
	public void sendError(int status) throws java.io.IOException {
		this.status = status;
	}
	/**
	 * sendError method comment.
	 */
	public void sendError(int status, String msg) throws java.io.IOException {
		sendError(status);
	}
	/**
	 * sendRedirect method comment.
	 */
	public void sendRedirect(String redirectURL) throws IOException {
		this.redirectURL = redirectURL;
	}
	public void setBufferSize(int size) {
	}
	/**
	 * setContentLength method comment.
	 */
	public void setContentLength(int arg1) {
	}
	/**
	 * setContentType method comment.
	 */
	public void setContentType(java.lang.String type) {
		this.contentType = type;
	}
	/**
	 * setDateHeader method comment.
	 */
	public void setDateHeader(java.lang.String headerName, long timeInMilliseconds) {
		Date date = new Date(timeInMilliseconds);
		setHeader(headerName, FastDateFormat.getInstance("EEE, d MMM yyyy HH:mm:ss z").format(date));
	}
	/**
	 * setIntHeader method comment.
	 */
	public void setIntHeader(java.lang.String arg1, int arg2) {
	}
	public void setLocale(java.util.Locale locale) {
		this.locale = locale;
	}
	/**
	 * setStatus method comment.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * setStatus method comment.
	 */
	public void setStatus(int status, java.lang.String arg2) {
		this.status = status;
	}
	public boolean isRedirected() {
		return this.redirectURL != null;
	}
	/**
	 * Get a given header if contained in the map
	 */
	public String getHeader(String key) {
		String ret = null;
		if (key != null && key.length() > 0 &&
			this.headers.containsKey(key)) {
			ret = (String) this.headers.get(key); 
		}
		return ret;
	}
	
	public void setHeader(String headerName, String headerValue) {
		if (headerName != null && headerName.length() > 0) {
			if (headerValue != null && headerValue.length() > 0) {
				this.headers.put(headerName, headerValue);
			} 
			else {
				if (this.headers.containsKey(headerName)) {
					this.headers.remove(headerName);
				}
			}
		}
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setCharacterEncoding(String arg0) {
		
	}
	@Override
	public Collection<String> getHeaders(String name) {
		return Arrays.asList(getHeader(name));
	}
	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}
}