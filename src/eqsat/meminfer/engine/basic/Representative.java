package eqsat.meminfer.engine.basic;

public abstract class Representative<V> {
	private final V mValue;
	
	protected Representative(V value) {mValue = value;}
	protected Representative(RepresentativeConstructor<V> constructor) {
		this(constructor.getValueManager().createValue());
	}
	
	public final V getValue() {return mValue;}
	
	public boolean isTerm() {return false;}
	public boolean isAmbassador() {return false;}
	
	public boolean isRemoved() {return false;}
	
	public final boolean equals(Object that) {return this == that;}
	public final int hashCode() {return System.identityHashCode(this);}
}
