package llvm.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a struct type in LLVM.
 * A struct has a fixed number of heterogeneous field types.
 */
public class StructureType extends CompositeType {
	protected final boolean packed;
	protected final List<Type> fieldTypes;
	
	public StructureType(boolean _packed, List<? extends Type> _fieldTypes) {
		if (_fieldTypes == null)
			throw new NullPointerException("fieldTypes is null");
		
		this.packed = _packed;
		this.fieldTypes = new ArrayList<Type>(_fieldTypes);
	}
	
	public boolean isPacked() {return this.packed;}
	public int getNumFields() {return this.fieldTypes.size();}
	public Type getFieldType(int i) {return this.fieldTypes.get(i);}
	
	public boolean isElementIndexValid(int index) {
		return 0<=index && index<this.fieldTypes.size();
	}
	public Type getElementType(int index) {
		if (!isElementIndexValid(index))
			throw new IndexOutOfBoundsException(""+index);
		return this.fieldTypes.get(index);
	}
	public boolean isStructure() {return true;}
	public StructureType getStructureSelf() {return this;}
	protected void ensureSized() {
		for (Type t : this.fieldTypes) {
			t.ensureSized();
		}
	}
	public boolean hasTypeSize() {
		for (Type f : this.fieldTypes) {
			if (!f.hasTypeSize())
				return false;
		}
		return true;
	}
	public long getTypeSize() {
		long result = 0L;
		for (Type t : this.fieldTypes)
			result += t.getTypeSize();
		return result;
	}
	public Iterator<Type> getSubtypes() {
		return Collections.unmodifiableList(this.fieldTypes).iterator();
	}

	protected String toString(int depth) {
		if (depth==0) return "?";
		StringBuffer buffer = new StringBuffer(100);
		if (this.packed) buffer.append("<");
		buffer.append("{ ");
		boolean gotone = false;
		for (Type t : this.fieldTypes) {
			if (gotone) buffer.append(", ");
			buffer.append(t.toString(depth-1));
			gotone = true;
		}
		buffer.append(" }");
		if (this.packed) buffer.append(">");
		
		return buffer.toString();
	}
	public boolean equalsType(Type o) {
		if (!(o.isComposite() && o.getCompositeSelf().isStructure()))
			return false;
		return TypeEqualityCheck.equals(this, o);
	}
	protected int hashCode(int depth) {
		if (depth==0) return 0;
		int result = (this.packed ? 359 : 353);
		for (Type t : this.fieldTypes) {
			result += t.hashCode(depth-1)*349;
		}
		return result;
	}
}
