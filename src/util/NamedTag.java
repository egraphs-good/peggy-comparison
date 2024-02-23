package util;

public class NamedTag<T> implements Tag<T> {
	protected final String mName;
	
	public NamedTag(String name) {mName = name;}
	
	public String toString() {return mName;}
}
