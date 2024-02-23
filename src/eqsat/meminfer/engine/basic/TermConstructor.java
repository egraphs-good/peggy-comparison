package eqsat.meminfer.engine.basic;

public final class TermConstructor<V> {
	private final RepresentativeConstructor<V> mSuper;
	private final Representative<V>[] mChildren;
	
	public TermConstructor(RepresentativeConstructor<V> sup,
			Representative<V>[] children) {
		mSuper = sup;
		mChildren = children;
	}
	
	public RepresentativeConstructor<V> getSuper() {return mSuper;}
	public Representative<V>[] getChildren() {return mChildren;}
}
