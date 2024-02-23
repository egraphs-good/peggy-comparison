package llvm.values;

import java.util.*;

import llvm.bitcode.UnsignedLong;
import llvm.types.VectorType;

/**
 * This represents s constant vector value where every element value is explicitly
 * represented in a list.
 */
public class ConstantExplicitVectorValue extends ConstantVectorValue {
	protected final List<Value> elements;
	
	public ConstantExplicitVectorValue(VectorType _type, List<? extends Value> _elements) {
		super(_type);
		if (!new UnsignedLong(_elements.size()).equals(_type.getNumElements()))
			throw new IllegalArgumentException("Wrong number of elements for constant vector");
		for (Value v : _elements) {
			if (!v.getType().equals(_type.getElementType()))
				throw new IllegalArgumentException("Bad element type for constant vector: " + v.getType());
			v.ensureConstant();
		}
		this.elements = new ArrayList<Value>(_elements);
	}

	public Value getElement(int i) {return this.elements.get(i);}

	public void ensureConstant() {
		for (Value e : this.elements) {
			e.ensureConstant();
		}
	}
	public Iterator<? extends Value> getSubvalues() {
		return Collections.unmodifiableList(this.elements).iterator();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("< ");
		for (int i = 0; i < this.elements.size(); i++) {
			if (i>0) buffer.append(", ");
			buffer.append(this.elements.get(i));
		}
		buffer.append(" >");
		return buffer.toString();
	}
	public int hashCode() {
		int result = this.type.hashCode()*97;
		for (Value v : this.elements) {
			result += v.hashCode()*107;
		}
		return result;
	}
	
	protected ConstantVectorValue rewriteChildren(Map<Value,Value> old2new) {
		List<Value> newelements = new ArrayList<Value>(this.elements.size());
		boolean gotone = false;
		for (Value e : this.elements) {
			Value newe = e.rewrite(old2new);
			if (newe != e)
				gotone = true;
			newelements.add(newe);
		}
		if (gotone)
			return new ConstantExplicitVectorValue(this.getType(), newelements);
		else
			return this;
	}
}
