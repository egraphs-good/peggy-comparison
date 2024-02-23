package eqsat.meminfer.engine.basic;

import java.util.Arrays;
import java.util.List;

import util.UnhandledCaseException;

public abstract class Term<T extends Term<T,V>, V extends Value<T,V>>
		extends Representative<V>
		implements Structure<T>, TermOrTermChild<T,V> {
	protected final Representative<V>[] mChildren;
	protected boolean mRemoved = false;
	
	public Term(V value, Representative<V>... children) {
		super(value);
		mChildren = children;
		getValue().addTerm(getSelf());
	}
	public Term(RepresentativeConstructor<V> constructor,
			Representative<V>... children) {
		super(constructor);
		mChildren = children;
		getValue().addTerm(getSelf());
	}
	public Term(TermConstructor<V> constructor) {
		this(constructor.getSuper(), constructor.getChildren());
	}
	
	public boolean equals(TermOrTermChild<T,V> that) {return this == that;}
	public boolean equals(T that) {return this == that;}
	
	protected abstract T getSelf();
	
	public final int getArity() {return mChildren.length;}
	
	public final Representative<V> getChild(int child) {
		return mChildren[child];
	}
	public final List<? extends Representative<V>> getChildren() {
		return Arrays.asList(mChildren);
	}
	
	public final int getTermCount() {return 1;}
	public final T getTerm(int index) {
		if (index == 0)
			return getSelf();
		else
			throw new IndexOutOfBoundsException();
	}
	
	public final boolean isTerm() {return true;}
	public final T getTerm() {return getSelf();}
	public final Representative<V> getRepresentative() {return this;}
	
	public final boolean isTermChild() {return false;}
	public final T getParentTerm() {throw new UnsupportedOperationException();}
	public final int getChildIndex() {
		throw new UnsupportedOperationException();
	}
	
	public T asTerm() {return getSelf();}
	
	public T getChildAsTerm(int child) {
		if (mChildren[child].isTerm())
			return (T)mChildren[child];
		else if (mChildren[child].isAmbassador())
			return ((Ambassador<T,V>)mChildren[child]).getTerm();
		else
			throw new UnhandledCaseException();
	}
	
	public boolean isComplete() {return true;}
	
	public void remove() {mRemoved = true;}
	public boolean isRemoved() {return mRemoved;}
}
