package llvm.types;

import java.util.Iterator;

/**
 * Represents a pointer type in LLVM.
 * A pointer has an element type and an address space number (default=0)
 */
public class PointerType extends CompositeType {
	protected final Type pointee;
	protected final int addressSpace;
	
	public PointerType(Type _pointee, int _addressSpace) {
		if (_pointee == null)
			throw new NullPointerException("pointee is null");
		if (_addressSpace < 0)
			throw new IllegalArgumentException("addressSpace must be >= 0");
		this.pointee = _pointee;
		this.addressSpace = _addressSpace;
	}
	public PointerType(Type _pointee) {
		if (_pointee == null)
			throw new NullPointerException("pointee is null");
		this.pointee = _pointee;
		this.addressSpace = 0;
	}
	
	public Type getPointeeType() {return this.pointee;}
	public int getAddressSpace() {return this.addressSpace;}
	
	public boolean isElementIndexValid(int index) {return true;}
	public Type getElementType(int index) {return this.pointee;}
	
	public boolean isPointer() {return true;}
	public PointerType getPointerSelf() {return this;}
	protected void ensureSized() {}
	public boolean hasTypeSize() {return true;}
	public long getTypeSize() {return 32L;}
	public Iterator<Type> getSubtypes() {
		return new TypeIterator(this.pointee);
	}

	protected String toString(int depth) {
		if (depth==0) return "?";

		String result = this.pointee.toString(depth-1);
		if (this.addressSpace != 0) {
			result += " addrspace(" + this.addressSpace + ")*";
		} else {
			result += "*";
		}
		return result;
	}
	public boolean equalsType(Type o) {
		if (!(o.isComposite() && o.getCompositeSelf().isPointer()))
			return false;
		return TypeEqualityCheck.equals(this, o);
	}
	protected int hashCode(int depth) {
		if (depth==0) return 0;
		return this.pointee.hashCode(depth-1)*521 + this.addressSpace*509;
	}
}
