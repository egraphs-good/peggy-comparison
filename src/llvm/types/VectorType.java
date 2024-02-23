package llvm.types;

import java.util.Iterator;

import llvm.bitcode.UnsignedLong;

/**
 * This class represents the vector type in LLVM.
 * A vector can only have integer or floating point elements.
 * The size of the vector is encoded in the type.
 */
public class VectorType extends CompositeType {
	protected final Type elementType;
	protected final UnsignedLong size; // must be power of 2 (only in 2.3)
	
	public VectorType(Type _elementType, long _size) {
		this(_elementType, new UnsignedLong(_size));
	}
	public VectorType(Type _elementType, UnsignedLong _size) {
		if (_elementType == null)
			throw new NullPointerException("elementType is null");
		if (!(_elementType.isInteger() || _elementType.isFloatingPoint()))
			throw new IllegalArgumentException("elementType must be integer or floating point type: " + _elementType);
		this.elementType = _elementType;
		this.size = _size;
	}
	
	public Type getElementType() {return this.elementType;}
	public UnsignedLong getNumElements() {return this.size;}

	protected void ensureSized() {this.elementType.ensureSized();}
	public boolean hasTypeSize() {return true;}
	public long getTypeSize() {return this.size.signedValue() * this.elementType.getTypeSize();}
	public boolean isElementIndexValid(int index) {
		return true;
	}
	
	public boolean isVectorOfFloatingPoint() {
		return this.elementType.isFloatingPoint();
	}
	public boolean isVectorOfInteger() {
		return this.elementType.isInteger();
	}
	
	public Type getElementType(int index) {
		if (!this.isElementIndexValid(index))
			throw new IndexOutOfBoundsException(""+index);
		return this.elementType;
	}
	public boolean isVector() {return true;}
	public VectorType getVectorSelf() {return this;}
	public Iterator<Type> getSubtypes() {
		return new TypeIterator(this.elementType);
	}

	protected String toString(int depth) {
		if (depth==0) return "?";
		return "<" + this.size + " x " + this.elementType.toString(depth-1) + ">";
	}
	public boolean equalsType(Type o) {
		if (!(o.isComposite() && o.getCompositeSelf().isVector()))
			return false;
		return TypeEqualityCheck.equals(this, o);
	}
	protected int hashCode(int depth) {
		if (depth==0) return 0;
		return this.elementType.hashCode(depth-1)*503 + (int)this.size.signedValue()*499;
	}
}
