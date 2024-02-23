package llvm.values;

import java.util.*;

import llvm.types.StructureType;

/**
 * This represents a constant struct value.
 * All the field values of a constant struct are themselves constants.
 */
public class ConstantStructureValue extends Value {
	protected final StructureType type;
	protected final List<Value> fieldValues;
	
	public ConstantStructureValue(StructureType _type, List<? extends Value> _fieldValues) {
		if (_type == null)
			throw new NullPointerException("type is null");
		if (_type.getNumFields() != _fieldValues.size())
			throw new IllegalArgumentException("wrong number of field values for structure");
		for (int i = 0; i < _fieldValues.size(); i++) {
			if (!_fieldValues.get(i).getType().equals(_type.getFieldType(i)))
				throw new IllegalArgumentException("wrong type for structure field value");
			_fieldValues.get(i).ensureConstant();
		}
		
		this.type = _type;
		this.fieldValues = new ArrayList<Value>(_fieldValues);
	}

	public int getNumFields() {return this.fieldValues.size();}
	public Value getFieldValue(int i) {return this.fieldValues.get(i);}

	public void ensureConstant() {
		for (Value f : this.fieldValues) {
			f.ensureConstant();
		}
	}
	public StructureType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return Collections.unmodifiableList(this.fieldValues).iterator();
	}
	public boolean isConstantStructure() {return true;}
	public ConstantStructureValue getConstantStructureSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("{ ");
		boolean gotone = false;
		for (Value v : this.fieldValues) {
			if (gotone) buffer.append(", ");
			buffer.append(v);
			gotone = true;
		}
		buffer.append(" }");
		return buffer.toString();
	}
	public boolean equalsValue(Value o) {
		if (!o.isConstantStructure())
			return false;
		ConstantStructureValue s = o.getConstantStructureSelf();
		return this.type.equalsType(s.type) && this.fieldValues.equals(s.fieldValues);
	}
	public int hashCode() {
		int result = this.type.hashCode()*19;
		for (Value v : this.fieldValues) {
			result += v.hashCode()*23;
		}
		return result;
	}

	protected ConstantStructureValue rewriteChildren(Map<Value,Value> old2new) {
		List<Value> newfields = new ArrayList<Value>(this.fieldValues.size());
		boolean gotone = false;
		for (Value old : this.fieldValues) {
			Value newvalue = old.rewrite(old2new);
			if (newvalue != old)
				gotone = true;
			newfields.add(newvalue);
		}
		return (gotone ? new ConstantStructureValue(this.type, newfields) : this);
	}
}
