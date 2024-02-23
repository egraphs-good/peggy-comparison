package util;

public final class Out<T> {
	private T mOut;
	
	public Out() {this(null);}
	public Out(T initial) {mOut = initial;}
	
	public void set(T out) {mOut = out;}
	public T get() {return mOut;}
	public boolean isSet() {return mOut != null;}
}
