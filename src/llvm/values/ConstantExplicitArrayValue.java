package llvm.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import llvm.bitcode.UnsignedLong;
import llvm.types.ArrayType;

/**
 * This represents a constant array value where every element value is explicitly
 * represented in a list.
 */
public class ConstantExplicitArrayValue extends ConstantArrayValue {
	protected final List<Value> elements;
	
	public ConstantExplicitArrayValue(ArrayType _type, List<? extends Value> _elements) {
		super(_type);
		if (!new UnsignedLong(_elements.size()).equals( _type.getNumElements()))
			throw new IllegalArgumentException("Wrong number of elements for constant array");
		for (Value v : _elements) {
			if (!v.getType().equals(_type.getElementType()))
				throw new IllegalArgumentException("Bad element type for constant array: " + v.getType());
			v.ensureConstant();
		}
		this.elements = new ArrayList<Value>(_elements);
	}

	public Value getElement(int i) {return this.elements.get(i);}
	
	public void ensureConstant() {
		for (Value a : this.elements) {
			a.ensureConstant();
		}
	}
	public Iterator<? extends Value> getSubvalues() {
		return Collections.unmodifiableList(this.elements).iterator();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[ ");
		for (int i = 0; i < this.elements.size(); i++) {
			if (i>0) buffer.append(", ");
			buffer.append(this.elements.get(i));
		}
		buffer.append(" ]");
		return buffer.toString();
	}
	public int hashCode() {
		int result = this.type.hashCode()*257;
		for (Value v : this.elements) {
			result += v.hashCode()*337;
		}
		return result;
	}
	
	protected ConstantArrayValue rewriteChildren(Map<Value,Value> old2new) {
		List<Value> newelements = new ArrayList<Value>(this.elements.size());
		boolean gotone = false;
		for (Value e : this.elements) {
			Value newe = e.rewrite(old2new);
			if (newe != e)
				gotone = true;
			newelements.add(newe);
		}
		if (gotone)
			return new ConstantExplicitArrayValue(this.getType(), newelements);
		else
			return this;
	}
}
