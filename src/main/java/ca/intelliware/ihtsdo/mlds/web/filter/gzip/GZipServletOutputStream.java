package ca.intelliware.ihtsdo.mlds.web.filter.gzip;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;
import java.io.OutputStream;

class GZipServletOutputStream extends ServletOutputStream {
    private OutputStream stream;

    public GZipServletOutputStream(OutputStream output)
            throws IOException {
        super();
        this.stream = output;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override
    public void write(byte b[]) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener listener) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}
