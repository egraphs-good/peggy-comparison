package eqsat.meminfer.engine.basic;

public interface TermOrTermChild<T, V> {
	boolean equals(TermOrTermChild<T,V> that);
	
	boolean isTerm();
	T getTerm();
	
	boolean isTermChild();
	T getParentTerm();
	int getChildIndex();
	
	T asTerm();
	
	Representative<V> getRepresentative();
	V getValue();
}
