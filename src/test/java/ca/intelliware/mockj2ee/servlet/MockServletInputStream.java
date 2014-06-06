package ca.intelliware.mockj2ee.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;
/**
 * 
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockServletInputStream extends ServletInputStream {

	protected InputStream in;
public MockServletInputStream(InputStream in) {
	super();

	this.in = in;
}
public int available() throws IOException {
	return in.available();
}
public void close() throws IOException {
	in.close();
}
public synchronized void mark(int readlimit) {
	in.mark(readlimit);
}
public boolean markSupported() {
	return in.markSupported();
}
public int read() throws IOException {
	return in.read();
}
public int read(byte b[], int off, int len) throws IOException {
	return in.read(b, off, len);
}
public synchronized void reset() throws IOException {
	in.reset();
}
public long skip(long n) throws IOException {
	return in.skip(n);
}
}
