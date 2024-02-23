package util;

public interface Basis<E> {
	public E getElement(Object potential);
	public int getIndex(E element);
	public E getElement(int basisIndex);
}
