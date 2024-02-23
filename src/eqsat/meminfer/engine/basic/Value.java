package eqsat.meminfer.engine.basic;

import java.util.Collection;

public abstract class Value<T extends Term<T,V>, V extends Value<T,V>> {
	public abstract boolean equals(Object that);
	public abstract boolean equals(V that);
	
	public abstract Collection<? extends T> getTerms();
	
	public abstract void addTerm(T term);
	public abstract void addAmbassador(Ambassador<T,V> ambassador);
	
	public abstract void removeTerm(T term);
}
