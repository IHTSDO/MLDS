package ca.intelliware.mockj2ee.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A Test implementation of an HttpOutputStream.
 * 
 * @author Deth Thirakul
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */

public class MockHttpServletOutputStream extends javax.servlet.ServletOutputStream {

    private OutputStream out;
    private boolean isStandardOut = true; // if true; output goes to System.out; if
                                            // false; output goes to a
                                            // ByteArrayOutputStream

    /**
     * TestHttpOutputStream constructor comment.
     */
    protected MockHttpServletOutputStream() {
        super();
    }

    /**
     * 
     */
    public ByteArrayOutputStream getByteArrayOutputStream() {


        return (ByteArrayOutputStream) out;
    }

    /**
     * 
     */
    public OutputStream getOutputStream() {

        if (out == null) {
            if (isStandardOut) {
                out = System.out;
            } else {
                out = new ByteArrayOutputStream();
            }

        }

        return out;
    }

    /**
     * 
     * 
     */
    public void setIsStandardOut(boolean flag) {

        isStandardOut = flag;

        return;
    }

    /**
     * 
     * @param arg1
     *            int
     */
    public void write(int intValue) throws IOException {

        getOutputStream().write(intValue);

        return;
    }
}
