package eqsat.meminfer.engine.basic;

public interface Structure<T> {
	int getTermCount();
	T getTerm(int index);
	boolean isComplete();
	boolean isRemoved();
}
