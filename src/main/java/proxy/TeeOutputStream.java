package proxy;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends FilterOutputStream {
	
	private OutputStream copy;

	public TeeOutputStream(OutputStream out, OutputStream copy) {
		super(out);
		this.copy = copy;
	}

	@Override
	public void close() throws IOException {
		out.close();
		copy.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
		copy.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		copy.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
		copy.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		copy.write(b);
	}
	
	
	
}
