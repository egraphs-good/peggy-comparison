package llvm.instructions;

import java.util.*;

import llvm.types.CompositeType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.Value;

/**
 * This represents the GETELEMENTPTR instruction.
 * It takes a base pointer value and a list of index values.
 */
public class GEPInstruction extends Instruction {
	public static Type computeType(Type baseType, List<? extends Value> indexes) {
		if (!(baseType.isComposite() && baseType.getCompositeSelf().isPointer()))
			throw new IllegalArgumentException("Input type to GEP must be a pointer");
		Type i32 = Type.getIntegerType(32);
		
		int addressSpace = baseType.getCompositeSelf().getPointerSelf().getAddressSpace();
		int index = 0;
		
		if (indexes.size() == 0) // special case
			return baseType;

		while (index < indexes.size() && baseType.isComposite()) {
			CompositeType ctype = baseType.getCompositeSelf();
			if (ctype.isPointer() || ctype.isArray() || ctype.isVector()) {
				if (!indexes.get(index).getType().isInteger()) 
					throw new IllegalArgumentException("Indexes into pointers must be integer type: " + indexes.get(index));
				baseType = ctype.getElementType(0);
			} else if (ctype.isStructure()) {
				// index must be an i32 constant
				if (!indexes.get(index).getType().equals(i32))
					throw new IllegalArgumentException("Indexes into structure must be i32's: " + indexes.get(index));
				indexes.get(index).ensureConstant();
				if (!indexes.get(index).isInteger())
					throw new IllegalArgumentException("Indexes into structure must be constant integers: " + indexes.get(index));
				int bits = indexes.get(index).getIntegerSelf().getIntBits();
				baseType = ctype.getElementType(bits);
			} else {
				throw new RuntimeException("Forgot to handle: " + ctype);
			}
			
			index++;
		}
		if (index < indexes.size())
			throw new IllegalArgumentException("Too many indexes for GEP");
		
		return new PointerType(baseType, addressSpace);
	}
	
	protected final Value baseValue;
	protected final List<Value> indexes;
	protected final Type computedType;
	
	public GEPInstruction(Value _baseValue, List<? extends Value> _indexes) {
		this.baseValue = _baseValue;
		this.indexes = new ArrayList<Value>(_indexes);

		for (Value v : _indexes) {
			if (!v.getType().isInteger())
				throw new IllegalArgumentException("All indexes to GEP must be integers: " + v);
		}
		
		this.computedType = computeType(_baseValue.getType(), _indexes);
	}
	
	public boolean isInbounds() {return false;}
	
	public Value getBaseValue() {return this.baseValue;}
	public int getNumIndexes() {return this.indexes.size();}
	public Value getIndex(int i) {return this.indexes.get(i);}

	public Type getType() {return this.computedType;}
	public Iterator<? extends Value> getValues() {
		List<Value> temp = new ArrayList<Value>(this.indexes);
		temp.add(this.baseValue);
		return temp.iterator();
	}
	public Iterator<? extends Type> getTypes() {
		List<Type> temp = new ArrayList<Type>();
		temp.add(this.baseValue.getType());
		temp.add(this.computedType);
		for (Value v : this.indexes) {
			temp.add(v.getType());
		}
		return temp.iterator();
	}
	public boolean isGEP() {return true;}
	public GEPInstruction getGEPSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("getelementptr ( ").append(baseValue.toString());
		for (Value v : this.indexes) {
			buffer.append(", ");
			buffer.append(v.toString());
		}
		buffer.append(" )");
		return buffer.toString();
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isGEP())
			return false;
		GEPInstruction c = o.getGEPSelf();
		if (!(this.baseValue.equalsValue(c.baseValue) && this.indexes.size() == c.indexes.size()))
			return false;
		for (int i = 0; i < this.indexes.size(); i++) {
			if (!this.indexes.get(i).equalsValue(c.indexes.get(i)))
				return false;
		}
		return true;
	}
	public int hashCode() {
		int result = this.baseValue.hashCode()*91;
		for (Value v : this.indexes) {
			result += v.hashCode()*97;
		}
		return result;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newbase = this.baseValue.rewrite(old2new);
		List<Value> newindexes = new ArrayList<Value>(this.indexes.size());
		boolean gotone = (newbase != this.baseValue);
		for (Value i : this.indexes) {
			Value newi = i.rewrite(old2new);
			newindexes.add(newi);
			if (newi != i)
				gotone = true;
		}
		if (!gotone)
			return this;
		else
			return new GEPInstruction(newbase, newindexes);
	}
}
