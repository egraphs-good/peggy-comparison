package llvm.types;

import java.util.Iterator;

/**
 * This is a type that implements the holder pattern. 
 * A holder type is a container that will eventually be filled by another Type
 * instance. It is convenient to have these during bitcode decoding, because
 * of all the forward references in the type tables.
 */
public class HolderType extends Type {
	protected Type inner;
	protected boolean makeSized;
	
	public HolderType() {
		this.makeSized = false;
	}
	
	public Type setInnerType(Type _inner) {
		if (this.inner == null) {
			if (this.makeSized)
				_inner.ensureSized();
			this.inner = _inner;
			return this.inner;
		} else
			throw new IllegalStateException();
	}
	
	public <T extends Type> T intern() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.<T>intern();
	}

	public boolean isHolder() {
		if (this.inner == null)
			return true;
		else
			return this.inner.isHolder();
	}
	public HolderType getHolderSelf() {
		if (this.inner == null)
			return this;
		else
			return this.inner.getHolderSelf();
	}

	public Iterator<Type> getSubtypes() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getSubtypes();
	}
	
	public boolean isPrimitive() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isPrimitive();
	}
	public boolean isDerived() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isDerived();
	}
	public boolean isFirstClass() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isFirstClass();
	}
	
	public boolean isInteger() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isInteger();
	}
	public IntegerType getIntegerSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getIntegerSelf();
	}
	
	public boolean isFloatingPoint() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isFloatingPoint();
	}
	public FloatingPointType getFloatingPointSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getFloatingPointSelf();
	}
	
	public boolean isFunction() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isFunction();
	}
	public FunctionType getFunctionSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getFunctionSelf();
	}

	public boolean isComposite() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isComposite();
	}
	public CompositeType getCompositeSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getCompositeSelf();
	}

	public boolean isOpaque() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isOpaque();
	}
	public OpaqueType getOpaqueSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getOpaqueSelf();
	}

	public boolean isLabel() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isLabel();
	}
	public boolean isVoid() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isVoid();
	}
	public boolean isMetadata() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isMetadata();
	}
	
	protected void ensureSized() {
		if (this.inner == null)
			this.makeSized = true;
		else
			this.inner.ensureSized();
	}
	public boolean hasTypeSize() {
		if (this.inner == null)
			return false;
		else
			return this.inner.hasTypeSize();
	}
	public long getTypeSize() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getTypeSize();
	}
	
	public boolean isVectorOfFloatingPoint() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isVectorOfFloatingPoint();
	}
	public boolean isVectorOfInteger() {
		if (this.inner == null)
			return false;
		else
			return this.inner.isVectorOfInteger();
	}

	protected String toString(int depth) {
		if (this.inner == null)
			return "<holder>";
		else
			return this.inner.toString(depth);
	}
	public boolean equalsType(Type o) {
		if (this.inner == null)
			return this == o;
		else
			return this.inner.equalsType(o);
	}
	protected int hashCode(int depth) {
		if (this.inner == null)
			return 54302985;
		else
			return this.inner.hashCode(depth);
	}
}
