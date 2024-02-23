package llvm.types;

import java.util.Iterator;

import llvm.bitcode.UnsignedLong;

/**
 * Represents the array type in LLVM.
 * Arrays have a single element type, which can be anything.
 * The type also encodes a fixed size for the array.
 */
public class ArrayType extends CompositeType {
	protected final Type elementType;
	protected final UnsignedLong size;

	public ArrayType(Type _elementType, long _size) {
		this(_elementType, new UnsignedLong(_size));
	}
	public ArrayType(Type _elementType, UnsignedLong _size) {
		if (_elementType == null)
			throw new NullPointerException("elementType is null");
		this.elementType = _elementType;
		this.size = _size;
	}
	
	public Type getElementType() {return this.elementType;}
	public UnsignedLong getNumElements() {return this.size;}
	
	public boolean isElementIndexValid(int index) {
		return true;
	}
	public Type getElementType(int index) {
		if (!this.isElementIndexValid(index))
			throw new IndexOutOfBoundsException(""+index);
		return this.elementType;
	}
	public boolean isArray() {return true;}
	public ArrayType getArraySelf() {return this;}
	protected void ensureSized() {this.elementType.ensureSized();}
	public boolean hasTypeSize() {return this.elementType.hasTypeSize();}
	public long getTypeSize() {return this.elementType.getTypeSize() * this.size.signedValue();}
	public Iterator<Type> getSubtypes() {
		return new TypeIterator(this.elementType);
	}

	protected String toString(int depth) {
		if (depth==0) return "?";
		return "[" + this.size + " x " + this.elementType.toString(depth-1) + "]";
	}
	public boolean equalsType(Type o) {
		if (!(o.isComposite() && o.getCompositeSelf().isArray()))
			return false;
		return TypeEqualityCheck.equals(this, o);
	}
	protected int hashCode(int depth) {
		if (depth==0) return 0;
		return this.elementType.hashCode(depth-1)*491 + (int)this.size.signedValue()*487;
	}
}
