package peggy.input;

/**
 * An immutable character buffer with an implicit index position, that can
 * be updated as the buffer is read.
 */
public class Buffer {
	private CharSequence sequence;
	private int index;

	public Buffer(CharSequence _sequence) {
		this.sequence = _sequence;
		this.index = 0;
	}
	public int getIndex() {return this.index;}
	public int setIndex(int newindex) {
		if (newindex < 0 || newindex > sequence.length())
			throw new IndexOutOfBoundsException(""+newindex);
		int old = this.index;
		this.index = newindex;
		return old;
	}
	public int inc() {return inc(1);}
	public int inc(int n) {
		int newindex = this.index+n;
		if (n < 0 || newindex > this.sequence.length())
			throw new IllegalArgumentException("" + n);
		int oldindex = this.index;
		this.index = newindex;
		return oldindex;
	}
	public int dec() {return dec(1);}
	public int dec(int n) {
		int newindex = this.index-n;
		if (n < 0 || newindex < 0)
			throw new IllegalArgumentException("" + n);
		int oldindex = this.index;
		this.index = newindex;
		return oldindex;
	}
	public CharSequence subsequence(int start, int end) {
		return this.sequence.subSequence(start, end);
	}
	
	public char peek() {return this.sequence.charAt(this.index);}
	public char read() {return this.sequence.charAt(this.index++);}
	public char charAt(int i) {return this.sequence.charAt(i);}
	public boolean atEnd() {return this.index >= this.sequence.length();}
	public boolean hasN(int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n must be positive: " + n);
		return this.index+n <= this.sequence.length();
	}
	public char peekNth(int n) {
		return this.sequence.charAt(this.index + n);
	}
	public boolean nextN(String str) {
		if (this.index + str.length() > this.sequence.length())
			return false;
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) != this.sequence.charAt(this.index+i))
				return false;
		return true;
	}

	public int skipWS() {
		while (this.index < sequence.length()) {
			switch (sequence.charAt(this.index)) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				this.index++;
				break;
			default:
				return this.index;
			}
		}
		return this.index;
	}
}
