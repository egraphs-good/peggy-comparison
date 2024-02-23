package peggy.analysis;

/**
 * This interface defines any type of mathematical lattice. 
 */
public interface Lattice<T> {
	public T bottom();
	public T top();
	public T lub(T left, T right);
	public T glb(T left, T right);
	public boolean isLower(T lower, T higher);
}
