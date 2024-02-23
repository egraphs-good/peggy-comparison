package peggy.represent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a two-way mapping of A's to B's.
 * Each A must correspond to exactly one B, and vice versa.
 */
public class BijectionMapping<A,B> {
	private final Map<A,B> atob;
	private final Map<B,A> btoa;
	
	public BijectionMapping() {
		this.atob = new HashMap<A,B>();
		this.btoa = new HashMap<B,A>();
	}
	public void put(A a, B b) {
		this.atob.put(a,b);
		this.btoa.put(b,a);
	}
	public A getByB(B b) {
		return this.btoa.get(b);
	}
	public B getByA(A a) {
		return this.atob.get(a);
	}
	public Collection<? extends Map.Entry<A,B>> getEntries() {
		return this.atob.entrySet();
	}
	public boolean hasA(A a) {
		return this.atob.containsKey(a);
	}
	public boolean hasB(B b) {
		return this.btoa.containsKey(b);
	}
}
