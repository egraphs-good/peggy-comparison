package eqsat.meminfer.engine.basic;

public final class Ambassador<T extends Term<T,V>, V extends Value<T,V>>
		extends Representative<V> {
	private T mTerm;
	
	public Ambassador(V value) {
		super(value);
		getValue().addAmbassador(this);
	}
	
	public String toString() {return "Ambassador(" + getValue() + ")";}

	public boolean hasTerm() {return mTerm != null;}
	public T getTerm() {return mTerm;}
	public void setTerm(T term) {
		if (hasTerm() || !getValue().equals(term.getValue()))
			throw new IllegalStateException();
		mTerm = term;
	}
	
	public boolean isAmbassador() {return true;}
}
