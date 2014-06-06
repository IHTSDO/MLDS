package ca.intelliware.mockj2ee.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * @uthor Greg Betty
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockServletOutputStream extends ServletOutputStream {
    protected OutputStream out;

    public MockServletOutputStream(OutputStream out) {
        super();
        this.out = out;
    }

    public void close() throws IOException {
        out.close();
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void write(byte b[], int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void write(int b) throws java.io.IOException {
        out.write(b);
    }
}
