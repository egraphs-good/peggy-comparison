package peggy.pb;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A composite output stream that counts the number of bytes written.
 */
public class CounterOutputStream extends OutputStream {
	private final OutputStream inner;
	private int writtenBytes = 0;
	
	public CounterOutputStream(OutputStream _inner) {
		this.inner = _inner;
	}
	public int getWrittenByteCount() {return this.writtenBytes;}
	public void close() throws IOException {
		this.inner.close();
	}
	public void flush() throws IOException {
		this.inner.flush();
	}
	public void write(byte[] b) throws IOException {
		this.inner.write(b);
		this.writtenBytes += b.length;
	}
	public void write(byte[] b, int off, int len) throws IOException {
		this.inner.write(b, off, len);
		this.writtenBytes += len;
	}
	public void write(int b) throws IOException {
		this.inner.write(b);
		this.writtenBytes++;
	}
}
