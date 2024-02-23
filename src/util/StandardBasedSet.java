package util;

public class StandardBasedSet<E extends Based<E>> extends BasedSet<E> {
	public StandardBasedSet(Basis<E> basis) {super(basis);}

	public boolean add(E element) {return mBits.add(element.getBasisIndex());}
	
	public boolean contains(Object element) {
		E based = mBasis.getElement(element);
		return based != null && mBits.contains(based.getBasisIndex());
	}
	
	public boolean contains(E element) {
		return element != null && mBits.contains(element.getBasisIndex());
	}
	
	public boolean remove(Object element) {
		E based = mBasis.getElement(element);
		return based != null && mBits.remove(based.getBasisIndex());
	}
	
	public boolean remove(E element) {
		return element != null && mBits.remove(element.getBasisIndex());
	}
}
