package llvm.types;

/**
 * Parent class of all "composite" types in LLVM.
 * Composite types are those that reference other types,
 * such as struct, pointer, array, and vector.
 */
public abstract class CompositeType extends Type {
	public boolean isComposite() {return true;}
	public CompositeType getCompositeSelf() {return this;}
	public final boolean isFirstClass() {return true;}
	public final boolean isDerived() {return true;}
	
	public abstract boolean isElementIndexValid(int index);
	public abstract Type getElementType(int index);
	
	public boolean isPointer() {return false;}
	public PointerType getPointerSelf() {throw new UnsupportedOperationException();}
	
	public boolean isStructure() {return false;}
	public StructureType getStructureSelf() {throw new UnsupportedOperationException();}
	
	public boolean isVector() {return false;}
	public VectorType getVectorSelf() {throw new UnsupportedOperationException();}
	
	public boolean isArray() {return false;}
	public ArrayType getArraySelf() {throw new UnsupportedOperationException();}
}
