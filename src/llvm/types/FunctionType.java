package llvm.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a function type in LLVM.
 * Function types have a return type (may be VOID), a list of param types,
 * and may optionally be vararg.
 */
public class FunctionType extends Type {
	protected final Type returnType;
	protected final List<Type> paramTypes;
	protected final boolean vararg;
	
	private static boolean isValidReturnType(Type type) {
		if (type.isFirstClass())
			return true;
		else if (type.isVoid() || type.isOpaque())
			return true;
		else if (type.isComposite() && type.getCompositeSelf().isStructure()) {
			StructureType st = type.getCompositeSelf().getStructureSelf();
			if (st.getNumFields() == 0)
				return false;
			for (int i = 0; i < st.getNumFields(); i++) 
				if (!st.getFieldType(i).isFirstClass())
					return false;
			return true;
		} 
		else
			return false;
	}
	
	public FunctionType(Type _returnType, List<? extends Type> _paramTypes, boolean _vararg) {
		if (_returnType == null)
			throw new NullPointerException("returnType is null");
		if (!isValidReturnType(_returnType))
			throw new IllegalArgumentException("returnType is not a valid return type: " + _returnType);
		if (_paramTypes == null)
			throw new NullPointerException("paramTypes is null");
		for (Type t : _paramTypes) {
			if (!(t.isFirstClass() || t.isOpaque()))
				throw new IllegalArgumentException("paramType must be first class or opaque: " + t);
		}
		
		this.returnType = _returnType;
		this.paramTypes = new ArrayList<Type>(_paramTypes);
		this.vararg = _vararg;
	}
	
	public boolean isVararg() {return this.vararg;}
	public Type getReturnType() {return this.returnType;}
	public int getNumParams() {return this.paramTypes.size();}
	public Type getParamType(int i) {return this.paramTypes.get(i);}

	public Iterator<Type> getSubtypes() {
		List<Type> temp = new ArrayList<Type>(this.paramTypes);
		temp.add(this.returnType);
		return temp.iterator();
	}
	protected void ensureSized() {throw new IllegalStateException();}
	public boolean isFunction() {return true;}
	public FunctionType getFunctionSelf() {return this;}
	public boolean isDerived() {return true;}
	
	protected String toString(int depth) {
		if (depth==0) return "?";

		StringBuffer buffer = new StringBuffer(100);
		buffer.append(this.returnType.toString(depth-1));
		buffer.append(" (");
		boolean gotone = false;
		for (Type t : this.paramTypes) {
			if (gotone) buffer.append(", ");
			buffer.append(t.toString(depth-1));
			gotone = true;
		}
		if (this.vararg) {
			if (gotone) buffer.append(", ...");
			else buffer.append("...");
		}
		buffer.append(")");
		return buffer.toString();
	}
	public boolean equalsType(Type o) {
		if (!o.isFunction())
			return false;
		return TypeEqualityCheck.equals(this, o);
	}
	protected int hashCode(int depth) {
		if (depth==0) return 0;
		int result = this.returnType.hashCode(depth-1)*479 + (this.vararg ? 467 : 463);
		for (Type t : this.paramTypes) {
			result += t.hashCode(depth-1) * 461;
		}
		return result;
	}
}
