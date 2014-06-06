package ca.intelliware.mockj2ee.servlet;

import java.util.Hashtable;

/**
 * A Test implementation of an HttpSession.
 *
 * @author Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */

public class MockHttpSession implements javax.servlet.http.HttpSession {

	Hashtable values = new Hashtable();
	private String sessionId;

	
	public MockHttpSession() {
	}
	public MockHttpSession(String sessionId) {
		this.sessionId = sessionId;
	}


    public Object getAttribute(String name) {
        return getValue(name);
    }
    public java.util.Enumeration getAttributeNames() {
        return values.keys();
    }
/**
 * getCreationTime method comment.
 */
public long getCreationTime() {
	return 0;
}
/**
 * getId method comment.
 */
public String getId() {
	return this.sessionId;
}
/**
 * getLastAccessedTime method comment.
 */
public long getLastAccessedTime() {
	return 0;
}
/**
 * getMaxInactiveInterval method comment.
 */
public int getMaxInactiveInterval() {
	return 0;
}
/**
 * getServletContext method comment.
 */
public javax.servlet.ServletContext getServletContext() {
	return null;
}
/**
 * getSessionContext method comment.
 * @deprecated
 */
public javax.servlet.http.HttpSessionContext getSessionContext() {
	return null;
}
/**
 *
 */
public Object getValue(String key) {
	return values.get(key);
}
/**
 * getValueNames method comment.
 */
public java.lang.String[] getValueNames() {
	return null;
}
/**
 * invalidate method comment.
 */
public void invalidate() {
}
/**
 * isNew method comment.
 */
public boolean isNew() {
	return false;
}
/**
 *
 */
public void putValue(String key, Object value) {
	values.put(key, value);
	return;
}
    public void removeAttribute(String name) {
        removeValue(name);
    }
/**
 * removeValue method comment.
 */
public void removeValue(String value) {
	this.values.remove(value);
}
    public void setAttribute(String name,
                             Object value) {
        putValue(name, value);
    }
/**
 * setMaxInactiveInterval method comment.
 */
public void setMaxInactiveInterval(int arg1) {}
}
